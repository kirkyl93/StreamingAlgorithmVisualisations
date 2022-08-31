package counting.frequencycounting;

import java.util.Arrays;

/**
 * An implementation of Deng and Rafiei's Count-Mean min algorithm. Algorithm references to be found in report.
 */

public class CountMeanMinSketch extends CountMinSketch {

    // Algorithm 4.6 - keep a tally of the total weights added
    long totalWeight = 0;

    // Basic constructor
    public CountMeanMinSketch(int d, int t) {
        super(d, t);
    }

    // This constructor is used when we want to create a new sketch with the same parameters
    public CountMeanMinSketch(CountMeanMinSketch cmms) {
        super(cmms);
    }

    // This constructor is used to create a new merged count-mean-min sketch
    public CountMeanMinSketch(CountMeanMinSketch cmms, long[][] matrix) {
        super(cmms, matrix);
        this.totalWeight = cmms.getTotalWeight();
    }

    // Algorithm 4.7
    public void update(long item, long weight) {
        for (int i = 0; i < d; i++) {
            int hashValue = (int) (hashFunctions[i].hashFunction(item) % t);
            matrix[i][hashValue] += weight;
        }
        //
        totalWeight += weight;
    }

    // Algorithm 4.8
    @Override
    public long query(long item) {

        long minimum = Long.MAX_VALUE;
        long[] estimatesWithNoise = new long[d];
        long noiseEstimate;
        for (int i = 0; i < d; i++) {
            int hashValue = (int) (hashFunctions[i].hashFunction(item) % t);
            minimum = Math.min(minimum, matrix[i][hashValue]);
            noiseEstimate = (totalWeight - matrix[i][hashValue]) / (t - 1);
            estimatesWithNoise[i] = matrix[i][hashValue] - noiseEstimate;
        }

        // Find the median
        Arrays.sort(estimatesWithNoise);
        long estimate = estimatesWithNoise[estimatesWithNoise.length / 2];

        if (estimate < 0) {
            return 0;
        }

        return Math.min(minimum, estimate);

    }

    public long getTotalWeight() {
        return totalWeight;
    }

    public CountMeanMinSketch merge(CountMeanMinSketch cmmms2) {

        long[][] mergedMatrix = super.mergeHelper(cmmms2);

        return new CountMeanMinSketch(this, mergedMatrix);
    }

}
