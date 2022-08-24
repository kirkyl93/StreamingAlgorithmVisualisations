package counting.approximatecounting;

/** The simplest count possible. When using the long primitive, we can store a maximum value of 2^63 - 1
 */

public class BasicCounter implements Counter {

    private long count;

    // Standard constructor
    public BasicCounter() {
        count = 0;
    }

    // Constructor used when merging counters
    public BasicCounter(long count) {
        this.count = count;
    }

    // No probability is used here. Count is always incremented
    public void update() {
        count += 1;
    }

    public long query() {
        return count;
    }


    public BasicCounter merge(BasicCounter bc) {
        return new BasicCounter(this.getCount() + bc.getCount());
    }

    public long getCount() {
        return count;
    }
}
