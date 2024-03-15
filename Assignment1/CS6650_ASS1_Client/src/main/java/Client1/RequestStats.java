package Client1;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RequestStats {
    private AtomicInteger successfulRequests = new AtomicInteger(0);
    private AtomicInteger failedRequests = new AtomicInteger(0);
    private ConcurrentLinkedDeque<String> throughputLog = new ConcurrentLinkedDeque<>();

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public RequestStats() {
        // Schedule a task to log throughput every second
        scheduler.scheduleAtFixedRate(this::logThroughput, 1, 1, TimeUnit.SECONDS);
    }

    private void logThroughput() {
        // Current timestamp for the log entry
        long timestamp = System.currentTimeMillis() / 1000;
        // Calculate and reset the count of successful requests for the past second
        int count = successfulRequests.getAndSet(0);
        throughputLog.add(timestamp + "," + count);
    }

    public void incrementSuccessfulRequests() {
        successfulRequests.incrementAndGet();
    }

    public void incrementFailedRequests() {
        failedRequests.incrementAndGet();
    }

    public int getSuccessfulRequests() {
        throw new UnsupportedOperationException("This method is not supported in the modified version.");
    }

    public int getFailedRequests() {
        return failedRequests.get();
    }

    public ConcurrentLinkedDeque<String> getThroughputLog() {
        return throughputLog;
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
