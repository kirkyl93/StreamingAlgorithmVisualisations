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

        final int NUMBER_OF_COUNTERS = 100;
        final int COUNT_TO_VALUE = 50000;
        final int UPDATES_PER_FRAME = 30;
        final double MORRIS_B_VALUE = 1.1;
        final double APPROXIMATE_COUNT_VALUE = 128;

        stage.setTitle("Expected value proof of concept");
        final NumberAxis xAXIS = new NumberAxis();
        final NumberAxis yAXIS = new NumberAxis();
        xAXIS.setLabel("True count");
        yAXIS.setLabel("Algorithm estimate");
        final LineChart<Number, Number> LINE_CHART = new LineChart<>(xAXIS, yAXIS);
        LINE_CHART.setTitle("Expected value proof of concept");
        LINE_CHART.setAnimated(false);
        LINE_CHART.setCreateSymbols(false);
        XYChart.Series<Number, Number> basicCounterLine = new XYChart.Series<>();
        basicCounterLine.setName("Basic Counter");
        XYChart.Series<Number, Number> basicApproximateCounterLine = new XYChart.Series<>();
        basicApproximateCounterLine.setName("Approximate Counter");
        XYChart.Series<Number, Number> morrisCounterLine = new XYChart.Series<>();
        morrisCounterLine.setName("Morris Counter");
        LINE_CHART.getData().add(basicCounterLine);
        LINE_CHART.getData().add(basicApproximateCounterLine);
        LINE_CHART.getData().add(morrisCounterLine);

        ArrayList<BasicApproximateCounter> basicApproximateCounters = new ArrayList<>();
        ArrayList<MorrisCounter> morrisCounters = new ArrayList<>();


        for (int i = 0; i < NUMBER_OF_COUNTERS; i++) {
            basicApproximateCounters.add(new BasicApproximateCounter(APPROXIMATE_COUNT_VALUE));
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

                double basicApproximateAverage = (double) basicApproximateSum / NUMBER_OF_COUNTERS;
                double morrisAverage = (double) morrisSum / NUMBER_OF_COUNTERS;

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