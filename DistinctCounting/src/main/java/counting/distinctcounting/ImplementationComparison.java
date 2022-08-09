package counting.distinctcounting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import org.apache.datasketches.cpc.CpcSketch;
import org.apache.datasketches.hll.HllSketch;
import org.apache.datasketches.theta.UpdateSketch;

import java.util.Random;

public class ImplementationComparison extends Application {

    @Override
    public void start(Stage stage) {

        Random rand = new Random();

        // Set the number of items we will store in our priority queue (the Kth value will be the item at the top
        // of the heap). The bigger this value, the more accurate our algorithm should be.
        final int KMV_K_VALUE = 10000;

        // Set the LgK value for our CPC sketch
        final int CPC_LGK_VALUE = 16;

        // Set the LgK value for our HyperLogLog sketch
        final int HLL_LGK_VALUE = 16;

        // Set the number of distinct items we will count to until the algorithm terminates
        final long DISTINCT_COUNT = 100000000;

        // Set the max number that our random number generator can generate. In order for the algorithm to work, this
        // has to be set higher than the DISTINCT_COUNT number.
        final long UPPER_LIMIT_NUM_TO_ADD = 150000000;

        // Set the number of updates made to our KMVs before refreshing the graph visualisation. The smaller this is,
        // the more detail that can be seen in the results. However, it will take the program much longer to arrive at
        // large count values
        final int UPDATES_PER_FRAME = 100000;

        // Prepare line charts
        stage.setTitle("Comparing accuracy of implementations of distinct counting");
        final NumberAxis distinctItems = new NumberAxis();
        final NumberAxis algorithmEstimate = new NumberAxis();
        distinctItems.setLabel("Distinct Items");
        algorithmEstimate.setLabel("Algorithm percentage error");
        final LineChart<Number, Number> LINE_CHART_ACCURACY = new LineChart<>(distinctItems, algorithmEstimate);
        LINE_CHART_ACCURACY.setTitle("Comparing accuracy of implementations of distinct counting");
        LINE_CHART_ACCURACY.setAnimated(false);
        LINE_CHART_ACCURACY.setCreateSymbols(false);

        Stage secondStage = new Stage();
        secondStage.setTitle("Comparing space usage of implementations of distinct counting");
        final NumberAxis distinctItems2 = new NumberAxis();
        final NumberAxis spaceUsed = new NumberAxis();
        distinctItems2.setLabel("Distinct Items");
        spaceUsed.setLabel("Space used");
        final LineChart<Number, Number> LINE_CHART_SPACE = new LineChart<>(distinctItems2, spaceUsed);
        LINE_CHART_SPACE.setTitle("Comparing space usage of implementations of distinct counting");
        LINE_CHART_SPACE.setAnimated(false);
        LINE_CHART_SPACE.setCreateSymbols(false);

        // Add lines to line charts for each of our sketches
        XYChart.Series<Number, Number> kmvAccuracy = new XYChart.Series<>();
        kmvAccuracy.setName("KMV Accuracy");
        LINE_CHART_ACCURACY.getData().add(kmvAccuracy);
        XYChart.Series<Number, Number> kmvSpace = new XYChart.Series<>();
        kmvSpace.setName("KMV Space");
        LINE_CHART_SPACE.getData().add(kmvSpace);
        XYChart.Series<Number, Number> cpcAccuracy = new XYChart.Series<>();
        cpcAccuracy.setName("CPC Accuracy");
        LINE_CHART_ACCURACY.getData().add(cpcAccuracy);
        XYChart.Series<Number, Number> cpcSpace = new XYChart.Series<>();
        cpcSpace.setName("CPC Space");
        LINE_CHART_SPACE.getData().add(cpcSpace);
        XYChart.Series<Number, Number> hllAccuracy = new XYChart.Series<>();
        hllAccuracy.setName("HLL Accuracy");
        LINE_CHART_ACCURACY.getData().add(hllAccuracy);
        XYChart.Series<Number, Number> hllSpace = new XYChart.Series<>();
        hllSpace.setName("HLL Space");
        LINE_CHART_SPACE.getData().add(hllSpace);
        XYChart.Series<Number, Number> thetaAccuracy = new XYChart.Series<>();
        thetaAccuracy.setName("Theta Accuracy");
        LINE_CHART_ACCURACY.getData().add(thetaAccuracy);
        XYChart.Series<Number, Number> thetaSpace = new XYChart.Series<>();
        thetaSpace.setName("Theta Space");
        LINE_CHART_SPACE.getData().add(thetaSpace);

        // Set up sketches
        UpdateSketch thetaSketch = UpdateSketch.builder().build();
        HllSketch hllSketch = new HllSketch(HLL_LGK_VALUE);
        PairwiseKMV kmvSketch = new PairwiseKMV(KMV_K_VALUE);
        CpcSketch cpcSketch = new CpcSketch(CPC_LGK_VALUE);

        // Set up basic true distinct count
        BasicDistinctCountingHash trueDistinctCount = new BasicDistinctCountingHash();

        new AnimationTimer() {
            private long currentCount;

            @Override
            public void handle(long current) {

                // Terminate when our current count is greater than the count limit we set earlier
                if (currentCount > DISTINCT_COUNT) {
                    return;
                }

                // Initialise percentage error variables for our sketches
                double thetaPercentageError = 0;
                double hllPercentageError = 0;
                double kmvPercentageError = 0;
                double cpcPercentageError = 0;
                long count = trueDistinctCount.query();

                // Calculate the percentage error for sketches
                if (count > 0) {
                    thetaPercentageError = Math.abs(thetaSketch.getEstimate() - count) / count * 100;
                    hllPercentageError = Math.abs(hllSketch.getEstimate() - count) / count * 100;
                    kmvPercentageError = (double) Math.abs(count - kmvSketch.query()) / count * 100;
                    cpcPercentageError = Math.abs(cpcSketch.getEstimate() - count) / count * 100;
                }

                //Plot on graphs for each sketch
                thetaAccuracy.getData().add(new XYChart.Data<>(count, thetaPercentageError));
                thetaSpace.getData().add(new XYChart.Data<>(count, thetaSketch.getCurrentBytes()));

                hllAccuracy.getData().add(new XYChart.Data<>(count, hllPercentageError));
                hllSpace.getData().add(new XYChart.Data<>(count, hllSketch.getCompactSerializationBytes()));

                kmvAccuracy.getData().add(new XYChart.Data<>(count, kmvPercentageError));
                kmvSpace.getData().add(new XYChart.Data<>(count, kmvSketch.getBytesUsed()));

                cpcAccuracy.getData().add(new XYChart.Data<>(count, cpcPercentageError));
                cpcSpace.getData().add(new XYChart.Data<>(count, (cpcSketch.toByteArray()).length));

                // Generate random numbers and update our sketches
                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    long randomNumber = rand.nextLong(UPPER_LIMIT_NUM_TO_ADD);
                    thetaSketch.update(randomNumber);
                    hllSketch.update(randomNumber);
                    kmvSketch.update(randomNumber);
                    cpcSketch.update(randomNumber);
                    trueDistinctCount.update(randomNumber);
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
