package counting.distinctcounting;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public abstract class KMV {

    private final TreeSet<Long> kMinimumHashValues;
    private final int kMinimumValues;

    // Algorithm 3.1: KMV: Initialise(k)
    public KMV(int kMinimumValues) {
        // The algorithm doesn't work for values less than or equal to 1
        if (kMinimumValues <= 1) {
            throw new RuntimeException("k has to be greater than 1");
        }
        this.kMinimumValues = kMinimumValues;
        kMinimumHashValues = new TreeSet<>();
    }

    // A constructor to instantiate a merged KMV
    public KMV(int kMinimumValues, TreeSet<Long> kMinimumHashValues) {
        this.kMinimumValues = kMinimumValues;
        this.kMinimumHashValues = kMinimumHashValues;
    }

    // Algorithm 3.2: KMV: Update(s)
    public void update(long item, long hashValue) {

        // Ignore duplicates
        if (kMinimumHashValues.contains(hashValue)) {
            return;
        }

        // Add the item's hash value to the tree if fewer than k items have been added
        if (kMinimumHashValues.size() < kMinimumValues) {
            kMinimumHashValues.add(hashValue);
            return;
        }

        // Only add the item's hash value if it is smaller than the largest element in the tree
        long biggestK = kMinimumHashValues.last();
        if (biggestK > hashValue) {
            kMinimumHashValues.remove(biggestK);
            kMinimumHashValues.add(hashValue);
        }
    }

    // Algorithm 3.3: KMV: Query()
    public long query(long prime) {

        // The algorithm is deterministic and offers exact answers when the size of the tree is smaller than k
        if (kMinimumHashValues.size() < kMinimumValues) {
            return kMinimumHashValues.size();
        }

        double area_on_line = (double) prime / kMinimumHashValues.last();
        return Math.round((kMinimumValues - 1) * area_on_line);
    }


    // Algorithm 3.4: KMV: Merge(KMV1, KMV2)
    public TreeSet<Long> mergeHelper(KMV kmv2) {

        // Set up a new tree for our merged KMV
        TreeSet<Long> newKMinimumValues = new TreeSet<>();

        // Store all of kmv1 and kmv2's hash values in a list. Because of the implementation of this method on
        // TreeSet structures, the values are stored in sorted, ascending order
        List<Long> kmv1HashValues = new ArrayList<>(this.getKMinimumHashValues());
        List<Long> kmv2HashValues = new ArrayList<>(kmv2.getKMinimumHashValues());


        // This simple algorithm is inspired by the merging of the sorted lists in mergesort. We continue to merge until
        // we've added k values to our newly established tree, or we've exhausted the items in the kmv1 and kmv2 lists.
        int kmv1Index = 0;
        int kmv2Index = 0;

        while (newKMinimumValues.size() < this.kMinimumValues && kmv1Index < kmv1HashValues.size()
                && kmv2Index < kmv2HashValues.size()) {
            if (kmv1HashValues.get(kmv1Index) < kmv2HashValues.get(kmv2Index)) {
                newKMinimumValues.add(kmv1HashValues.get(kmv1Index));
                kmv1Index++;
            }
            else {
                newKMinimumValues.add(kmv2HashValues.get(kmv2Index));
                kmv2Index++;
            }
        }

        while (newKMinimumValues.size() < this.kMinimumValues && kmv1Index < kmv1HashValues.size()) {
            newKMinimumValues.add(kmv1HashValues.get(kmv1Index));
            kmv1Index++;
        }

        while (newKMinimumValues.size() < this.getKMinimumValues() && kmv2Index < kmv2HashValues.size()) {
            newKMinimumValues.add(kmv2HashValues.get(kmv2Index));
            kmv2Index++;
        }

        // Return the merged KMV
        return newKMinimumValues;
    }

    public int getKMinimumValues() {
        return kMinimumValues;
    }


    public int getBytesUsed() {
        return kMinimumHashValues.size() * 8;
    }

    public TreeSet<Long> getKMinimumHashValues() {
        return kMinimumHashValues;
    }





}
