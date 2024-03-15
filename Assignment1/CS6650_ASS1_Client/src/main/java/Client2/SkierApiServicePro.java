package Client2;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ConcurrentLinkedQueue;

import Client1.LiftRideEvent;
import com.google.gson.Gson;

// A service class to handle sending lift ride events to the server and recording latency
public class SkierApiServicePro {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String serverUrl = "http://52.24.34.169:8080/CS6650_ASS1_EC2_API/skiers";
    private final ConcurrentLinkedQueue<LatencyRecord> latencyRecords; // Queue to store latency records

    // Constructor to initialize the service with a queue for latency records
    public SkierApiServicePro(ConcurrentLinkedQueue<LatencyRecord> latencyRecords) {
        this.latencyRecords = latencyRecords;
    }

    public void postLiftRide(LiftRideEvent event) {
        String json = gson.toJson(event);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        long startTime = System.currentTimeMillis();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long endTime = System.currentTimeMillis();
            // Add a new LatencyRecord to the queue.
            latencyRecords.add(new LatencyRecord(startTime, "POST", endTime - startTime, response.statusCode()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
