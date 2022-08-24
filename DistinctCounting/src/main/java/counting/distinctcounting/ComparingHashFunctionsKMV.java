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

/** This class is a proof of concept to show that a pairwise hash function performs just as well as a fourwise hash
 * function when using the KMV algorithm. This class creates two charts:
 * 1) A dynamic visualisation of the percentage error of each KMV hash function implementation (averaged if NUMBER_OF_KMVs
 * > 1)
 * 2) A dynamic visualisation of the algorithm estimate for each KMV hash function implementation (averaged if
 * NUMBER_OF_KMVs > 1) plotted alongside the true count
 */

public class ComparingHashFunctionsKMV extends Application {

    @Override
    public void start(Stage stage) {

        Random rand = new Random();

        // Set the number of items we will store in our priority queue (the Kth value will be the item at the top
        // of the heap). The bigger this value, the more accurate our algorithm should be.
        final int K_VALUE = 10;

        // Set the number of KMVs we run simultaneously. We can use a higher value to get a better idea of the average
        // performance of our algorithm.
        final int NUMBER_OF_KMVS = 1;

        // Set the number of distinct items we will count to until the algorithm terminates.
        final long DISTINCT_COUNT = 1200000000000L;

        // Set the max number that our random number generator can generate. In order for the algorithm to work, this
        // has to be set higher than the DISTINCT_COUNT number.
        final long UPPER_LIMIT_NUM_TO_ADD = 100000000L;

        // Set the number of updates made to our KMVs before refreshing the graph visualisation. The smaller this is,
        // the more detail that can be seen in the results. However, it will take the program much longer to arrive at
        // large count values.
        final int UPDATES_PER_FRAME = 100;


        // Prepare line charts
        stage.setTitle("Comparing percentage errors of hash functions - KMV");
        final NumberAxis distinctItems = new NumberAxis();
        final NumberAxis percentageError = new NumberAxis();
        distinctItems.setLabel("Distinct Items");
        percentageError.setLabel("Algorithm percentage error");
        final LineChart<Number, Number> LINE_CHART_ERROR = new LineChart<>(distinctItems, percentageError);
        LINE_CHART_ERROR.setTitle("Comparing percentage errors of hash functions - KMV");
        LINE_CHART_ERROR.setAnimated(false);
        LINE_CHART_ERROR.setCreateSymbols(false);

        Stage secondStage = new Stage();
        secondStage.setTitle("Hash function proof of concept");
        final NumberAxis distinctItems2 = new NumberAxis();
        final NumberAxis algorithmEstimate = new NumberAxis();
        distinctItems2.setLabel("Distinct Items");
        algorithmEstimate.setLabel("Algorithm estimate");
        final LineChart<Number, Number> LINE_CHART_ESTIMATE = new LineChart<>(distinctItems2, algorithmEstimate);
        LINE_CHART_ESTIMATE.setTitle("Hash function proof of concept");
        LINE_CHART_ESTIMATE.setAnimated(false);
        LINE_CHART_ESTIMATE.setCreateSymbols(false);

        // Create array lists to store our hash functions and add hash functions
        ArrayList<PairwiseKMV> pairwiseKMVs = new ArrayList<>();
        ArrayList<FourwiseKMV> fourwiseKMVs = new ArrayList<>();

        for (int j = 0; j < NUMBER_OF_KMVS; j++) {
            pairwiseKMVs.add(new PairwiseKMV(K_VALUE));
            fourwiseKMVs.add(new FourwiseKMV(K_VALUE));
        }

        // Add lines to line charts for pairwise and fourwise hash functions
        XYChart.Series<Number, Number> pairwiseError = new XYChart.Series<>();
        pairwiseError.setName("Pairwise hash");
        LINE_CHART_ERROR.getData().add(pairwiseError);

        XYChart.Series<Number, Number> fourwiseError = new XYChart.Series<>();
        fourwiseError.setName("Fourwise hash");
        LINE_CHART_ERROR.getData().add(fourwiseError);

        XYChart.Series<Number, Number> pairwiseEstimate = new XYChart.Series<>();
        pairwiseEstimate.setName("Pairwise hash");
        LINE_CHART_ESTIMATE.getData().add(pairwiseEstimate);

        XYChart.Series<Number, Number> fourwiseEstimate = new XYChart.Series<>();
        fourwiseEstimate.setName("Fourwise hash");
        LINE_CHART_ESTIMATE.getData().add(fourwiseEstimate);

        XYChart.Series<Number, Number> trueCountLine = new XYChart.Series<>();
        trueCountLine.setName("True count");
        LINE_CHART_ESTIMATE.getData().add(trueCountLine);

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

                // Initialise variables we will use to store estimates

                long pairwiseEstimates = 0;
                long fourwiseEstimates = 0;

                // Initialise variables we will use for the sum of percentage errors across our KMVs
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
                    pairwiseEstimates += pairwiseEstimate;
                    fourwiseEstimates += fourwiseEstimate;
                    long distinctCount = trueDistinctCount.query();
                    if (distinctCount > 0 && distinctCount - fourwiseEstimate != 0) {
                        pairwiseErrorPercentage += (double) Math.abs(distinctCount - pairwiseEstimate) / distinctCount * 100;
                        fourwiseErrorPercentage += (double) Math.abs(distinctCount - fourwiseEstimate) / distinctCount * 100;
                    }

                    // Update KMVs with already generated random numbers
                    for (int j = 0; j < UPDATES_PER_FRAME; j++) {
                        pairwiseKMV.update(randomNumbersToAdd.get(j));
                        fourwiseKMV.update(randomNumbersToAdd.get(j));
                    }
                }

                long distinctCount = trueDistinctCount.query();
                // Plot average error percentage and estimates on graphs
                pairwiseError.getData().add(new XYChart.Data<>(distinctCount, pairwiseErrorPercentage / NUMBER_OF_KMVS));
                fourwiseError.getData().add(new XYChart.Data<>(distinctCount, fourwiseErrorPercentage / NUMBER_OF_KMVS));

                pairwiseEstimate.getData().add(new XYChart.Data<>(distinctCount, (double) pairwiseEstimates / NUMBER_OF_KMVS));
                fourwiseEstimate.getData().add(new XYChart.Data<>(distinctCount, (double) fourwiseEstimates / NUMBER_OF_KMVS));
                trueCountLine.getData().add(new XYChart.Data<>(distinctCount, distinctCount));

                // Update our true distinct count with already generated random numbers
                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    trueDistinctCount.update(randomNumbersToAdd.get(i));
                }

                // Update our current distinct count
                currentDistinctCount = trueDistinctCount.query();
            }
        }.start();

        // Set the scene for the graph
        Scene scene = new Scene(LINE_CHART_ERROR, 800, 600);
        stage.setScene(scene);
        stage.show();

        Scene scene2 = new Scene(LINE_CHART_ESTIMATE, 800, 600);
        secondStage.setScene(scene2);
        secondStage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}
