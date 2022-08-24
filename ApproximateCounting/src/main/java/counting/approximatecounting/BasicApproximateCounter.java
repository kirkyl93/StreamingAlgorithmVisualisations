package counting.approximatecounting;

import java.util.Random;

/** This counter introduces an element of randomness (and therefore approximation) to save space. Here, the user passes
 * a parameter, b, which controls the update probability. This number has to be > 1 for the algorithm to not become
 * a BasicCounter. Algorithm references to be found in report.
 */

public class BasicApproximateCounter implements Counter {

    long count = 0;
    double b;
    double updateProbability;
    Random rand = new Random();


    // Algorithm 2.1: BasicApproximateCounter: Initialise(b)
    public BasicApproximateCounter(double b) {
        if (b <= 1) {
            throw new RuntimeException("Please provide a value > 1 for b");
        }
        this.b = b;
        updateProbability = 1 / b;
    }

    /* This constructor is used when merging two BasicApproximateCounters. It is instantiated with the sum of the counts
     of the two counters being merged.
     */
    public BasicApproximateCounter(double b, long sumCount) {
        this(b);
        count = sumCount;
    }


    // Algorithm 2.2: BasicApproximateCounter: Update()
    public void update() {
        double y = rand.nextDouble();
        if (y < updateProbability) {
            count += 1;
        }
    }

    // Algorithm 2.4: BasicApproximateCounter: Merge(c1, c2)
    public BasicApproximateCounter merge(BasicApproximateCounter bac) {

        // Merges only work when the two counters have the same update probability.
        if (bac.getB() != b) {
            throw new RuntimeException("Can't merge two BasicApproximateCounters with two different b values");
        }

        // Return a new counter which is instantiated with the sum of the two counters' counts.
        return new BasicApproximateCounter(b, this.getCount() + bac.getCount());
    }

    // Algorithm 2.3: Query()
    public long query() {
        return Math.round(count * b);
    }

    public double getB() {
        return b;
    }

    public long getCount() {
        return count;
    }

}
