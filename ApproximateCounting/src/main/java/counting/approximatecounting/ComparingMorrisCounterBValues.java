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
        final double[] B_VALUES = {1.05, 1.2, 1.5, 4};

        // Set the number of counters used for finding the average. If we set this to 100, we run 100 Morris and
        // Approximate Counters simultaneously and take their average.
        final int NUMBER_OF_COUNTERS = 1;

        // Set the value to which our counters count to.
        final long COUNT_TO_VALUE = 200000L;

        // Set the number of updates made to our counters before refreshing the graph visualisation.
        final int UPDATES_PER_FRAME = 100;

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

        XYChart.Series<Number, Number> basicCounterLine = new XYChart.Series<>();
        basicCounterLine.setName("Basic Counter");
        LINE_CHART.getData().add(basicCounterLine);


        ArrayList<XYChart.Series<Number,Number>> morrisLines = new ArrayList<>();
        ArrayList<XYChart.Series<Number, Number>> updateLines = new ArrayList<>();
        ArrayList<ArrayList<MorrisCounter>> morrisCounters = new ArrayList<>();


        for (int i = 0; i < B_VALUES.length; i++) {
            XYChart.Series<Number, Number> morrisLine = new XYChart.Series<>();
            morrisLine.setName(Double.toString(B_VALUES[i]));
            XYChart.Series<Number, Number> lineUpdate = new XYChart.Series<>();
            lineUpdate.setName(B_VALUES[i] + " updates");
            morrisLines.add(morrisLine);
            updateLines.add(lineUpdate);
            LINE_CHART.getData().add(morrisLine);
            LINE_CHART.getData().add(lineUpdate);
            morrisCounters.add(new ArrayList<>());
            for (int j = 0; j < NUMBER_OF_COUNTERS; j++) {
                morrisCounters.get(i).add(new MorrisCounter(B_VALUES[i]));
            }
        }

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
                    morrisLines.get(i).getData().add(new XYChart.Data<>(count, averageCount));
                    updateLines.get(i).getData().add(new XYChart.Data<>(count, averageUpdates));
                }

                basicCounterLine.getData().add(new XYChart.Data<>(count, count));

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

