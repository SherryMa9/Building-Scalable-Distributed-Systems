package Client;

import java.util.concurrent.ThreadLocalRandom;

public class LiftRideEventGenerator {

    // Generates a random LiftRideEvent with randomized attributes within specified ranges
    public LiftRideEvent generate() {
        return new LiftRideEvent(
                ThreadLocalRandom.current().nextInt(1, 100001), // skierId
                ThreadLocalRandom.current().nextInt(1, 11),     // resortId
                ThreadLocalRandom.current().nextInt(1, 41),     // liftId
                "2024",                                         // seasonId
                "1",                                            // dayId
                ThreadLocalRandom.current().nextInt(1, 361)     // time
        );
    }
}

