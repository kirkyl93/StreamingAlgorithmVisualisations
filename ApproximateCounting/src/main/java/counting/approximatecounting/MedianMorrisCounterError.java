package counting.approximatecounting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.Arrays;

public class MedianMorrisCounterError extends Application {

    @Override
    public void start(Stage stage) {

        // Set the b value for the Morris Counters
        final double B_VALUE = 5;

        // Set the number of counters from which we will select the median and 90th percentile run
        final int NUMBER_OF_COUNTERS = 10000;

        // Set the value to which our counter counts to
        final long COUNT_TO_VALUE = 100000000;

        // Set the number of updates made to our counters before refreshing the graph visualisation. The smaller this is,
        // the more detail that can be seen in the results. However, it will take the program much longer to arrive at
        // large count values.
        final int UPDATES_PER_FRAME = 1000;


        // Prepare percentage error line chart
        stage.setTitle("Median and 90th percentile run " + B_VALUE);
        final NumberAxis trueCount = new NumberAxis();
        final NumberAxis percentageError = new NumberAxis();
        trueCount.setLabel("True count");
        percentageError.setLabel("Percentage error");
        final LineChart<Number, Number> LINE_CHART = new LineChart<>(trueCount, percentageError);
        LINE_CHART.setTitle("Median and 90th percentile run " + B_VALUE);
        LINE_CHART.setAnimated(false);
        LINE_CHART.setCreateSymbols(false);

        // Prepare median and 90th percentile error line
        XYChart.Series<Number, Number> medianLine = new XYChart.Series<>();
        medianLine.setName("Median line");

        XYChart.Series<Number, Number> percentile90Line = new XYChart.Series<>();
        percentile90Line.setName("90th percentile line");

        LINE_CHART.getData().add(medianLine);
        LINE_CHART.getData().add(percentile90Line);

        MorrisCounter[] morrisCounters = new MorrisCounter[NUMBER_OF_COUNTERS];


        // Calculate median value
        int medianValue = NUMBER_OF_COUNTERS / 2;

        // Calculate 90th percentile value
        int percentile90Value = (NUMBER_OF_COUNTERS / 10) * 9;

        // Initialise Morris Counters
        for (int i = 0; i < NUMBER_OF_COUNTERS; i++) {
            morrisCounters[i] = new MorrisCounter(B_VALUE);
        }

        new AnimationTimer() {
            private long count;

            @Override
            public void handle(long current) {

                // Terminate when our current count is greater than the count limit we set earlier
                if (count > COUNT_TO_VALUE) {
                    return;
                }

                // Initialise array to store error percentages
                double[] morrisPercentageErrors = new double[NUMBER_OF_COUNTERS];


                // Retrieve Morris Counter estimates and calculate percentage error
                for (int i = 0; i < NUMBER_OF_COUNTERS; i++) {
                    MorrisCounter mc = morrisCounters[i];
                    long mcCount = mc.query();

                    double morrisPercentageError = 0;

                    if (count > 0) {
                        morrisPercentageError = (double) Math.abs(mcCount - count) / count * 100;
                    }

                    morrisPercentageErrors[i] = morrisPercentageError;

                    // Update Morris Counters
                    for (int j = 0; j < UPDATES_PER_FRAME; j++) {
                        mc.update();
                    }
                }

                // Sort percentage errors
                Arrays.sort(morrisPercentageErrors);

                // Add median error to median line
                medianLine.getData().add(new XYChart.Data<>(count, morrisPercentageErrors[medianValue]));

                // Add 90th percentile error line to 90th percentile line
                percentile90Line.getData().add(new XYChart.Data<>(count, morrisPercentageErrors[percentile90Value]));

                // Update count
                count += UPDATES_PER_FRAME;

            }

        }.start();

        // Prepare scene
        Scene scene = new Scene(LINE_CHART, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
