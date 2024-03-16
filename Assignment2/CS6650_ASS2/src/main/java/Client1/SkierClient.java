package Client1;

import java.util.concurrent.*;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class SkierClient {
    private static final int totalRequests = 200000;
    private static final int numThreads = 32; // change

    public static void main(String[] args) {
        // Initialize the RequestStats object to track and log request statistics
        final RequestStats stats = new RequestStats();

        // Create a thread pool with a fixed number of threads to send requests concurrently
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Initialize the API service and event generator
        SkierApiService apiService = new SkierApiService(stats);
        LiftRideEventGenerator generator = new LiftRideEventGenerator();

        // Print a start message indicating the number of threads
        System.out.println("Starting the client with a thread pool of " + numThreads + " threads.");

        // Record the start time for calculating total run time
        long startTime = System.currentTimeMillis();

        // Distribute the total number of requests evenly across the threads
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < totalRequests / numThreads; j++) {
                    // Generate a new lift ride event and post it via the API service
                    LiftRideEvent event = generator.generate();
                    apiService.postLiftRide(event);
                }
            });
        }

        // Shut down service
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            stats.shutdown();
        }

        // Calculate total run time and throughput
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double throughput = (double) totalRequests / (totalTime / 1000.0);

        writeThroughputLogToFile(stats);

        System.out.println("Total run time (wall time): " + totalTime + " ms");
        System.out.println("Total throughput in requests per second: " + throughput);
    }

    // Write the throughput data to a CSV file
    private static void writeThroughputLogToFile(RequestStats stats) {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get("throughput_over_time.csv")))) {
            out.println("Timestamp,Requests");
            // Write each log entry to the file
            stats.getThroughputLog().forEach(out::println);
        } catch (IOException e) {
            System.err.println("Error writing throughput log to file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
