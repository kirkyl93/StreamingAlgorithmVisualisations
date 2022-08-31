package counting.distinctcounting;

import java.util.Random;

/** An abstract class for k-wise independent hash functions. Child classes are required to implement the hashFunction
 * method
 *
 */

public abstract class Hash {

    private final long p = 887213367811L;
    private final long a;
    private final long b;

    // The constructor generates two random parameters between 0 and p
    public Hash() {
        Random rand = new Random();
        this.a = rand.nextLong(p);
        this.b = rand.nextLong(p);
    }

    public abstract long hashFunction (long x);

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
