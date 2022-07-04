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
        final double[] B_VALUES = {1.001, 1.1, 2};

        // Set the number of counters used for finding the average. If we set this to 100, we run 100 Morris and
        // Approximate Counters simultaneously and take their average.
        final int NUMBER_OF_COUNTERS = 1;

        // Set the value to which our counters count to.
        final long COUNT_TO_VALUE = 13000000;

        // Set the number of updates made to our counters before refreshing the graph visualisation.
        final int UPDATES_PER_FRAME = 1000;

        // Prepare line chart
        stage.setTitle("Comparing B values in Morris Counter");
        final NumberAxis xAXIS = new NumberAxis();
        final NumberAxis yAXIS = new NumberAxis();
        xAXIS.setLabel("True count");
        yAXIS.setLabel("Algorithm estimate");
        final LineChart<Number, Number> LINE_CHART = new LineChart<>(xAXIS, yAXIS);
        LINE_CHART.setTitle("Comparing B values in Morris Counter");
        LINE_CHART.setAnimated(false);
        LINE_CHART.setCreateSymbols(false);

        Stage secondStage = new Stage();
        secondStage.setTitle("Comparing updates per B value");
        final NumberAxis xAXIS2 = new NumberAxis();
        final NumberAxis yAXIS2 = new NumberAxis();
        xAXIS2.setLabel("True count");
        yAXIS2.setLabel("Number of updates");
        final LineChart<Number, Number> LINE_CHART2 = new LineChart<>(xAXIS2, yAXIS2);
        LINE_CHART2.setTitle("Comparing updates per B value");
        LINE_CHART2.setAnimated(false);
        LINE_CHART2.setCreateSymbols(false);

        Stage thirdStage = new Stage();
        thirdStage.setTitle("Comparing relative error");
        final NumberAxis xAXIS3 = new NumberAxis();
        final NumberAxis yAXIS3 = new NumberAxis();
        xAXIS3.setLabel("Items seen");
        yAXIS3.setLabel("Relative error");
        final LineChart<Number, Number> LINE_CHART3 = new LineChart<>(xAXIS3, yAXIS3);
        LINE_CHART3.setTitle("Comparing relative error");
        LINE_CHART3.setAnimated(false);
        LINE_CHART3.setCreateSymbols(false);

        ArrayList<XYChart.Series<Number,Number>> morrisLines = new ArrayList<>();
        ArrayList<XYChart.Series<Number, Number>> updateLines = new ArrayList<>();
        ArrayList<XYChart.Series<Number, Number>> relativeErrorLines = new ArrayList<>();
        ArrayList<ArrayList<MorrisCounter>> morrisCounters = new ArrayList<>();


        for (int i = 0; i < B_VALUES.length; i++) {
            XYChart.Series<Number, Number> morrisLine = new XYChart.Series<>();
            morrisLine.setName(Double.toString(B_VALUES[i]));
            XYChart.Series<Number, Number> lineUpdate = new XYChart.Series<>();
            lineUpdate.setName(B_VALUES[i] + " updates");
            XYChart.Series<Number, Number> relativeErrorLine = new XYChart.Series<>();
            relativeErrorLine.setName(B_VALUES[i] + "relative error");
            morrisLines.add(morrisLine);
            updateLines.add(lineUpdate);
            relativeErrorLines.add(relativeErrorLine);
            LINE_CHART.getData().add(morrisLine);
            LINE_CHART2.getData().add(lineUpdate);
            LINE_CHART3.getData().add(relativeErrorLine);
            morrisCounters.add(new ArrayList<>());
            for (int j = 0; j < NUMBER_OF_COUNTERS; j++) {
                morrisCounters.get(i).add(new MorrisCounter(B_VALUES[i]));
            }
        }

        XYChart.Series<Number, Number> basicCounterLine = new XYChart.Series<>();
        basicCounterLine.setName("Basic Counter");
        LINE_CHART.getData().add(basicCounterLine);

        new AnimationTimer() {
            private long count;

            @Override
            public void handle(long current) {

                if (count > COUNT_TO_VALUE) {
                    return;
                }

                ArrayList<Long> sums = new ArrayList<>();
                ArrayList<Long> updates = new ArrayList<>();
                for (int i = 0; i < B_VALUES.length; i++) {
                    sums.add(0L);
                    updates.add(0L);
                }

                for (int i = 0; i < B_VALUES.length; i++) {
                    ArrayList<MorrisCounter> mcs = morrisCounters.get(i);
                    for (int j = 0; j < NUMBER_OF_COUNTERS; j++) {
                        MorrisCounter mc = mcs.get(j);
                        long mcCount = mc.getCount();
                        long updateCount = mc.getTimesUpdated();
                        sums.set(i, sums.get(i) + mcCount);
                        updates.set(i, updates.get(i) + updateCount);

                        for (int k = 0; k < UPDATES_PER_FRAME; k++) {
                            mc.update();
                        }
                    }
                }



                for (int i = 0; i < B_VALUES.length; i++) {
                    double averageCount = (double) sums.get(i) / NUMBER_OF_COUNTERS;
                    double averageUpdates = (double) updates.get(i);
                    double relativeError = ((averageCount - count) / count) * 100;
                    morrisLines.get(i).getData().add(new XYChart.Data<>(count, averageCount));
                    updateLines.get(i).getData().add(new XYChart.Data<>(count, averageUpdates));
                    relativeErrorLines.get(i).getData().add(new XYChart.Data<>(count, (double) Math.round(relativeError * 100) / 100));
                }

                basicCounterLine.getData().add(new XYChart.Data<>(count, count));

                count += UPDATES_PER_FRAME;
            }
        }.start();

        Scene scene = new Scene(LINE_CHART, 800, 600);
        stage.setScene(scene);
        stage.show();

        Scene scene2 = new Scene(LINE_CHART2, 800, 600);
        secondStage.setScene(scene2);
        secondStage.show();

        Scene scene3 = new Scene(LINE_CHART3, 800, 600);
        thirdStage.setScene(scene3);
        thirdStage.show();
    }




    public static void main(String[] args) {
        launch();
    }
}

