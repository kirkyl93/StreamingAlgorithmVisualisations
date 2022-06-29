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

        // Set the number of counters used for finding the average. If we set this to 100, we run 100 Morris and
        // Approximate Counters simultaneously and take their average.
        final int NUMBER_OF_COUNTERS = 1;

        // Set the value to which our counters count to.
        final int COUNT_TO_VALUE = 1000;

        // Set the number of updates made to our counters before refreshing the graph visualisation.
        final int UPDATES_PER_FRAME = 1000;

        //Set the number of lines added to the graph.
        final int NUMBER_OF_LINES = 3;

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
        ArrayList<XYChart.Series<Number, Number>> morrisLines = new ArrayList<>();
        ArrayList<XYChart.Series<Number, Number>> updateLines = new ArrayList<>();
        ArrayList<ArrayList<MorrisCounter>> morrisCounters = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_LINES; i++) {
            XYChart.Series<Number, Number> morrisLine = new XYChart.Series<>();
            XYChart.Series<Number, Number> updateLine = new XYChart.Series<>();
            double b_value = 1 + ( (i + 1) * (double) 1 / NUMBER_OF_LINES);
            morrisLine.setName(Double.toString(b_value));
            updateLine.setName(b_value + " updates");
            morrisCounters.add(new ArrayList<>());
            for (int j = 0; j < NUMBER_OF_COUNTERS; j++) {
                morrisCounters.get(i).add(new MorrisCounter(b_value));
            }
            LINE_CHART.getData().add(morrisLine);
            LINE_CHART.getData().add(updateLine);
            morrisLines.add(morrisLine);
            updateLines.add(updateLine);
        }
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
                for (int i = 0; i < NUMBER_OF_LINES; i++) {
                    sums.add(0L);
                    updates.add(0L);
                }

                for (int i = 0; i < NUMBER_OF_LINES; i++) {
                    ArrayList<MorrisCounter> mcs = morrisCounters.get(i);
                    for (int j = 0; j < NUMBER_OF_COUNTERS; j++) {
                        MorrisCounter mc = mcs.get(j);
                        long mcCount = mc.getCount();
                        long updateCount = mc.getTimesUpdated();
                        sums.set(i, sums.get(i) + mcCount);
                        updates.set(i, updates.get(i) + updateCount);

                        System.out.println(mcCount);

                        for (int k = 0; k < UPDATES_PER_FRAME; k++) {
                            mc.update();
                        }
                    }
                }



                for (int i = 0; i < NUMBER_OF_LINES; i++) {
                    double averageCount = (double) sums.get(i) / NUMBER_OF_COUNTERS;
                    double averageUpdates = (double) updates.get(i) / NUMBER_OF_COUNTERS;
                    System.out.println(averageCount);
                    System.out.println(averageUpdates);
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

