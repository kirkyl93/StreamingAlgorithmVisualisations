package counting.distinctcounting;

import java.util.Random;

/** A class that chooses a hash function from a family of fourwise independent functions
 *
 */

public class FourwiseHash {

    private final long p = 887213367811L;
    private final long a;
    private final long b;
    private final long c;
    private final long d;

    // The constructor generates four random parameters between 0 and p
    public FourwiseHash() {
        Random rand = new Random();
        this.a = rand.nextLong(p);
        this.b = rand.nextLong(p);
        this.c = rand.nextLong(p);
        this.d = rand.nextLong(p);
    }

    // The hash function is in the form ax^3 + bx^2 + cx + d
    public long hash(long x) {
        return Math.abs( ((long) Math.pow(x * a, 3) + (long) Math.pow(x * b, 2) + (x * c) + d) % p);
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

    public long getC() {
        return c;
    }

    public long getD() {
        return d;
    }
}
