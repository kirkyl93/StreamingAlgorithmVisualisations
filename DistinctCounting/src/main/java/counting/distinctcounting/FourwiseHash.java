package counting.distinctcounting;

import java.util.Random;

/** A class that chooses a hash function from a family of fourwise independent functions
 *
 */

public class FourwiseHash extends Hash {


    private final long c;
    private final long d;

    // The constructor generates four random parameters between 0 and p
    public FourwiseHash() {
        super();
        Random rand = new Random();
        this.c = rand.nextLong(super.getPrime());
        this.d = rand.nextLong(super.getPrime());
    }

    @Override
    // The hash function is in the form ax^3 + bx^2 + cx + d
    public long hashFunction(long x) {
        return Math.abs( ((long) Math.pow(x * super.getA(), 3) + (long) Math.pow(x * super.getB(), 2) + (x * c) + d) % super.getPrime());
    }

    public long getC() {
        return c;
    }

    public long getD() {
        return d;
    }
}
