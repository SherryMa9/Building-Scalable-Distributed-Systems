package Client1;

public class LiftRideEvent {
    private final int skierID;
    private final int resortID;
    private final int liftID;
    private final String seasonID;
    private final String dayID;
    private final int time;

    // Constructor to initialize a LiftRideEvent with all necessary attributes
    public LiftRideEvent(int skierID, int resortID, int liftID, String seasonID, String dayID, int time) {
        this.skierID = skierID;
        this.resortID = resortID;
        this.liftID = liftID;
        this.seasonID = seasonID;
        this.dayID = dayID;
        this.time = validateTime(time);
    }

    // Throws an IllegalArgumentException if the time is outside this range
    private int validateTime(int time) {
        if (time < 1 || time > 360) {
            throw new IllegalArgumentException("Invalid time: " + time);
        }
        return time;
    }

    @Override
    public String toString() {
        return String.format("LiftRideEvent{skierID=%d, resortID=%d, liftID=%d, seasonID='%s', dayID='%s', time=%d}", skierID, resortID, liftID, seasonID, dayID, time);
    }
}
