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

public class ComparingKValuesKMV extends Application {
    @Override
    public void start(Stage stage) {

        Random rand = new Random();

        // Add
        final int[] K_VALUES = {10000, 20000};

        // Set the number of KMVs we run simultaneously. We can use a higher value to get a better idea of the average
        // performance of our algorithm.
        final int NUMBER_OF_KMVS = 200;

        // Set the number of distinct items we will count to until the algorithm terminates.
        final long DISTINCT_COUNT = 12000000;

        // Set the max number that our random number generator can generate. In order for the algorithm to work, this
        // has to be set higher than the DISTINCT_COUNT number.
        final long UPPER_LIMIT_NUM_TO_ADD = 150000000;

        // Set the number of updates made to our KMVs before refreshing the graph visualisation. The smaller this is,
        // the more detail that can be seen in the results. However, it will take the program much longer to arrive at
        // large count values.
        final int UPDATES_PER_FRAME = 1000;

        // Prepare line chart
        stage.setTitle("Comparing k values - KMV");
        final NumberAxis distinctItems = new NumberAxis();
        final NumberAxis algorithmEstimate = new NumberAxis();
        distinctItems.setLabel("Distinct Items");
        algorithmEstimate.setLabel("Algorithm percentage error");
        final LineChart<Number, Number> LINE_CHART = new LineChart<>(distinctItems, algorithmEstimate);
        LINE_CHART.setTitle("Comparing k values - KMV");
        LINE_CHART.setAnimated(false);
        LINE_CHART.setCreateSymbols(false);

        // Create array lists for our kmvlines and KMVs
        ArrayList<XYChart.Series<Number,Number>> kmvLines = new ArrayList<>();
        ArrayList<ArrayList<PairwiseKMV>> kmvs = new ArrayList<>();

        // Add kmvLine and KMVs to array lists
        for (int i = 0; i < K_VALUES.length; i++) {
            XYChart.Series<Number, Number> kmvLine = new XYChart.Series<>();
            kmvLine.setName(Integer.toString(K_VALUES[i]));
            kmvLines.add(kmvLine);
            LINE_CHART.getData().add(kmvLine);
            kmvs.add(new ArrayList<>());
            for (int j = 0; j < NUMBER_OF_KMVS; j++) {
                kmvs.get(i).add(new PairwiseKMV(K_VALUES[i]));
            }
        }

        // Set up basic true distinct count
        BasicDistinctCounting trueDistinctCount = new BasicDistinctCounting();

        new AnimationTimer() {
            private long currentDistinctCount = 0;

            @Override
            public void handle(long current) {

                // Terminate when our current count is greater than the count limit we set earlier
                if (currentDistinctCount > DISTINCT_COUNT) {
                    return;
                }

                // Generate random numbers to add to KMVs
                ArrayList<Long> randomNumbersToAdd = new ArrayList<>();
                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    randomNumbersToAdd.add(rand.nextLong(UPPER_LIMIT_NUM_TO_ADD));
                }

                // For each k value, sum up the percentage error from its KMVs
                for (int i = 0; i < K_VALUES.length; i++) {
                    double percentageErrorSum = 0;
                    ArrayList<PairwiseKMV> kmvArray = kmvs.get(i);
                    for (int j = 0; j < NUMBER_OF_KMVS; j++) {
                        PairwiseKMV kmv = kmvArray.get(j);
                        long estimate = kmv.query();
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
                    kmvLines.get(i).getData().add(new XYChart.Data<>(trueDistinctCount.query(), percentageError));
                }

                // Update our true count with the already generated random numbers
                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    trueDistinctCount.update(randomNumbersToAdd.get(i));
                }

                // Update our current distinct count
                currentDistinctCount = trueDistinctCount.query();
            }
        }.start();

        Scene scene = new Scene(LINE_CHART, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}