package Client2;

import Client1.LiftRideEvent;
import Client1.LiftRideEventGenerator;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SkierClient2 {
    private static final int totalRequests = 200000;
    private static final int numThreads = 32;   // change

    // hold latency records for each request
    private static final ConcurrentLinkedQueue<LatencyRecord> latencyRecords = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);   // synchronization
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            executor.execute(new RequestSender(latch));
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdown();

        long endTime = System.currentTimeMillis();
        printStatsAndWriteCSV(latencyRecords, startTime, endTime);
    }

    private static void printStatsAndWriteCSV(ConcurrentLinkedQueue<LatencyRecord> latencyRecords, long startTime, long endTime) {
        List<Long> latencies = latencyRecords.stream()
                .map(LatencyRecord::getLatency)
                .sorted()
                .collect(Collectors.toList());

        double mean = latencies.stream().mapToDouble(Long::doubleValue).average().orElse(0.0);
        long median = latencies.get(latencies.size() / 2);
        long p99 = latencies.get((int) (latencies.size() * 0.99) - 1);
        long min = latencies.get(0);
        long max = latencies.get(latencies.size() - 1);
        double throughput = (double) totalRequests / ((endTime - startTime) / 1000.0);

        System.out.println("Mean response time: " + mean + " ms");
        System.out.println("Median response time: " + median + " ms");
        System.out.println("P99 response time: " + p99 + " ms");
        System.out.println("Min response time: " + min + " ms");
        System.out.println("Max response time: " + max + " ms");
        System.out.println("Throughput: " + throughput + " requests/sec");

        // Write latency and throughput data to CSV
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get("latency_throughput_data.csv")))) {
            writer.println("StartTimeMs,RequestType,LatencyMs,ResponseCode,ThroughputRequestsPerSec");
            latencyRecords.forEach(record -> writer.println(record.toString() + "," + throughput));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class RequestSender implements Runnable {
        private final CountDownLatch latch;

        RequestSender(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            LiftRideEventGenerator generator = new LiftRideEventGenerator();
            SkierApiServicePro apiService = new SkierApiServicePro(latencyRecords);

            for (int i = 0; i < totalRequests / numThreads; i++) {
                LiftRideEvent event = generator.generate();
                apiService.postLiftRide(event);
            }

            latch.countDown();
        }
    }
}
