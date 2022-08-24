package counting.frequencycounting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Random;

public class CountMinSketchesComparison extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        Random rand = new Random();

        final int NUMBER_OF_HASH_FUNCTIONS = 3;

        final int NUMBER_OF_SLOTS_PER_ROW = 7;

        final long UNIQUE_ITEMS_TO_ADD = 100000;

        final long MAX_ITEM_TO_ADD = 100000000;

        final double PROBABILITY_OF_LARGE_UPDATE_WEIGHT = 0.9;

        final int MAX_WEIGHT_TO_ADD = 7000;

        final int UPDATES_PER_FRAME = 1000;


        CountMinSketch cmOriginal = new CountMinSketchOriginal(NUMBER_OF_HASH_FUNCTIONS, NUMBER_OF_SLOTS_PER_ROW);
        CountMinSketch cmConservative = new CountMinSketchConservative(NUMBER_OF_HASH_FUNCTIONS, NUMBER_OF_SLOTS_PER_ROW);
        CountMinSketch cmMean = new CountMeanMinSketch(NUMBER_OF_HASH_FUNCTIONS, NUMBER_OF_SLOTS_PER_ROW);

        stage.setTitle("Comparing Count Min Sketches Performance");
        final NumberAxis distinctItems = new NumberAxis();
        final NumberAxis averageError = new NumberAxis();
        distinctItems.setLabel("Distinct Items");
        averageError.setLabel("Average error");
        final LineChart<Number, Number> LINE_CHART = new LineChart<>(distinctItems, averageError);
        LINE_CHART.setTitle("Comparing Count Min Sketches Performance");
        LINE_CHART.setAnimated(false);
        LINE_CHART.setCreateSymbols(false);

        XYChart.Series<Number, Number> cmOriginalLine = new XYChart.Series<>();
        cmOriginalLine.setName("cmOriginalLine");
        XYChart.Series<Number, Number> cmConservativeLine = new XYChart.Series<>();
        cmConservativeLine.setName("cmConservativeLine");
        XYChart.Series<Number, Number> cmMeanLine = new XYChart.Series<>();
        cmMeanLine.setName("cmMeanLine");

        LINE_CHART.getData().add(cmOriginalLine);
        LINE_CHART.getData().add(cmConservativeLine);
        LINE_CHART.getData().add(cmMeanLine);

        BasicFrequencyCounter trueFrequencyCounter = new BasicFrequencyCounter();

        new AnimationTimer() {
            private long currentDistinctCount;
            private long totalCount;
            private double maxError = 0;

            @Override
            public void handle(long current) {

                if (currentDistinctCount > UNIQUE_ITEMS_TO_ADD) {
                    return;
                }

                long cmOriginalAbsoluteError = 0;
                long cmConservativeAbsoluteError = 0;
                long cmMeanAbsoluteError = 0;

                System.out.println(totalCount);




                for (Map.Entry<Long, Long> set: trueFrequencyCounter.items.entrySet()) {
                    long keyWeight = set.getValue();
                    cmOriginalAbsoluteError += Math.abs(keyWeight - cmOriginal.query(set.getKey()));
                    cmConservativeAbsoluteError += Math.abs(keyWeight - cmConservative.query(set.getKey()));
                    cmMeanAbsoluteError += Math.abs(keyWeight - cmMean.query(set.getKey()));

                    System.out.println("total count is " + totalCount);
                    System.out.println("Cm original error is: " + Math.abs(keyWeight - cmOriginal.query(set.getKey())));
                }

                double cmOriginalAverageError = 0;
                double cmConservativeAverageError = 0;
                double cmMeanAverageError = 0;

                long currentCount = trueFrequencyCounter.items.size();
                if (currentCount > 0) {
                    cmOriginalAverageError = (double) cmOriginalAbsoluteError / currentCount;
                    cmConservativeAverageError = (double) cmConservativeAbsoluteError / currentCount;
                    cmMeanAverageError = (double) cmMeanAbsoluteError / currentCount;
                }

                cmOriginalLine.getData().add(new XYChart.Data<>(currentCount, cmOriginalAverageError));
                cmConservativeLine.getData().add(new XYChart.Data<>(currentCount, cmConservativeAverageError));
                cmMeanLine.getData().add(new XYChart.Data<>(currentCount, cmMeanAverageError));

                for (int i = 0; i < UPDATES_PER_FRAME; i++) {

                    long itemToAdd = rand.nextLong(MAX_ITEM_TO_ADD);

                    int itemWeight = 1;
                    if (rand.nextDouble() < PROBABILITY_OF_LARGE_UPDATE_WEIGHT) {
                        itemWeight = rand.nextInt(MAX_WEIGHT_TO_ADD);
                    }

                    totalCount += itemWeight;

                    trueFrequencyCounter.update(itemToAdd, itemWeight);
                    cmOriginal.update(itemToAdd, itemWeight);
                    cmConservative.update(itemToAdd, itemWeight);
                    cmMean.update(itemToAdd, itemWeight);

                }

                currentDistinctCount = trueFrequencyCounter.items.size();

            }
        }.start();

        Scene scene = new Scene(LINE_CHART, 800, 600);
        stage.setScene(scene);
        stage.show();



    }
}
