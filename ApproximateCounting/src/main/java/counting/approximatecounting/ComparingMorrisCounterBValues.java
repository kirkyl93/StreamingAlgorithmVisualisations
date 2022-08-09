package counting.approximatecounting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ComparingMorrisCounterBValues extends Application {

    @Override
    public void start(Stage stage) {

        // Set the b values for the lines
        final double[] B_VALUES = {1.001};

        // Set the number of counters used for finding the average. If we set this to 100, we run 100 Morris Counters
        // simultaneously for each b_value and take their average.
        final int NUMBER_OF_COUNTERS = 1;

        // Set the value to which our counters count to.
        final long COUNT_TO_VALUE = 5000000000L;

        // Set the number of updates made to our counters before refreshing the graph visualisation. The smaller this is,
        // the more detail that can be seen in the results. However, it will take the program much longer to arrive at
        // large count values.
        final int UPDATES_PER_FRAME = 1000000;

        // Prepare estimate line chart
        stage.setTitle("Comparing B value estimates in Morris Counter");
        final NumberAxis trueCount = new NumberAxis();
        final NumberAxis algorithmEstimate = new NumberAxis();
        trueCount.setLabel("True count");
        algorithmEstimate.setLabel("Algorithm estimate");
        final LineChart<Number, Number> LINE_CHART_ESTIMATES = new LineChart<>(trueCount, algorithmEstimate);
        LINE_CHART_ESTIMATES.setTitle("Comparing B value estimates in Morris Counter");
        LINE_CHART_ESTIMATES.setAnimated(false);
        LINE_CHART_ESTIMATES.setCreateSymbols(false);

        // Prepare number of updates line chart
        Stage secondStage = new Stage();
        secondStage.setTitle("Comparing updates per B value");
        final NumberAxis numberOfUpdates = new NumberAxis();
        final NumberAxis trueCount2 = new NumberAxis();
        numberOfUpdates.setLabel("Number of updates");
        trueCount2.setLabel("True count");
        final LineChart<Number, Number> LINE_CHART_UPDATES = new LineChart<>(trueCount2, numberOfUpdates);
        LINE_CHART_UPDATES.setTitle("Comparing updates per B value");
        LINE_CHART_UPDATES.setAnimated(false);
        LINE_CHART_UPDATES.setCreateSymbols(false);

        // Prepare percentage error line chart
        Stage thirdStage = new Stage();
        thirdStage.setTitle("Comparing percentage error");
        final NumberAxis percentageError = new NumberAxis();
        final NumberAxis trueCount3 = new NumberAxis();
        percentageError.setLabel("Percentage  error");
        trueCount3.setLabel("True count");
        final LineChart<Number, Number> LINE_CHART_PERCENTAGE_ERROR = new LineChart<>(trueCount3, percentageError);
        LINE_CHART_PERCENTAGE_ERROR.setTitle("Comparing percentage error");
        LINE_CHART_PERCENTAGE_ERROR.setAnimated(false);
        LINE_CHART_PERCENTAGE_ERROR.setCreateSymbols(false);

        // Prepare storage for all the lines we are going to store and update on our charts
        ArrayList<XYChart.Series<Number,Number>> morrisLines = new ArrayList<>();
        ArrayList<XYChart.Series<Number, Number>> updateLines = new ArrayList<>();
        ArrayList<XYChart.Series<Number, Number>> percentageErrorLines = new ArrayList<>();
        ArrayList<ArrayList<MorrisCounter>> morrisCounters = new ArrayList<>(NUMBER_OF_COUNTERS);


        for (int i = 0; i < B_VALUES.length; i++) {
            // For every b_value, add a line for MorrisCounter approximation, number of updates and relative error
            XYChart.Series<Number, Number> morrisLine = new XYChart.Series<>();
            morrisLine.setName(Double.toString(B_VALUES[i]));

            XYChart.Series<Number, Number> updatesLine = new XYChart.Series<>();
            updatesLine.setName(Double.toString(B_VALUES[i]));

            XYChart.Series<Number, Number> percentageErrorLine = new XYChart.Series<>();
            percentageErrorLine.setName(Double.toString(B_VALUES[i]));

            // Add newly created lines to our previously prepared arraylists and charts
            morrisLines.add(morrisLine);
            updateLines.add(updatesLine);
            percentageErrorLines.add(percentageErrorLine);

            LINE_CHART_ESTIMATES.getData().add(morrisLine);
            LINE_CHART_UPDATES.getData().add(updatesLine);
            LINE_CHART_PERCENTAGE_ERROR.getData().add(percentageErrorLine);

            // Add requested number of MorrisCounters to each b_value slot
            morrisCounters.add(new ArrayList<>());
            for (int j = 0; j < NUMBER_OF_COUNTERS; j++) {
                morrisCounters.get(i).add(new MorrisCounter(B_VALUES[i]));
            }
        }

        // Set up basic counter line for comparison with Morris Counters on the estimates line chart
        XYChart.Series<Number, Number> basicCounterLine = new XYChart.Series<>();
        basicCounterLine.setName("Basic Counter");
        LINE_CHART_ESTIMATES.getData().add(basicCounterLine);

        new AnimationTimer() {
            private long count;

            @Override
            public void handle(long current) {

                // Terminate when our current count is greater than the count limit we set earlier
                if (count > COUNT_TO_VALUE) {
                    return;
                }

                // Prepare estimate, update and percentage error trackers
                ArrayList<Long> morrisEstimates = new ArrayList<>(NUMBER_OF_COUNTERS);
                ArrayList<Long> updates = new ArrayList<>();
                ArrayList<Double> percentageErrors = new ArrayList<>(NUMBER_OF_COUNTERS);

                for (int i = 0; i < B_VALUES.length; i++) {
                    morrisEstimates.add(0L);
                    updates.add(0L);
                    percentageErrors.add(0D);

                    // Retrieve each set of b_value MorrisCounters and gather their updates, estimates and relative errors
                    ArrayList<MorrisCounter> mcs = morrisCounters.get(i);
                    for (int j = 0; j < NUMBER_OF_COUNTERS; j++) {
                        MorrisCounter mc = mcs.get(j);
                        long mcCount = mc.query();
                        long updateCount = mc.getTimesUpdated();
                        double percentageError = 0;
                        if (count > 0) {
                            percentageError = (double) Math.abs((mcCount - count)) / count * 100;
                        }

                        // Update trackers
                        morrisEstimates.set(i, morrisEstimates.get(i) + mcCount);
                        updates.set(i, updates.get(i) + updateCount);
                        percentageErrors.set(i, percentageErrors.get(i) + percentageError);

                        // Update each counter by the updates per frame
                        for (int k = 0; k < UPDATES_PER_FRAME; k++) {
                            mc.update();
                        }
                    }
                }

                // Calculate average count, updates and percentage error for each b_value and add to charts
                for (int i = 0; i < B_VALUES.length; i++) {
                    double averageCount = (double) morrisEstimates.get(i) / NUMBER_OF_COUNTERS;
                    double averageUpdates = (double) updates.get(i) / NUMBER_OF_COUNTERS;
                    double percentageError = percentageErrors.get(i) / NUMBER_OF_COUNTERS;
                    morrisLines.get(i).getData().add(new XYChart.Data<>(count, averageCount));
                    updateLines.get(i).getData().add(new XYChart.Data<>(count, averageUpdates));
                    percentageErrorLines.get(i).getData().add(new XYChart.Data<>(count, percentageError));
                }

                // Add basic counter line
                basicCounterLine.getData().add(new XYChart.Data<>(count, count));

                count += UPDATES_PER_FRAME;
            }
        }.start();

        // Set up a scene for each of the line charts
        Scene scene = new Scene(LINE_CHART_ESTIMATES, 800, 600);
        stage.setScene(scene);
        stage.show();

        Scene scene2 = new Scene(LINE_CHART_UPDATES, 800, 600);
        secondStage.setScene(scene2);
        secondStage.show();

        Scene scene3 = new Scene(LINE_CHART_PERCENTAGE_ERROR, 800, 600);
        thirdStage.setScene(scene3);
        thirdStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

