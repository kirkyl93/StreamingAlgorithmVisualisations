package counting.distinctcounting;

import java.util.Random;

public class FourwiseHash {

    private final long p = 3727459247L;
    private final long a;
    private final long b;
    private final long c;
    private final long d;

    public FourwiseHash() {
        Random rand = new Random();
        this.a = rand.nextLong(p);
        this.b = rand.nextLong(p);
        this.c = rand.nextLong(p);
        this.d = rand.nextLong(p);
    }

    public long hash(long x) {
        return ((long) Math.pow(x * a, 3) + (long) Math.pow(x * b, 2) + (x * c) + d) % p;
    }

    public long getPrime() {
        return p;
    }
}
