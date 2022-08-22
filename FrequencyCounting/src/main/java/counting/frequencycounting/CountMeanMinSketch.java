package counting.frequencycounting;

import java.util.Arrays;

public class CountMeanMinSketch extends CountMinSketch {

    long totalWeight = 0;

    public CountMeanMinSketch(int d, int t) {
        super(d, t);
    }

    public void update(long item, long weight) {
        for (int i = 0; i < d; i++) {
            int hashValue = (int) (hashFunctions[i].hash(item) % t);
            matrix[i][hashValue] += weight;
        }

        totalWeight += weight;
    }

    @Override
    public long query(long item) {

        long minimum = Long.MAX_VALUE;
        long[] estimatesWithNoise = new long[d];
        long noiseEstimate;
        for (int i = 0; i < d; i++) {
            int hashValue = (int) (hashFunctions[i].hash(item) % t);
            minimum = Math.min(minimum, matrix[i][hashValue]);
            noiseEstimate = (totalWeight - matrix[i][hashValue]) / (t - 1);
            estimatesWithNoise[i] = matrix[i][hashValue] - noiseEstimate;
        }

        Arrays.sort(estimatesWithNoise);
        long estimate = estimatesWithNoise[estimatesWithNoise.length / 2];

        if (estimate < 0) {
            return 0;
        }

        return Math.min(minimum, estimate);


    }

}
