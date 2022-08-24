package counting.approximatecounting;

import java.util.Random;

/** The MorrisCounter uses randomisation to reduce the space required to store approximations of a counter.
 * First published by Morris in 1978, it is sometimes considered to be the first non-trivial streaming algorithm.
 * Algorithm references to be found in report.
 */

public class MorrisCounter implements Counter {

    long count;
    private final double b;
    Random rand = new Random();

    // Algorithm 2.1: BasicApproximateCounter: Initialise(b) - Morris Counter uses the same algorithm
    public MorrisCounter(double b) {
        if (b <= 1) {
            throw new RuntimeException("Please provide a value > 1 for b");
        }
        this.b = b;
        count = 0;
    }

    /* This constructor is used when merging two MorrisCounters. It is instantiated with the maximum count of the two
     * counters being merged
     */
    public MorrisCounter(double b, long maxCount) {
        this(b);
        count = maxCount;
    }

    // Algorithm 2.5: MorrisCounter: Update()
    public void update() {
        // randomly generate a real number between 0 and 1 (line 1)
        double y = rand.nextDouble();
        // Update y if condition met (lines 2-3)
        if (y < (Math.pow(b, -count))) {
            count++;
        }
    }

    // Algorithm 2.7: MorrisCounter: Merge(c1, c2)
    public MorrisCounter merge(MorrisCounter mc) {
        // Check that both MorrisCounters share the same base
        if (mc.getB() != this.b) {
            throw new RuntimeException("Can't merge two Morris Counters with two different b values");
        }

        long biggestCount = Math.max(this.count, mc.count);
        long smallestCount = Math.min(this.count, mc.count);

        // Set up a new MorrisCounter and initialise it with the biggest count of the two counters
        MorrisCounter mergedMorrisCounter = new MorrisCounter(this.b, biggestCount);

        for (long i = 0; i < smallestCount; i++) {
            double y = rand.nextDouble();
            if (y < Math.pow(b, i - mergedMorrisCounter.count)) {
                mergedMorrisCounter.count++;
            }
        }

        return mergedMorrisCounter;

    }

    // Algorithm 2.6: MorrisCounter: Query()
    public long query() {
        return Math.round((Math.pow(b, count) - 1) / (b - 1));
    }


    public long getCount() {
        return count;
    }

    public double getB() {
        return b;
    }
}
