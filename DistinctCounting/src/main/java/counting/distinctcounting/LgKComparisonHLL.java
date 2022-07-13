package counting.distinctcounting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import org.apache.datasketches.hll.HllSketch;

import java.util.ArrayList;
import java.util.Random;

public class LgKComparisonHLL extends Application {

    @Override
    public void start(Stage stage) {

        Random rand = new Random();

        // Set the LgK values for our HLL sketches
        final int[] lgKValues = {4, 6, 8};

        // Set the number of distinct items we will count to until the algorithm terminates.
        final long DISTINCT_COUNT = 10000000000L;

        // Set the max number that our random number generator can generate. In order for the algorithm to work, this
        // has to be set higher than the DISTINCT_COUNT number.
        final long UPPER_LIMIT_NUM_TO_ADD = 150000000000L;

        // Set the number of updates made to our KMVs before refreshing the graph visualisation. The smaller this is,
        // the more detail that can be seen in the results. However, it will take the program much longer to arrive at
        // large count values
        final int UPDATES_PER_FRAME = 100000;

        // Prepare line charts
        stage.setTitle("Comparing accuracy of lgK values - HLL");
        final NumberAxis distinctItems = new NumberAxis();
        final NumberAxis algorithmEstimate = new NumberAxis();
        distinctItems.setLabel("Distinct Items");
        algorithmEstimate.setLabel("Algorithm percentage error");
        final LineChart<Number, Number> LINE_CHART_ACCURACY = new LineChart<>(distinctItems, algorithmEstimate);
        LINE_CHART_ACCURACY.setTitle("Comparing accuracy of lgK values - HLL");
        LINE_CHART_ACCURACY.setAnimated(false);
        LINE_CHART_ACCURACY.setCreateSymbols(false);

        Stage secondStage = new Stage();
        secondStage.setTitle("Comparing space usage of lgK values");
        final NumberAxis distinctItems2 = new NumberAxis();
        final NumberAxis spaceUsed = new NumberAxis();
        distinctItems2.setLabel("Distinct Items");
        spaceUsed.setLabel("Space used");
        final LineChart<Number, Number> LINE_CHART_SPACE = new LineChart<>(distinctItems2, spaceUsed);
        LINE_CHART_SPACE.setTitle("Comparing space usage of implementations of lgK values");
        LINE_CHART_SPACE.setAnimated(false);
        LINE_CHART_SPACE.setCreateSymbols(false);

        // Create array lists to store our lines and HLL sketches
        ArrayList<XYChart.Series<Number, Number>> lgKAccuracyLines = new ArrayList<>();
        ArrayList<XYChart.Series<Number, Number>> lgKSpaceLines = new ArrayList<>();

        ArrayList<HllSketch> hllSketches = new ArrayList<>();

        // Create our lines and sketches
        for (int i = 0; i < lgKValues.length; i++) {
            XYChart.Series<Number, Number> accuracyLine = new XYChart.Series<>();
            accuracyLine.setName("lgK " + lgKValues[i]);
            LINE_CHART_ACCURACY.getData().add(accuracyLine);
            lgKAccuracyLines.add(accuracyLine);

            XYChart.Series<Number, Number> spaceLine = new XYChart.Series<>();
            spaceLine.setName("lgK " + lgKValues[i]);
            LINE_CHART_SPACE.getData().add(spaceLine);
            lgKSpaceLines.add(spaceLine);

            hllSketches.add(new HllSketch(lgKValues[i]));
        }

        // Set up basic true distinct count
        BasicDistinctCounting trueDistinctCount = new BasicDistinctCounting();

        new AnimationTimer() {
            private long currentCount;

            @Override
            public void handle(long current) {

                // Terminate when our current count is greater than the count limit we set earlier
                if (currentCount > DISTINCT_COUNT) {
                    return;
                }

                // Update our line charts
                for (int i = 0; i < lgKValues.length; i++) {
                    lgKAccuracyLines.get(i).getData().add(new XYChart.Data<>(currentCount,
                            (currentCount == 0) ? 0 : Math.abs(hllSketches.get(i).getEstimate() - currentCount) / currentCount * 100));

                    lgKSpaceLines.get(i).getData().add(new XYChart.Data<>(trueDistinctCount.query(),
                            hllSketches.get(i).getCompactSerializationBytes()));
                }

                // Update our sketches with randomly generated numbers
                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    long randomNumber = rand.nextLong(UPPER_LIMIT_NUM_TO_ADD);
                    trueDistinctCount.update(randomNumber);
                    for (int j = 0; j < lgKValues.length; j++) {
                        hllSketches.get(j).update(randomNumber);
                    }
                }

                currentCount = trueDistinctCount.query();
            }
        }.start();

        Scene scene = new Scene(LINE_CHART_ACCURACY, 800, 600);
        stage.setScene(scene);
        stage.show();

        Scene scene2 = new Scene(LINE_CHART_SPACE, 800, 600);
        secondStage.setScene(scene2);
        secondStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
