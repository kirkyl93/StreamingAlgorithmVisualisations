package counting.approximatecounting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;


/** This class is a proof of concept that the expected values of the Morris Counter and approximate counters equal the
 * true count. The higher the number of counters entered, the closer the lines should run to the true count. The user can
 * edit input values to change the data being visualised. This class creates 2 charts:
 * 1) A dynamic visualisation of the percentage errors of the average query results of the Morris Counter and
 * BasicApproximateCounter
 * 2) A dynamic visualisation of the average of the algorithm estimates for the MorrisCounter and BasicApproximateCounters
 * plotted alongside the true count
 */


public class ExpectedValueProofOfConcept extends Application {
    @Override
    public void start(Stage stage) {

        // Set the number of counters used for finding the average. If we set this to 100, we run 100 Morris and
        // Approximate Counters simultaneously and take their average.
        final int NUMBER_OF_COUNTERS = 50000;

        // Set the value to which our counters count to.
        final long COUNT_TO_VALUE = 1000000;

        // Set the number of updates made to our counters before refreshing the graph visualisation.
        final int UPDATES_PER_FRAME = 100;

        // Set the b value in Morris counter (see MorrisCounter class)
        final double MORRIS_B_VALUE = 1.01;

        final double APPROXIMATE_COUNT_B_VALUE = 1000;

        // Prepare line charts
        stage.setTitle("Expected value proof of concept");
        final NumberAxis xAXIS = new NumberAxis();
        xAXIS.tickLabelFontProperty().set(Font.font(20));
        final NumberAxis yAXIS = new NumberAxis();
        yAXIS.tickLabelFontProperty().set(Font.font(20));
        xAXIS.setLabel("True count");
        yAXIS.setLabel("Algorithm estimate");
        final LineChart<Number, Number> LINE_CHART = new LineChart<>(xAXIS, yAXIS);
        LINE_CHART.setStyle("-fx-font-size: " + 24 + "px;");
        LINE_CHART.setTitle("Expected value proof of concept");
        LINE_CHART.setAnimated(false);
        LINE_CHART.setCreateSymbols(false);

        Stage stage2 = new Stage();
        stage2.setTitle("Percentage Error of average of Counter queries");
        final NumberAxis trueCount = new NumberAxis();
        trueCount.tickLabelFontProperty().set(Font.font(20));
        final NumberAxis percentageError = new NumberAxis();
        percentageError.tickLabelFontProperty().set(Font.font(20));
        trueCount.setLabel("True count");
        percentageError.setLabel("Percentage error");
        final LineChart<Number, Number> LINE_CHART_PERCENTAGE_ERROR = new LineChart<>(trueCount, percentageError);
        LINE_CHART_PERCENTAGE_ERROR.setStyle("-fx-font-size: " + 24 + "px;");
        LINE_CHART_PERCENTAGE_ERROR.setTitle("Percentage error of average of counter queries");
        LINE_CHART_PERCENTAGE_ERROR.setAnimated(false);
        LINE_CHART_PERCENTAGE_ERROR.setCreateSymbols(false);


        // Prepare series data on line chart for our counters
        XYChart.Series<Number, Number> basicCounterLine = new XYChart.Series<>();
        basicCounterLine.setName("Basic Counter");
        XYChart.Series<Number, Number> basicApproximateCounterLine = new XYChart.Series<>();
        basicApproximateCounterLine.setName("Approximate Counter");
        XYChart.Series<Number, Number> morrisCounterLine = new XYChart.Series<>();
        morrisCounterLine.setName("Morris Counter");
        LINE_CHART.getData().add(basicApproximateCounterLine);
        LINE_CHART.getData().add(morrisCounterLine);
        LINE_CHART.getData().add(basicCounterLine);

        XYChart.Series<Number, Number> basicApproximateCounterError = new XYChart.Series<>();
        basicApproximateCounterError.setName("Basic Approximate Counter error");
        XYChart.Series<Number, Number> morrisCounterError = new XYChart.Series<>();
        morrisCounterError.setName("Morris Counter error");
        LINE_CHART_PERCENTAGE_ERROR.getData().add(basicApproximateCounterError);
        LINE_CHART_PERCENTAGE_ERROR.getData().add(morrisCounterError);

        // Create our counters
        ArrayList<BasicApproximateCounter> basicApproximateCounters = new ArrayList<>();
        ArrayList<MorrisCounter> morrisCounters = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_COUNTERS; i++) {
            basicApproximateCounters.add(new BasicApproximateCounter(APPROXIMATE_COUNT_B_VALUE));
            morrisCounters.add(new MorrisCounter(MORRIS_B_VALUE));
        }

        new AnimationTimer() {
            private long count;

            @Override
            public void handle(long current) {

                // Terminate when our current count is greater than the count limit we set earlier
                if (count > COUNT_TO_VALUE) {
                    return;
                }
                long basicApproximateSum = 0;
                long morrisSum = 0;

                // Retrieve counter values and update
                for (int i = 0; i < NUMBER_OF_COUNTERS; i++) {
                    BasicApproximateCounter bac = basicApproximateCounters.get(i);
                    basicApproximateSum += bac.query();
                    MorrisCounter mc = morrisCounters.get(i);
                    morrisSum += mc.query();
                    for (int j = 0; j < UPDATES_PER_FRAME; j++) {
                        bac.update();
                        mc.update();
                    }
                }


                // Find average and percentage error of our counters
                double basicApproximatePercentageError = 0;
                double morrisCounterPercentageError = 0;
                double basicApproximateAverage = (double) basicApproximateSum / NUMBER_OF_COUNTERS;
                double morrisAverage = (double) morrisSum / NUMBER_OF_COUNTERS;
                if (count > 0) {
                    basicApproximatePercentageError = (basicApproximateAverage - count) / count * 100;
                    morrisCounterPercentageError = (morrisAverage - count) / count * 100;
                }

                // Add to line chart and visualise
                basicCounterLine.getData().add(new XYChart.Data<>(count, count));
                basicApproximateCounterLine.getData().add(new XYChart.Data<>(count, basicApproximateAverage));
                morrisCounterLine.getData().add(new XYChart.Data<>(count, morrisAverage));

                basicApproximateCounterError.getData().add(new XYChart.Data<>(count, basicApproximatePercentageError));
                morrisCounterError.getData().add(new XYChart.Data<>(count, morrisCounterPercentageError));

                count += UPDATES_PER_FRAME;
            }
        }.start();

        //Set scene
        Scene scene = new Scene(LINE_CHART, 800, 600);
        stage.setScene(scene);
        stage.show();

        Scene scene2 = new Scene(LINE_CHART_PERCENTAGE_ERROR, 800, 600);
        stage2.setScene(scene2);
        stage2.show();
    }


    public static void main(String[] args) {
        launch();
    }
}