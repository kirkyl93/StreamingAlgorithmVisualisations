package counting.frequencycounting;

public abstract class CountMinSketch {

    int d;
    int t;
    long[][] matrix;
    PairwiseHash[] hashFunctions;

    public CountMinSketch(int d, int t) {
        this.d = d;
        this.t = t;
        matrix = new long[d][t];
        hashFunctions = new PairwiseHash[d];
        for (int i = 0; i < d; i++) {
            hashFunctions[i] = new PairwiseHash();
        }
    }

    public abstract void update(long item, long weight);

    public long query(long item) {
        long minimum = Long.MAX_VALUE;
        for (int i = 0; i < d; i++) {
            int hashValue = (int) (hashFunctions[i].hash(item) % t);
            minimum = Math.min(minimum, matrix[i][hashValue]);
        }

        return minimum;
    }

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

