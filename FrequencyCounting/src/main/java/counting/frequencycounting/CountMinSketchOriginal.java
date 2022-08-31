package counting.frequencycounting;

public class CountMinSketchOriginal extends CountMinSketch {

    // Basic constructor
    public CountMinSketchOriginal(int d, int t) {
        super(d, t);
    }

    // This constructor is used when we want to create a new sketch with the same parameters as an existing sketch
    public CountMinSketchOriginal(CountMinSketchOriginal cms) {
    super(cms);
    }

    // This constructor is used to create a new merged count min sketch original
    public CountMinSketchOriginal(CountMinSketchOriginal cms, long[][] matrix) {
        super(cms, matrix);
    }


    // Algorithm 4.2
    public void update(long item, long weight) {
        for (int i = 0; i < d; i++) {
            int hashValue = (int) (hashFunctions[i].hashFunction(item) % t);
            matrix[i][hashValue] += weight;
        }
    }

    public CountMinSketchOriginal merge(CountMinSketchOriginal cms2) {

        long[][] mergedMatrix = super.mergeHelper(cms2);

        return new CountMinSketchOriginal(this, mergedMatrix);
    }
}

