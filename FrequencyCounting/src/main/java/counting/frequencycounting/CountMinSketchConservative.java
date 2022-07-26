package counting.frequencycounting;

public class CountMinSketchConservative extends CountMinSketch {

    public CountMinSketchConservative(int d, int t) {
        super(d, t);
    }

    public void update(long item, long weight) {
        long minMatrixValue = Long.MAX_VALUE;
        for (int i = 0; i < d; i++) {
            int hashValue = (int) (hashFunctions[i].hash(item) % t);
            minMatrixValue = Math.min(minMatrixValue, matrix[i][hashValue]);
        }

        for (int i = 0; i < d; i++) {
            int hashValue = (int) (hashFunctions[i].hash(item) % t);
            if (matrix[i][hashValue] - weight < minMatrixValue) {
                matrix[i][hashValue] = minMatrixValue + weight;
            }
        }
    }


}
