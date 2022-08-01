package counting.distinctcounting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class ComparingHashFunctionsKMV extends Application {

    @Override
    public void start(Stage stage) {

        Random rand = new Random();

        // Set the number of items we will store in our priority queue (the Kth value will be the item at the top
        // of the heap). The bigger this value, the more accurate our algorithm should be.
        final int K_VALUE = 2;

        // Set the number of KMVs we run simultaneously. We can use a higher value to get a better idea of the average
        // performance of our algorithm.
        final int NUMBER_OF_KMVS = 1;

        // Set the number of distinct items we will count to until the algorithm terminates.
        final long DISTINCT_COUNT = 1200000000000L;

        // Set the max number that our random number generator can generate. In order for the algorithm to work, this
        // has to be set higher than the DISTINCT_COUNT number.
        final long UPPER_LIMIT_NUM_TO_ADD = 100000000000000L;

        // Set the number of updates made to our KMVs before refreshing the graph visualisation. The smaller this is,
        // the more detail that can be seen in the results. However, it will take the program much longer to arrive at
        // large count values.
        final int UPDATES_PER_FRAME = 100000;


        // Prepare line chart
        stage.setTitle("Comparing hash functions - KMV");
        final NumberAxis distinctItems = new NumberAxis();
        final NumberAxis algorithmEstimate = new NumberAxis();
        distinctItems.setLabel("Distinct Items");
        algorithmEstimate.setLabel("Algorithm percentage error");
        final LineChart<Number, Number> LINE_CHART = new LineChart<>(distinctItems, algorithmEstimate);
        LINE_CHART.setTitle("Comparing hash functions - KMV");
        LINE_CHART.setAnimated(false);
        LINE_CHART.setCreateSymbols(false);

        // Create array lists to store our hash functions and add hash functions
        ArrayList<PairwiseKMV> pairwiseKMVs = new ArrayList<>();
        ArrayList<FourwiseKMV> fourwiseKMVs = new ArrayList<>();

        for (int j = 0; j < NUMBER_OF_KMVS; j++) {
            pairwiseKMVs.add(new PairwiseKMV(K_VALUE));
            fourwiseKMVs.add(new FourwiseKMV(K_VALUE));
        }

        // Add lines on line chart for pairwise and fourwise hash functions
        XYChart.Series<Number, Number> pairWise = new XYChart.Series<>();
        pairWise.setName("Pairwise hash");
        LINE_CHART.getData().add(pairWise);

        XYChart.Series<Number, Number> fourWise = new XYChart.Series<>();
        fourWise.setName("Fourwise hash");
        LINE_CHART.getData().add(fourWise);


        // Set up basic true distinct count
        BasicDistinctCountingHash trueDistinctCount = new BasicDistinctCountingHash();

        new AnimationTimer() {
            private long currentDistinctCount;

            @Override
            public void handle(long current) {

                // Terminate when our current count equals the count limit we set earlier
                if (currentDistinctCount > DISTINCT_COUNT) {
                    return;
                }

                // Initialse variables we will use for the sum of percentage errors across our KMVs
                double pairwiseErrorPercentage = 0;
                double fourwiseErrorPercentage = 0;

                // Generate a list of random numbers to be added to our KMVs
                ArrayList<Long> randomNumbersToAdd = new ArrayList<>();
                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    randomNumbersToAdd.add((rand.nextLong(UPPER_LIMIT_NUM_TO_ADD)));
                }

                // Query our KMVs and add error percentage to our error percentage sums
                for (int i = 0; i < NUMBER_OF_KMVS; i++) {
                    PairwiseKMV pairwiseKMV = pairwiseKMVs.get(i);
                    FourwiseKMV fourwiseKMV = fourwiseKMVs.get(i);
                    long pairwiseEstimate = pairwiseKMV.query();
                    long fourwiseEstimate = fourwiseKMV.query();
                    long distinctCount = trueDistinctCount.query();
                    if (distinctCount > 0) {
                        pairwiseErrorPercentage += (double) Math.abs(distinctCount - pairwiseEstimate) / distinctCount * 100;
                        fourwiseErrorPercentage += (double) Math.abs(distinctCount - fourwiseEstimate) / distinctCount * 100;
                    }

                    // Update KMVs with already generated random numbers
                    for (int j = 0; j < UPDATES_PER_FRAME; j++) {
                        pairwiseKMV.update(randomNumbersToAdd.get(j));
                        fourwiseKMV.update(randomNumbersToAdd.get(j));
                    }
                }

                // Plot average error percentage on graph
                pairWise.getData().add(new XYChart.Data<>(trueDistinctCount.query(), pairwiseErrorPercentage / NUMBER_OF_KMVS));
                fourWise.getData().add(new XYChart.Data<>(trueDistinctCount.query(), fourwiseErrorPercentage / NUMBER_OF_KMVS));

                // Update our true distinct count with already generated random numbers
                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    trueDistinctCount.update(randomNumbersToAdd.get(i));
                }

                // Update our current distinct count
                currentDistinctCount = trueDistinctCount.query();
            }
        }.start();

        // Set the scene for the graph
        Scene scene = new Scene(LINE_CHART, 800, 600);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}
