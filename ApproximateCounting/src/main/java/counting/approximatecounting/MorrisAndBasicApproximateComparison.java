package counting.approximatecounting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MorrisAndBasicApproximateComparison extends Application {



    @Override
    public void start(Stage stage) {

        // Set the b value for the Morris Counters
        final double B_VALUE = 1.01;

        // Set the number of counters used for finding the average. If we set this to 100, we run 100 Morris Counters
        // and Basic Approximate Counters and take their average.
        final int NUMBER_OF_COUNTERS = 1000;

        // Set the update value for the Basic Approximate Counter
        final int APPROXIMATE_COUNT_VALUE = 1000;

        // Set the value to which our counter counts to
        final long COUNT_TO_VALUE = 10000000;

        // Set the number of updates made to our counters before refreshing the graph visualisation. The smaller this is,
        // the more detail that can be seen in the results. However, it will take the program much longer to arrive at
        // large count values.
        final int UPDATES_PER_FRAME = 1000;

        // Prepare percentage error line chart
        stage.setTitle("Comparing percentage error of Morris Counter to Basic Approximate Counter");
        final NumberAxis trueCount = new NumberAxis();
        trueCount.tickLabelFontProperty().set(Font.font(20));
        final NumberAxis percentageError = new NumberAxis();
        percentageError.tickLabelFontProperty().set(Font.font(20));
        trueCount.setLabel("True count");
        percentageError.setLabel("Percentage error");
        final LineChart<Number, Number> LINE_CHART_ERROR = new LineChart<>(trueCount, percentageError);
        LINE_CHART_ERROR.setStyle("-fx-font-size: " + 24 + "px;");
        LINE_CHART_ERROR.setTitle("Comparing percentage error of Morris Counter to Basic Approximate Counter");
        LINE_CHART_ERROR.setAnimated(false);
        LINE_CHART_ERROR.setCreateSymbols(false);

        // Prepare number of updates line chart
        Stage secondStage = new Stage();
        secondStage.setTitle("Comparing number of updates");
        final NumberAxis numberOfUpdates = new NumberAxis();
        numberOfUpdates.tickLabelFontProperty().set(Font.font(20));
        final NumberAxis trueCount2 = new NumberAxis();
        trueCount2.tickLabelFontProperty().set(Font.font(20));
        numberOfUpdates.setLabel("Number of updates");
        trueCount2.setLabel("True count");
        final LineChart<Number, Number> LINE_CHART_UPDATES = new LineChart<>(trueCount2, numberOfUpdates);
        LINE_CHART_UPDATES.setStyle("-fx-font-size: " + 24 + "px;");
        LINE_CHART_UPDATES.setTitle("Comparing number of updates");
        LINE_CHART_UPDATES.setAnimated(false);
        LINE_CHART_UPDATES.setCreateSymbols(false);

        // Prepare lines for Morris Counter and Basic Approximate Counter
        XYChart.Series<Number, Number> morrisErrorLine = new XYChart.Series<>();
        morrisErrorLine.setName("Morris error line");
        XYChart.Series<Number, Number> basicApproximateErrorLine = new XYChart.Series<>();
        basicApproximateErrorLine.setName("Basic approximate error line");
        XYChart.Series<Number, Number> morrisUpdateLine = new XYChart.Series<>();
        morrisUpdateLine.setName("Morris update line");
        XYChart.Series<Number, Number> basicApproximateUpdateLine = new XYChart.Series<>();
        basicApproximateUpdateLine.setName("Basic approximate update line");

        // Add error lines to chart
        LINE_CHART_ERROR.getData().add(morrisErrorLine);
        LINE_CHART_ERROR.getData().add(basicApproximateErrorLine);

        // Add update lines to chart
        LINE_CHART_UPDATES.getData().add(morrisUpdateLine);
        LINE_CHART_UPDATES.getData().add(basicApproximateUpdateLine);

        // Set up Morris and Basic Approximate Counters
        MorrisCounter[] morrisCounters = new MorrisCounter[NUMBER_OF_COUNTERS];
        BasicApproximateCounter[] basicApproximateCounters = new BasicApproximateCounter[NUMBER_OF_COUNTERS];

        for (int i = 0; i < NUMBER_OF_COUNTERS; i++) {
            morrisCounters[i] = new MorrisCounter(B_VALUE);
            basicApproximateCounters[i] = new BasicApproximateCounter(APPROXIMATE_COUNT_VALUE);
        }

        new AnimationTimer() {
            private long count;

            @Override
            public void handle(long current) {

                // Terminate when our current count is greater than the count limit we set earlier
                if (count > COUNT_TO_VALUE) {
                    return;
                }

                // Prepare error and update trackers
                double morrisPercentageErrors = 0;
                double basicApproximatePercentageErrors = 0;
                long morrisUpdates = 0;
                long basicApproximateUpdates = 0;

                // Retrieve estimates and updates and calculate percentage error
                for (int i = 0; i < NUMBER_OF_COUNTERS; i++) {
                    MorrisCounter mc = morrisCounters[i];
                    long mcCount = mc.query();
                    long mcUpdateCount = mc.getTimesUpdated();

                    BasicApproximateCounter bac = basicApproximateCounters[i];
                    long bacCount = bac.query();
                    long bacUpdateCount = bac.getTimesUpdated();

                    double morrisPercentageError = 0;
                    double basicApproximatePercentageError = 0;

                    if (count > 0) {
                        morrisPercentageError = (double) Math.abs(mcCount - count) / count * 100;
                        basicApproximatePercentageError = (double) Math.abs(bacCount - count) / count * 100;
                    }

                    // Add updates and errors to the update and error trackers
                    morrisPercentageErrors += morrisPercentageError;
                    basicApproximatePercentageErrors += basicApproximatePercentageError;

                    morrisUpdates += mcUpdateCount;
                    basicApproximateUpdates += bacUpdateCount;


                    // Update each counter by the updates per frame
                    for (int j = 0; j < UPDATES_PER_FRAME; j++) {
                        mc.update();
                        bac.update();
                    }

                }

                // Calculate averages
                double mcAveragePercentageError = morrisPercentageErrors / NUMBER_OF_COUNTERS;
                double mcAverageUpdates = (double) morrisUpdates / NUMBER_OF_COUNTERS;
                double bacAveragePercentageError = basicApproximatePercentageErrors / NUMBER_OF_COUNTERS;
                double bacAverageUpdates = (double) basicApproximateUpdates / NUMBER_OF_COUNTERS;

                // Plot averages on chart
                morrisErrorLine.getData().add(new XYChart.Data<>(count, mcAveragePercentageError));
                morrisUpdateLine.getData().add(new XYChart.Data<>(count, mcAverageUpdates));

                basicApproximateErrorLine.getData().add(new XYChart.Data<>(count, bacAveragePercentageError));
                basicApproximateUpdateLine.getData().add(new XYChart.Data<>(count, bacAverageUpdates));

                count += UPDATES_PER_FRAME;

            }
        }.start();

        // Set up a scene for each of the line charts
        Scene scene = new Scene(LINE_CHART_ERROR, 800, 600);
        stage.setScene(scene);
        stage.show();

        Scene scene2 = new Scene(LINE_CHART_UPDATES, 800, 600);
        secondStage.setScene(scene2);
        secondStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
