package Client;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SkierClient {

    private static final String BASE_URL = "http://CS6650-40527052.us-west-2.elb.amazonaws.com/CS6650_Servlet/skiers";
    private static final int NUM_THREADS = 10;
    private static final int REQUESTS_PER_THREAD = 120;
    private static final Gson gson = new Gson();
    private static final LiftRideEventGenerator generator = new LiftRideEventGenerator();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        AtomicInteger successfulRequests = new AtomicInteger();
        AtomicInteger failedRequests = new AtomicInteger();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(() -> {
                for (int j = 0; j < REQUESTS_PER_THREAD; j++) {
                    // Generate a new LiftRideEvent for each request
                    LiftRideEvent event = generator.generate();
                    String jsonPayload = gson.toJson(event);

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(BASE_URL))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                            .build();

                    try {
                        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                        if (response.statusCode() == 200 || response.statusCode() == 201) {
                            successfulRequests.incrementAndGet();
                        } else {
                            failedRequests.incrementAndGet();
                        }

                        // Print response status and body for debugging
//                        System.out.println("Response status: " + response.statusCode());
//                        System.out.println("Response body: " + response.body());
                    } catch (Exception e) {
                        failedRequests.incrementAndGet();
                        e.printStackTrace();
                    }
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
                System.out.println("1");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.out.println("2");
            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + "ms");
        System.out.println("Successful requests: " + successfulRequests.get());
        System.out.println("Failed requests: " + failedRequests.get());
    }
}
