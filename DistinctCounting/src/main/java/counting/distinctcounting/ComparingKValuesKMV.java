package counting.distinctcounting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ComparingKValuesKMV extends Application {
    @Override
    public void start(Stage stage) {

        Random rand = new Random();

        final int[] K_VALUES = {50000};

        final int NUMBER_OF_KMVS = 500;

        final long DISTINCT_COUNT = 100000000;

        final int UPDATES_PER_FRAME = 1000;

        stage.setTitle("Comparing k values - KMV");
        final NumberAxis distinctItems = new NumberAxis();
        final NumberAxis algorithmEstimate = new NumberAxis();
        distinctItems.setLabel("Distinct Items");
        algorithmEstimate.setLabel("Algorithm percentage error");
        final LineChart<Number, Number> LINE_CHART = new LineChart<>(distinctItems, algorithmEstimate);
        LINE_CHART.setTitle("Comparing k values - KMV");
        LINE_CHART.setAnimated(false);
        LINE_CHART.setCreateSymbols(false);

        ArrayList<XYChart.Series<Number,Number>> kmvLines = new ArrayList<>();
        ArrayList<ArrayList<KMV>> kmvs = new ArrayList<>();

        for (int i = 0; i < K_VALUES.length; i++) {
            XYChart.Series<Number, Number> kmvLine = new XYChart.Series<>();
            kmvLine.setName(Integer.toString(K_VALUES[i]));
            kmvLines.add(kmvLine);
            LINE_CHART.getData().add(kmvLine);
            kmvs.add(new ArrayList<>());
            for (int j = 0; j < NUMBER_OF_KMVS; j++) {
                kmvs.get(i).add(new KMV(K_VALUES[i]));
            }
        }

        BasicDistinctCounting trueDistinctCount = new BasicDistinctCounting();

        new AnimationTimer() {
            private long distinctCount = 0;

            @Override
            public void handle(long current) {

                if (distinctCount > DISTINCT_COUNT) {
                    return;
                }


                ArrayList<Double> kmvPercentageErrors = new ArrayList<>();

                for (int i = 0; i < K_VALUES.length; i++) {
                    kmvPercentageErrors.add(0D);
                }

                ArrayList<Long> randomNumbersToAdd = new ArrayList<>();
                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    randomNumbersToAdd.add(rand.nextLong(10000000L));
                }

                for (int i = 0; i < K_VALUES.length; i++) {
                    ArrayList<KMV> kmvArray = kmvs.get(i);
                    for (int j = 0; j < NUMBER_OF_KMVS; j++) {
                        KMV kmv = kmvArray.get(j);
                        long estimate = kmv.query();
                        double percentageError = 0;
                        long distinctCount = trueDistinctCount.query();
                        if (distinctCount > 0) {
                            percentageError = (double) Math.abs(distinctCount - estimate) / distinctCount * 100;
                        }

                        kmvPercentageErrors.set(i, kmvPercentageErrors.get(i) + percentageError);

                        for (int k = 0; k < UPDATES_PER_FRAME; k++) {
                            kmv.update(randomNumbersToAdd.get(k));
                        }
                    }
                }

                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    trueDistinctCount.update(randomNumbersToAdd.get(i));
                }

                for (int i = 0; i < K_VALUES.length; i++) {
                    double percentageError = kmvPercentageErrors.get(i) / NUMBER_OF_KMVS;
                    kmvLines.get(i).getData().add(new XYChart.Data<>(trueDistinctCount.query(), percentageError));
                }

                distinctCount = trueDistinctCount.query();
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