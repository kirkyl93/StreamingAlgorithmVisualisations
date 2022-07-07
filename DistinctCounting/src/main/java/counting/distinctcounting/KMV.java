package counting.distinctcounting;

import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

public class KMV {

    private final PriorityQueue<Long> minimumHashValues = new PriorityQueue<>(Collections.reverseOrder());
    private final HashSet<Long> hashValues = new HashSet<>();
    private final int kMinimumValues;
    private final FourwiseHash hashFunction = new FourwiseHash();

    public KMV(int kMinimumValues) {
        this.kMinimumValues = kMinimumValues;
    }

    public void update(long item) {

        long hashValue = hashFunction.hash(item);

        if (hashValues.contains(hashValue)) {
            return;
        }

        if (minimumHashValues.size() < kMinimumValues) {
            minimumHashValues.add(hashValue);
            hashValues.add(hashValue);
            return;
        }

        long biggestK = minimumHashValues.peek();
        if (biggestK > hashValue) {
            minimumHashValues.poll();
            hashValues.remove(biggestK);

            minimumHashValues.add(hashValue);
            hashValues.add(hashValue);
        }
    }

    public long query() {
        if (minimumHashValues.size() < kMinimumValues) {
            return minimumHashValues.size();
        }

        double area_on_line = (double) hashFunction.getPrime() / minimumHashValues.peek();
        return Math.round((kMinimumValues - 1) * area_on_line);
    }
}