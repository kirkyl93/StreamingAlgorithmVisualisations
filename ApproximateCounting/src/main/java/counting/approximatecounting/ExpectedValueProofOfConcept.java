package counting.approximatecounting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ExpectedValueProofOfConcept extends Application {
    @Override
    public void start(Stage stage) {

        // Set the number of counters used for finding the average. If we set this to 100, we run 100 Morris and
        // Approximate Counters simultaneously and take their average.
        final int NUMBER_OF_COUNTERS = 100;

        // Set the value to which our counters count to.
        final int COUNT_TO_VALUE = 50000;

        // Set the number of updates made to our counters before refreshing the graph visualisation.
        final int UPDATES_PER_FRAME = 30;

        // Set the b value in Morris counter (see MorrisCounter class). In Morris' original formulation,
        // this is set to 2.
        final double MORRIS_B_VALUE = 2;

        final double APPROXIMATE_COUNT_EXPECTED_ITERATIONS_PER_UPDATE = 128;

        // Prepare line chart
        stage.setTitle("Expected value proof of concept");
        final NumberAxis xAXIS = new NumberAxis();
        final NumberAxis yAXIS = new NumberAxis();
        xAXIS.setLabel("True count");
        yAXIS.setLabel("Algorithm estimate");
        final LineChart<Number, Number> LINE_CHART = new LineChart<>(xAXIS, yAXIS);
        LINE_CHART.setTitle("Expected value proof of concept");
        LINE_CHART.setAnimated(false);
        LINE_CHART.setCreateSymbols(false);

        // Prepare series data on line chart for our counters
        XYChart.Series<Number, Number> basicCounterLine = new XYChart.Series<>();
        basicCounterLine.setName("Basic Counter");
        XYChart.Series<Number, Number> basicApproximateCounterLine = new XYChart.Series<>();
        basicApproximateCounterLine.setName("Approximate Counter");
        XYChart.Series<Number, Number> morrisCounterLine = new XYChart.Series<>();
        morrisCounterLine.setName("Morris Counter");
        LINE_CHART.getData().add(basicCounterLine);
        LINE_CHART.getData().add(basicApproximateCounterLine);
        LINE_CHART.getData().add(morrisCounterLine);

        // Create our counters
        ArrayList<BasicApproximateCounter> basicApproximateCounters = new ArrayList<>();
        ArrayList<MorrisCounter> morrisCounters = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_COUNTERS; i++) {
            basicApproximateCounters.add(new BasicApproximateCounter(APPROXIMATE_COUNT_EXPECTED_ITERATIONS_PER_UPDATE));
            morrisCounters.add(new MorrisCounter(MORRIS_B_VALUE));
        }

        new AnimationTimer() {
            private long count;

            @Override
            public void handle(long current) {
                long basicApproximateSum = 0;
                long morrisSum = 0;

                if (count > COUNT_TO_VALUE) {
                    return;
                }

                // Retrieve counter values and update
                for (int i = 0; i < NUMBER_OF_COUNTERS; i++) {
                    BasicApproximateCounter bac = basicApproximateCounters.get(i);
                    basicApproximateSum += bac.getCount();
                    MorrisCounter mc = morrisCounters.get(i);
                    morrisSum += mc.getCount();
                    for (int j = 0; j < UPDATES_PER_FRAME; j++) {
                        bac.update();
                        mc.update();
                    }
                }


                // Find average for our counters
                double basicApproximateAverage = (double) basicApproximateSum / NUMBER_OF_COUNTERS;
                double morrisAverage = (double) morrisSum / NUMBER_OF_COUNTERS;


                // Add to line chart and visualise
                basicCounterLine.getData().add(new XYChart.Data<>(count, count));
                basicApproximateCounterLine.getData().add(new XYChart.Data<>(count, basicApproximateAverage));
                morrisCounterLine.getData().add(new XYChart.Data<>(count, morrisAverage));

                count += UPDATES_PER_FRAME;
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