package counting.distinctcounting;

import java.util.TreeSet;

public class FourwiseKMV {


    private final TreeSet<Long> kMinimumHashValues = new TreeSet<>();
    private final int kMinimumValues;
    private final FourwiseHash hashFunction = new FourwiseHash();

    public FourwiseKMV(int kMinimumValues) {
        this.kMinimumValues = kMinimumValues;
    }

    public void update(long item) {

        long hashValue = hashFunction.hash(item);

        if (kMinimumHashValues.contains(hashValue)) {
            return;
        }

        if (kMinimumHashValues.size() < kMinimumValues) {
            kMinimumHashValues.add(hashValue);
            return;
        }

        long biggestK = kMinimumHashValues.last();
        if (biggestK > hashValue) {
            kMinimumHashValues.remove(biggestK);
            kMinimumHashValues.add(hashValue);
        }
    }

    public long query() {
        if (kMinimumHashValues.size() < kMinimumValues) {
            return kMinimumHashValues.size();
        }

        double area_on_line = (double) hashFunction.getPrime() / kMinimumHashValues.last();
        return Math.round((kMinimumValues - 1) * area_on_line);
    }

    public int getBytesUsed() {
        return kMinimumHashValues.size() * 8;
    }
}

