package counting.distinctcounting;

import java.util.TreeSet;

/** This class initialises a KMV instance which uses a pairwise hash function. The k minimum values are stored in a
 * binary search tree using Java's inbuilt tree class (red-black trees are used, guaranteeing O(log(n)) insertions,
 * deletions and queries (see parent KMV class). Algorithm references to be found in report
 *
 */

public class PairwiseKMV extends KMV {

    private final PairwiseHash hashFunction;

    // Algorithm 3.1: KMV: Initialise(k)
    public PairwiseKMV(int kMinimumValues) {
        super(kMinimumValues);
        hashFunction = new PairwiseHash();
    }

    // This constructor is used when we want to use the same hash function as another KMV. This is helpful when we
    // want to run multiple KMVs on different machines/threads and to later merge their results
    public PairwiseKMV(int kMinimumValues, PairwiseHash hashFunction) {
        super(kMinimumValues);
        this.hashFunction = hashFunction;
    }

    // A constructor to instantiate a merged KMV
    public PairwiseKMV(PairwiseKMV kmv, TreeSet<Long> kMinimumHashValues) {
        super(kmv.getKMinimumValues(), kMinimumHashValues);
        hashFunction = kmv.getHashFunction();
    }

    // Algorithm 3.2: KMV: Update(s)
    public void update(long item) {

        long hashValue = hashFunction.hashFunction(item);
        super.update(item, hashValue);
    }

    // Algorithm 3.3: KMV: Query()
    public long query() {

        return super.query(hashFunction.getPrime());
    }


    // Algorithm 3.4: KMV: Merge(KMV1, KMV2)
    public PairwiseKMV merge(PairwiseKMV kmv2) {

        // To merge two KMVs, they must share the same k value
        if (this.getKMinimumValues() != kmv2.getKMinimumValues()) {
            throw new RuntimeException("Can't merge two KMVs with different k values");
        }

        // To merge two KMVs, they must use the same hash function
        if (this.getHashFunction().getA() != kmv2.getHashFunction().getA() ||
                this.getHashFunction().getB() != kmv2.getHashFunction().getB() ||
                this.getHashFunction().getPrime() != kmv2.getHashFunction().getPrime()) {

            throw new RuntimeException("Can't merge two KMVs with different hash functions");
        }

        TreeSet<Long> newKMinimumValues = super.mergeHelper(kmv2);

        // Return the merged KMV
        return new PairwiseKMV(this, newKMinimumValues);
    }


    public PairwiseHash getHashFunction() {
        return hashFunction;
    }




}