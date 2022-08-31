package counting.frequencycounting;

public class CountMinSketchOriginal extends CountMinSketch {


    public CountMinSketchOriginal(int d, int t) {
        super(d, t);
    }

    public CountMinSketchOriginal(CountMinSketchOriginal cms) {
    super(cms);
    }

    public CountMinSketchOriginal(CountMinSketchOriginal cms, long[][] matrix) {
        super(cms, matrix);
    }


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

