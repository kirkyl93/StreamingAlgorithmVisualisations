package counting.distinctcounting;

import java.util.Random;

/** A class that chooses a hash function from a family of pairwise independent functions
 *
 */

public class PairwiseHash {

    private final long p = 887213367811L;
    private final long a;
    private final long b;

    // The constructor generates two random parameters between 0 and p
    public PairwiseHash() {
        Random rand = new Random();
        this.a = rand.nextLong(p);
        this.b = rand.nextLong(p);
    }

    // The hash function is in the form ax + b
    public long hash(long x) {
        return Math.abs(a * x + b) % p;
    }

    public long getPrime() {
        return p;
    }

    public long getA() {
        return a;
    }

    public long getB() {
        return b;
    }
}
