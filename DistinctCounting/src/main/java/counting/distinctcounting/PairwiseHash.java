package counting.distinctcounting;

import java.util.Random;

public class PairwiseHash {

    private final long p = 13204527053L;
    private final long a;
    private final long b;

    public PairwiseHash() {
        Random rand = new Random();
        this.a = rand.nextLong(p);
        this.b = rand.nextLong(p);
    }

    public long hash(long x) {
        return (a * x + b) % p;
    }

    public long getPrime() {
        return p;
    }
}
