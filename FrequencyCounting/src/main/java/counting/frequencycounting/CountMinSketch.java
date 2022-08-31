package counting.frequencycounting;

/**
 * This is an abstract class that sets up the basic features of a count-min sketch (matrix and set of hash functions).
 * This class is used as the parent class for the concrete implementations of different versions of the count-min sketch.
 */

public abstract class CountMinSketch {

    // Parameters of count-min sketch
    int d;
    int t;
    long[][] matrix;
    PairwiseHash[] hashFunctions;

    // Basic constructor
    public CountMinSketch(int d, int t) {
        this.d = d;
        this.t = t;
        matrix = new long[d][t];
        hashFunctions = new PairwiseHash[d];
        for (int i = 0; i < d; i++) {
            hashFunctions[i] = new PairwiseHash();
        }
    }

    // Constructor used to set up a new count-min sketch with the same parameters of an existing sketch
    public CountMinSketch(CountMinSketch cms) {
        this.d = cms.getD();
        this.t = cms.getT();
        matrix = new long[d][t];
        hashFunctions = cms.getHashFunctions();
    }

    // This constructor is called for merged count-min sketches
    public CountMinSketch(CountMinSketch cms, long[][] matrix) {
        this.d = cms.getD();
        this.t = cms.getT();
        this.matrix = matrix;
        hashFunctions = cms.getHashFunctions();
    }

    public abstract void update(long item, long weight);

    // Algorithm 4.4
    public long[][] mergeHelper(CountMinSketch cms2) {
        // Matrix size must be the same to merge two count-min sketches
        if (this.d != cms2.d || this.t != cms2.t) {
            throw new RuntimeException("To merge count-min sketches, the matrix size has to be the same");
        }

        // The sketches must also share the same hash functions
        for (int i = 0; i < d; i++) {
            PairwiseHash pairwiseHash1 = this.hashFunctions[i];
            PairwiseHash pairwiseHash2 = cms2.hashFunctions[i];

            if (pairwiseHash1.getA() != pairwiseHash2.getA() || pairwiseHash1.getB() != pairwiseHash2.getB() ||
            pairwiseHash1.getPrime() != pairwiseHash2.getPrime()) {
                throw new RuntimeException("To merge count-min sketches, they must share the same hash functions");
            }
        }

        // Simply add each cell of the matrix together and store in the merged matrix
        long[][] newMatrix = new long[d][t];
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < t; j++) {
                newMatrix[i][j] = this.getMatrix()[i][j] + cms2.getMatrix()[i][j];
            }
        }

        return newMatrix;
    }

    // Algorithm 4.3
    public long query(long item) {
        long minimum = Long.MAX_VALUE;
        for (int i = 0; i < d; i++) {
            int hashValue = (int) (hashFunctions[i].hashFunction(item) % t);
            minimum = Math.min(minimum, matrix[i][hashValue]);
        }

        return minimum;
    }

    public int getD() {
        return d;
    }

    public int getT() {
        return t;
    }

    public long[][] getMatrix() {
        return matrix;
    }

    public PairwiseHash[] getHashFunctions() {
        return hashFunctions;
    }

    // A simple function to display the values stored in the count-min sketch matrix
    public void showMatrix() {
        for (int i = 0; i < d; i++) {
            System.out.println();
            for (int j = 0; j < t; j++) {
                System.out.print(matrix[i][j] + " ");
            }
        }
        System.out.println();
    }
}

