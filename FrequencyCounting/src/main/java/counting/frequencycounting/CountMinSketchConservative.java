package counting.frequencycounting;

public class CountMinSketchConservative extends CountMinSketch {


    // Basic constructor
    public CountMinSketchConservative(int d, int t) {
        super(d, t);
    }

    // This constructor is used when we want to create a new sketch with the same parameters
    public CountMinSketchConservative(CountMinSketchConservative cmsConservative) {
        super(cmsConservative);
    }

    // This constructor is used to create a new merged count-mean-min sketch
    public CountMinSketchConservative(CountMinSketchConservative cmsConservative, long[][] matrix) {
        super(cmsConservative, matrix);
    }

    // Algorithm 4.5
    public void update(long item, long weight) {
        long minMatrixValue = Long.MAX_VALUE;
        for (int i = 0; i < d; i++) {
            int hashValue = (int) (hashFunctions[i].hashFunction(item) % t);
            minMatrixValue = Math.min(minMatrixValue, matrix[i][hashValue]);
        }

        for (int i = 0; i < d; i++) {
            int hashValue = (int) (hashFunctions[i].hashFunction(item) % t);
            if (matrix[i][hashValue] - weight < minMatrixValue) {
                matrix[i][hashValue] = minMatrixValue + weight;
            }
        }
    }

    public CountMinSketchConservative merge(CountMinSketchConservative cms2) {

        long[][] mergedMatrix = super.mergeHelper(cms2);

        return new CountMinSketchConservative(this, mergedMatrix);
    }


}
