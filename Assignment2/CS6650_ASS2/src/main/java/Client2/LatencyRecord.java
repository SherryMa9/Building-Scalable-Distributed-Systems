package Client2;

// Represent the latency and other details of each request
public class LatencyRecord {
    private final long startTime;
    private final String requestType;
    private final long latency;
    private final int responseCode;

    public LatencyRecord(long startTime, String requestType, long latency, int responseCode) {
        this.startTime = startTime;
        this.requestType = requestType;
        this.latency = latency;
        this.responseCode = responseCode;
    }

    public long getLatency() {
        return latency;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getRequestType() {
        return requestType;
    }

    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public String toString() {
        return startTime + "," + requestType + "," + latency + "," + responseCode;
    }
}
