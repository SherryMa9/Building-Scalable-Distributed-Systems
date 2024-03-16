package Client1;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;

// Send lift ride events to a specified server
public class SkierApiService {
    private final HttpClient httpClient = HttpClient.newHttpClient(); // HTTP requests
    private final Gson gson = new Gson();
    private final RequestStats stats;  // Track the number of successful and failed requests

    // Track request outcomes
    public SkierApiService(RequestStats stats) {
        this.stats = stats;
    }

    // Post a LiftRideEvent to the server as a JSON payload
    public void postLiftRide(LiftRideEvent event) {
        final String serverUrl = "http://CS6650-40527052.us-west-2.elb.amazonaws.com";  // "http://34.217.23.41:8080/CS6650_ASS1_EC2_API/skiers";

        String json = gson.toJson(event);
        // Create an HTTP POST request with the JSON payload
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        // Retry mechanism
        int attempts = 0;
        boolean requestSuccessful = false;
        while (attempts < 5 && !requestSuccessful) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 201) {
                    stats.incrementSuccessfulRequests();
                    requestSuccessful = true;
                } else {
                    stats.incrementFailedRequests();
                    attempts++;
                }
            } catch (Exception e) {
                attempts++;
            }
        }

        if (!requestSuccessful) {
            System.out.println("Failed to process request after retries: " + json);
        }
    }
}
