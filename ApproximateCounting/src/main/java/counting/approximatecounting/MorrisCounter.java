package counting.approximatecounting;

import java.util.Random;

// The MorrisCounter extends the idea of using randomisation to reduce the space required to store approximations
//  of our counter.

public class MorrisCounter implements Counter {

    long timesUpdated = 0;
    private final double b;
    Random rand = new Random();

    public MorrisCounter(double b) {
        if (b <= 1) {
            throw new RuntimeException("Please provide a value > 1 for b");
        }
        this.b = b;
    }

    public void update() {
        double y = rand.nextDouble();
        if (y < (Math.pow(b, -timesUpdated))) {
            timesUpdated++;
        }
    }

    public void merge(MorrisCounter mc) {
        if (mc.getB() != b) {
            System.err.println("Only Morris Counters with the same b values can be merged");
            return;
        }

        long mostUpdates = Math.max(timesUpdated, mc.timesUpdated);
        long leastUpdates = Math.max(timesUpdated, mc.timesUpdated);

        for (long i = 0; i < leastUpdates; i++) {
            double y = rand.nextDouble();
            if (y < Math.pow(b, i - mostUpdates)) {
                mostUpdates++;
            }
        }

        timesUpdated = mostUpdates;

    }

    public long query() {
        return Math.round((Math.pow(b, timesUpdated) - 1) / (b - 1));
    }

    public long getTimesUpdated() {
        return timesUpdated;
    }

    public double getB() {
        return b;
    }
}
