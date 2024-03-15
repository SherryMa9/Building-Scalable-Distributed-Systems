package Servlet;

public class SkierRequest {
    private String skierID;
    private String resortID;
    private String liftID;
    private String seasonID;
    private String dayID;
    private String time;

    // Constructor
    public SkierRequest() {
        // default constructor
    }

    // Getters and setters
    public String getSkierId() {
        return skierID;
    }

    public void setSkierId(String skierID) {
        this.skierID = skierID;
    }

    public String getResortId() {
        return resortID;
    }

    public void setResortId(String resortID) {
        this.resortID = resortID;
    }

    public String getLiftId() {
        return liftID;
    }

    public void setLiftId(String liftID) {
        this.liftID = liftID;
    }

    public String getSeasonId() {
        return seasonID;
    }

    public void setSeasonId(String seasonID) {
        this.seasonID = seasonID;
    }

    public String getDayId() {
        return dayID;
    }

    public void setDayId(String dayID) {
        this.dayID = dayID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

