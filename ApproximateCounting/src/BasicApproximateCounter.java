import java.util.Random;

// This counter introduces an element of randomness (and therefore approximation) to save space.
// Here, the user enters the expected iterations per update. This number has to be > 1 for the algorithm to not
// become a BasicCounter.

public class BasicApproximateCounter {

    long timesUpdated = 0;
    double expectedIterationsPerUpdate;
    double updateProbability;
    Random rand = new Random();

    public BasicApproximateCounter(double expectedIterationsPerUpdate) {
        if (expectedIterationsPerUpdate <= 1) {
            throw new RuntimeException("An update value <= 1 becomes a BasicCounter - there is no randomness here");
        }
        this.expectedIterationsPerUpdate = expectedIterationsPerUpdate;
        updateProbability = 1 / expectedIterationsPerUpdate;
    }

    public void update() {
        double y = rand.nextDouble();
        if (y < updateProbability) {
            timesUpdated += 1;
        }
    }

    public void merge(BasicApproximateCounter bac) {

        // Merges only work when the two counters have the same update probability.
        if (bac.getExpectedIterationsPerUpdate() != expectedIterationsPerUpdate) {
            throw new RuntimeException("The counter update values must have the same update probability");
        }

        timesUpdated += bac.getTimesUpdated();

    }


    public long getCount() {
        return Math.round(timesUpdated * expectedIterationsPerUpdate);
    }

    public double getExpectedIterationsPerUpdate() {
        return expectedIterationsPerUpdate;
    }

    public long getTimesUpdated() {
        return timesUpdated;
    }

}
