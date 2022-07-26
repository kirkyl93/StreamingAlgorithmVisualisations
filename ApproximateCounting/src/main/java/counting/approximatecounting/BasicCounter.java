package counting.approximatecounting;

// The simplest count possible. When using the long primitive, we can store a maximum value of 2^63 - 1

public class BasicCounter implements Counter {

    private long count = 0;

    public void update() {
        count += 1;
    }

    public long query() {
        return count;
    }

    public void merge(BasicCounter bc) {
        count += bc.query();
    }
}
