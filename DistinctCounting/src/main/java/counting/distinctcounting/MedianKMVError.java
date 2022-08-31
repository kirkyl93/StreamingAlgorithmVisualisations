package counting.distinctcounting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/** This class takes a single KMV k value and runs d simultaneous instances, where d is user-defined.
 * It then records the median and 90th percentile errors of the algorithm. This class creates a single chart:
 * 1) A dynamic visualisation of the median and 90th percentile errors as the true distinct count increases
 */

public class MedianKMVError extends Application {

    @Override
    public void start(Stage stage) {

        Random rand = new Random();

        // Add the k value to test
        final int K_VALUE = 1000000;

        // Set the number of KMVs we run simultaneously. We can use a higher value to get a better idea of the average
        // performance of our algorithm.
        final int NUMBER_OF_KMVS = 1000;

        // Set the number of distinct items we will count to until the algorithm terminates.
        final long DISTINCT_COUNT = 10000000;

        // Set the max number that our random number generator can generate. In order for the algorithm to work, this
        // has to be set higher than the DISTINCT_COUNT number.
        final long UPPER_LIMIT_NUM_TO_ADD = 15000000000L;

        // Set the number of updates made to our KMVs before refreshing the graph visualisation. The smaller this is,
        // the more detail that can be seen in the results. However, it will take the program much longer to arrive at
        // large count values.
        final int UPDATES_PER_FRAME = 1000;

        // Prepare line chart
        stage.setTitle("Median and 90th percentile line - KMV");
        final NumberAxis distinctItems = new NumberAxis();
        distinctItems.tickLabelFontProperty().set(Font.font(20));
        final NumberAxis algorithmEstimate = new NumberAxis();
        algorithmEstimate.tickLabelFontProperty().set(Font.font(20));
        distinctItems.setLabel("Distinct Items");
        algorithmEstimate.setLabel("Algorithm percentage error");
        final LineChart<Number, Number> LINE_CHART = new LineChart<>(distinctItems, algorithmEstimate);
        LINE_CHART.setStyle("-fx-font-size: " + 24 + "px;");
        LINE_CHART.setTitle("Median and 90th percentile line - KMV");
        LINE_CHART.setAnimated(false);
        LINE_CHART.setCreateSymbols(false);

        // Set up lines for median and 90th percentile error
        XYChart.Series<Number, Number> medianLine = new XYChart.Series<>();
        medianLine.setName("Median Line");
        XYChart.Series<Number, Number> percentile90Line = new XYChart.Series<>();
        percentile90Line.setName("90th Percentile Line");
        LINE_CHART.getData().add(medianLine);
        LINE_CHART.getData().add(percentile90Line);

        // Add KMVs to an array list

        ArrayList<PairwiseKMV> kmvs = new ArrayList<>(NUMBER_OF_KMVS);
        for (int i = 0; i < NUMBER_OF_KMVS; i++) {
            kmvs.add(new PairwiseKMV(K_VALUE));
        }

        int medianValue = NUMBER_OF_KMVS / 2;

        int percentile90Value = (NUMBER_OF_KMVS / 10) * 9;

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

                ArrayList<Long> kmvAbsoluteErrors = new ArrayList<>(NUMBER_OF_KMVS);

                // Generate random numbers to add to KMVs
                ArrayList<Long> randomNumbersToAdd = new ArrayList<>(UPDATES_PER_FRAME);
                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    randomNumbersToAdd.add(rand.nextLong(UPPER_LIMIT_NUM_TO_ADD));
                }

                for (int i = 0; i < NUMBER_OF_KMVS; i++) {
                    PairwiseKMV kmv = kmvs.get(i);
                    kmvAbsoluteErrors.add(Math.abs(kmv.query() - trueDistinctCount.query()));
                    for (int j = 0; j < UPDATES_PER_FRAME; j++) {
                        kmv.update(randomNumbersToAdd.get(j));
                    }
                }

                // Sort absolute errors
                Collections.sort(kmvAbsoluteErrors);

                double medianPercentageError = 0;
                double percentile90PercentageError = 0;

                if (currentDistinctCount > 0) {
                    medianPercentageError = (double) kmvAbsoluteErrors.get(medianValue) / currentDistinctCount * 100;
                    percentile90PercentageError = (double) kmvAbsoluteErrors.get(percentile90Value) / currentDistinctCount * 100;
                }
                // Add median error to median line
                medianLine.getData().add(new XYChart.Data<>(currentDistinctCount, medianPercentageError));

                // Add 90th percentile error line to 90th percentile line
                percentile90Line.getData().add(new XYChart.Data<>(currentDistinctCount, percentile90PercentageError));

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