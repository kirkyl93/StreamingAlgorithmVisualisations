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
import java.util.Random;

/** This class is a proof of concept to show that the expected value of the KMV equals the true count. The higher the
 * number of counters entered, and the bigger the k value, the closer the lines should run to the true distinct count.
 * This class creates 2 charts:
 * 1) A dynamic visualisation of the percentage errors of the average query results of the KMVs
 * 2) A dynamic visualisation of the average of the algorithm estimates for the KMVs plotted against the true count
 */

public class ExpectedValueProofOfConcept extends Application {

    @Override
    public void start(Stage stage) {

        Random rand = new Random();

        // Set the k Value of the KMVs
        final int K_VALUE = 100;

        // Set the number of KMVs we run simultaneously. We can use a higher value to get a better idea of the average
        // performance of our algorithm.
        final int NUMBER_OF_KMVS = 50000;

        // Set the number of distinct items we will count to until the algorithm terminates.
        final long DISTINCT_COUNT = 10000000;

        // Set the max number that our random number generator can generate. In order for the algorithm to work, this
        // has to be set higher than the DISTINCT_COUNT number.
        final long UPPER_LIMIT_NUM_TO_ADD = 150000000;

        // Set the number of updates made to our KMVs before refreshing the graph visualisation. The smaller this is,
        // the more detail that can be seen in the results. However, it will take the program much longer to arrive at
        // large count values.
        final int UPDATES_PER_FRAME = 1000;

        // Prepare line charts
        stage.setTitle("Expected value proof of concept");
        final NumberAxis distinctItems = new NumberAxis();
        distinctItems.tickLabelFontProperty().set(Font.font(20));
        distinctItems.setLabel("Distinct Items");
        final NumberAxis algorithmEstimate = new NumberAxis();
        algorithmEstimate.tickLabelFontProperty().set(Font.font(20));
        algorithmEstimate.setLabel("Algorithm estimate");
        final LineChart<Number, Number> LINE_CHART_ESTIMATE = new LineChart<>(distinctItems, algorithmEstimate);
        LINE_CHART_ESTIMATE.setTitle("Expected value proof of concept");
        LINE_CHART_ESTIMATE.setStyle("-fx-font-size: " + 24 + "px;");
        LINE_CHART_ESTIMATE.setAnimated(false);
        LINE_CHART_ESTIMATE.setCreateSymbols(false);

        Stage secondStage = new Stage();
        secondStage.setTitle("Percentage Error of average of KMV queries");
        final NumberAxis distinctItems2 = new NumberAxis();
        distinctItems2.tickLabelFontProperty().set(Font.font(20));
        distinctItems2.setLabel("Distinct Items");
        final NumberAxis percentageError = new NumberAxis();
        percentageError.tickLabelFontProperty().set(Font.font(20));
        percentageError.setLabel("Percentage error");
        final LineChart<Number, Number> LINE_CHART_ERROR = new LineChart<>(distinctItems2, percentageError);
        LINE_CHART_ERROR.setTitle("Percentage Error of average of KMV queries");
        LINE_CHART_ERROR.setStyle("-fx-font-size: " + 24 + "px;");
        LINE_CHART_ERROR.setAnimated(false);
        LINE_CHART_ERROR.setCreateSymbols(false);

        XYChart.Series<Number, Number> trueEstimate = new XYChart.Series<>();
        trueEstimate.setName("True estimate line");
        LINE_CHART_ESTIMATE.getData().add(trueEstimate);

        XYChart.Series<Number, Number> kmvsEstimate = new XYChart.Series<>();
        kmvsEstimate.setName("KMVs estimate");
        LINE_CHART_ESTIMATE.getData().add(kmvsEstimate);

        XYChart.Series<Number, Number> percentageErrorLine = new XYChart.Series<>();
        percentageErrorLine.setName("Percentage error");
        LINE_CHART_ERROR.getData().add(percentageErrorLine);

        ArrayList<PairwiseKMV> kmvs = new ArrayList<>(NUMBER_OF_KMVS);
        for (int i = 0; i < NUMBER_OF_KMVS; i++) {
            kmvs.add(new PairwiseKMV(K_VALUE));
        }

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

                long estimate = 0;

                for (int i = 0; i < NUMBER_OF_KMVS; i++) {
                    PairwiseKMV kmv = kmvs.get(i);
                    estimate += kmv.query();
                    for (int j = 0; j < UPDATES_PER_FRAME; j++) {
                        kmv.update(randomNumbersToAdd.get(j));
                    }
                }

                double percentageError = 0;
                double kmvEstimate = (double) estimate / NUMBER_OF_KMVS;

                if (currentDistinctCount > 0) {
                    percentageError = (kmvEstimate - currentDistinctCount) / currentDistinctCount * 100;
                }

                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    trueDistinctCount.update(randomNumbersToAdd.get(i));
                }

                trueEstimate.getData().add(new XYChart.Data<>(currentDistinctCount, currentDistinctCount));
                kmvsEstimate.getData().add(new XYChart.Data<>(currentDistinctCount, kmvEstimate));
                percentageErrorLine.getData().add(new XYChart.Data<>(currentDistinctCount, percentageError));

                currentDistinctCount = trueDistinctCount.query();
            }
        }.start();

        Scene scene = new Scene(LINE_CHART_ESTIMATE, 800, 600);
        stage.setScene(scene);
        stage.show();

        Scene scene2 = new Scene(LINE_CHART_ERROR, 800, 600);
        secondStage.setScene(scene2);
        secondStage.show();

    }
}
