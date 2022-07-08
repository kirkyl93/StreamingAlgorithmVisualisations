package counting.distinctcounting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class ComparingHashFunctionsKMV extends Application {

    @Override
    public void start(Stage stage) {

        Random rand = new Random();

        final int K_VALUE = 1000;

        final int NUMBER_OF_KMVS = 50;

        final long DISTINCT_COUNT = 100000000;

        final int UPDATES_PER_FRAME = 10000;

        stage.setTitle("Comparing hash functions - KMV");
        final NumberAxis distinctItems = new NumberAxis();
        final NumberAxis algorithmEstimate = new NumberAxis();
        distinctItems.setLabel("Distinct Items");
        algorithmEstimate.setLabel("Algorithm percentage error");
        final LineChart<Number, Number> LINE_CHART = new LineChart<>(distinctItems, algorithmEstimate);
        LINE_CHART.setTitle("Comparing hash functions - KMV");
        LINE_CHART.setAnimated(false);
        LINE_CHART.setCreateSymbols(false);

        ArrayList<PairwiseKMV> pairwiseKMVs = new ArrayList<>();
        ArrayList<FourwiseKMV> fourwiseKMVs = new ArrayList<>();

        XYChart.Series<Number, Number> pairWise = new XYChart.Series<>();
        pairWise.setName("Pairwise hash");
        LINE_CHART.getData().add(pairWise);

        XYChart.Series<Number, Number> fourWise = new XYChart.Series<>();
        fourWise.setName("Fourwise hash");
        LINE_CHART.getData().add(fourWise);


        for (int j = 0; j < NUMBER_OF_KMVS; j++) {
            pairwiseKMVs.add(new PairwiseKMV(K_VALUE));
            fourwiseKMVs.add(new FourwiseKMV(K_VALUE));
        }

        BasicDistinctCounting trueDistinctCount = new BasicDistinctCounting();

        new AnimationTimer() {
            private long distinctCount;

            @Override
            public void handle(long current) {
                if (distinctCount > DISTINCT_COUNT) {
                    return;
                }

                double twowiseErrorPercentage = 0;
                double fourwiseErrorPercentage = 0;

                ArrayList<Long> randomNumbersToAdd = new ArrayList<>();
                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    randomNumbersToAdd.add((rand.nextLong(10000000L)));
                }

                for (int i = 0; i < NUMBER_OF_KMVS; i++) {
                    PairwiseKMV pKMV = pairwiseKMVs.get(i);
                    FourwiseKMV fKMV = fourwiseKMVs.get(i);
                    long pEstimate = pKMV.query();
                    long fEstimate = fKMV.query();
                    long distinctCount = trueDistinctCount.query();
                    if (distinctCount > 0) {
                        twowiseErrorPercentage += (double) Math.abs(distinctCount - pEstimate) / distinctCount * 100;
                        fourwiseErrorPercentage += (double) Math.abs(distinctCount - fEstimate) / distinctCount * 100;
                    }

                    for (int j = 0; j < UPDATES_PER_FRAME; j++) {
                        pKMV.update(randomNumbersToAdd.get(j));
                        fKMV.update(randomNumbersToAdd.get(j));
                    }
                }

                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    trueDistinctCount.update(randomNumbersToAdd.get(i));
                }

                pairWise.getData().add(new XYChart.Data<>(trueDistinctCount.query(), twowiseErrorPercentage / NUMBER_OF_KMVS));
                fourWise.getData().add(new XYChart.Data<>(trueDistinctCount.query(), fourwiseErrorPercentage / NUMBER_OF_KMVS));

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
