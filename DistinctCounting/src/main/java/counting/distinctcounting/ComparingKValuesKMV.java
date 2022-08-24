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

/** This class creates a visualisation of the performance of different, user-defined k values for the KMV. This class
 *  creates 2 charts:
 * 1) A dynamic visualisation of the percentage error of each k value (averaged if NUMBER_OF_KMVS > 1)
 * 2) A dynamic visualisation of the algorithm estimate for each k value (averaged if NUMBER_OF_KMVS > 1) plotted
 * alongside the true distinct count
 */

public class ComparingKValuesKMV extends Application {
    @Override
    public void start(Stage stage) {

        Random rand = new Random();

        // Add the k values to compare
        final int[] K_VALUES = {100, 10000};

        // Set the number of KMVs we run simultaneously. We can use a higher value to get a better idea of the average
        // performance of our algorithm.
        final int NUMBER_OF_KMVS = 1;

        // Set the number of distinct items we will count to until the algorithm terminates.
        final long DISTINCT_COUNT = 120000000;

        // Set the max number that our random number generator can generate. In order for the algorithm to work, this
        // has to be set higher than the DISTINCT_COUNT number.
        final long UPPER_LIMIT_NUM_TO_ADD = 150000000;

        // Set the number of updates made to our KMVs before refreshing the graph visualisation. The smaller this is,
        // the more detail that can be seen in the results. However, it will take the program much longer to arrive at
        // large count values.
        final int UPDATES_PER_FRAME = 100;

        // Prepare line charts
        stage.setTitle("Comparing k value percentage errors - KMV");
        final NumberAxis distinctItems = new NumberAxis();
        final NumberAxis percentageError = new NumberAxis();
        distinctItems.setLabel("Distinct Items");
        percentageError.setLabel("Algorithm percentage error");
        final LineChart<Number, Number> LINE_CHART_ERROR = new LineChart<>(distinctItems, percentageError);
        LINE_CHART_ERROR.setTitle("Comparing k value percentage errors - KMV");
        LINE_CHART_ERROR.setAnimated(false);
        LINE_CHART_ERROR.setCreateSymbols(false);

        Stage stage2 = new Stage();
        stage2.setTitle("Comparing k value estimates - KMV");
        final NumberAxis distinctItems2 = new NumberAxis();
        final NumberAxis algorithmEstimate = new NumberAxis();
        distinctItems2.setLabel("Distinct Items");
        algorithmEstimate.setLabel("Algorithm estimate");
        final LineChart<Number, Number> LINE_CHART_ESTIMATE = new LineChart<>(distinctItems2, algorithmEstimate);
        LINE_CHART_ESTIMATE.setTitle("Comparing k value estimates - KMV");
        LINE_CHART_ESTIMATE.setAnimated(false);
        LINE_CHART_ESTIMATE.setCreateSymbols(false);

        // Create array lists for our kmvlines and KMVs
        ArrayList<XYChart.Series<Number,Number>> kmvErrorLines = new ArrayList<>();
        ArrayList<XYChart.Series<Number, Number>> kmvEstimateLines = new ArrayList<>();
        ArrayList<ArrayList<PairwiseKMV>> kmvs = new ArrayList<>();

        // Add kmvLine and KMVs to array lists
        for (int i = 0; i < K_VALUES.length; i++) {
            XYChart.Series<Number, Number> kmvErrorLine = new XYChart.Series<>();
            kmvErrorLine.setName(Integer.toString(K_VALUES[i]));
            kmvErrorLines.add(kmvErrorLine);
            LINE_CHART_ERROR.getData().add(kmvErrorLine);

            XYChart.Series<Number, Number> kmvEstimateLine = new XYChart.Series<>();
            kmvEstimateLine.setName(Integer.toString(K_VALUES[i]));
            kmvEstimateLines.add(kmvEstimateLine);
            LINE_CHART_ESTIMATE.getData().add(kmvEstimateLine);

            kmvs.add(new ArrayList<>(NUMBER_OF_KMVS));
            for (int j = 0; j < NUMBER_OF_KMVS; j++) {
                kmvs.get(i).add(new PairwiseKMV(K_VALUES[i]));
            }
        }

        XYChart.Series<Number, Number> trueCountLine = new XYChart.Series<>();
        trueCountLine.setName("True count");
        LINE_CHART_ESTIMATE.getData().add(trueCountLine);

        // Set up basic true distinct count
        BasicDistinctCountingHash trueDistinctCount = new BasicDistinctCountingHash();


        new AnimationTimer() {
            private long currentDistinctCount = 0;

            @Override
            public void handle(long current) {

                // Terminate when our current count is greater than the count limit we set earlier
                if (currentDistinctCount > DISTINCT_COUNT) {
                    return;
                }

                // Generate random numbers to add to KMVs
                ArrayList<Long> randomNumbersToAdd = new ArrayList<>(UPDATES_PER_FRAME);
                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    randomNumbersToAdd.add(rand.nextLong(UPPER_LIMIT_NUM_TO_ADD));
                }

                // For each k value, sum up the percentage error from its KMVs
                for (int i = 0; i < K_VALUES.length; i++) {
                    double percentageErrorSum = 0;
                    long kmvEstimateSum = 0L;
                    ArrayList<PairwiseKMV> kmvArray = kmvs.get(i);
                    for (int j = 0; j < NUMBER_OF_KMVS; j++) {
                        PairwiseKMV kmv = kmvArray.get(j);
                        long estimate = kmv.query();
                        kmvEstimateSum += estimate;
                        long distinctCount = trueDistinctCount.query();
                        if (distinctCount > 0) {
                            percentageErrorSum += (double) Math.abs(distinctCount - estimate) / distinctCount * 100;
                        }

                        // Update each KMV with already generated random numbers
                        for (int k = 0; k < UPDATES_PER_FRAME; k++) {
                            kmv.update(randomNumbersToAdd.get(k));
                        }
                    }

                    // Add percentage error to our line chart
                    double percentageError = percentageErrorSum / NUMBER_OF_KMVS;
                    kmvErrorLines.get(i).getData().add(new XYChart.Data<>(trueDistinctCount.query(), percentageError));

                    double kmvEstimate = (double) kmvEstimateSum / NUMBER_OF_KMVS;
                    kmvEstimateLines.get(i).getData().add(new XYChart.Data<>(trueDistinctCount.query(), kmvEstimate));
                }

                trueCountLine.getData().add(new XYChart.Data<>(trueDistinctCount.query(), trueDistinctCount.query()));

                // Update our true count with the already generated random numbers
                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    trueDistinctCount.update(randomNumbersToAdd.get(i));
                }

                // Update our current distinct count
                currentDistinctCount = trueDistinctCount.query();
            }
        }.start();

        Scene scene = new Scene(LINE_CHART_ERROR, 800, 600);
        stage.setScene(scene);
        stage.show();

        Scene scene2 = new Scene(LINE_CHART_ESTIMATE, 800, 600);
        stage2.setScene(scene2);
        stage2.show();
    }

    public static void main(String[] args) {
        launch();
    }
}