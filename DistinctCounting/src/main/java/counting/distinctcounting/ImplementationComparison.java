package counting.distinctcounting;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.datasketches.cpc.CpcSketch;
import org.apache.datasketches.hll.HllSketch;
import java.util.Random;

/** This class creates a visualisation of the KMV algorithm alongside instances of Apache Datasketches' CPC
 * and HLL implementations. This class creates three charts:
 * 1) A dynamic visualisation of the estimates of the KMV, CPC and HLL instances
 * 2) A dynamic visualisation of the percentage error of the KMV, CPC and HLL instances
 * 3) A dynamic visualsation of the space usage of the KMV, CPC and HLL instances
 *
 * Unfortunately, because of implementation details in the DataSketches build, only single instances of the HLL
 * and CPC can be run. This means there could be significant variances in the performance of the algorithms each
 * time the program is run
 * **/

public class ImplementationComparison extends Application {

    @Override
    public void start(Stage stage) {

        Random rand = new Random();

        // Set the number of items we will store in our priority queue (the Kth value will be the item at the top
        // of the heap). The bigger this value, the more accurate our algorithm should be.
        final int KMV_K_VALUE = 1000000;

        // Set the LgK value for our CPC sketch (between 4 and 26)
        final int CPC_LGK_VALUE = 21;

        // Set the LgK value for our HyperLogLog sketch (between 4 and 21)
        final int HLL_LGK_VALUE = 21;

        // Set the number of distinct items we will count to until the algorithm terminates
        final long DISTINCT_COUNT = 250000000;

        // Set the max number that our random number generator can generate. In order for the algorithm to work, this
        // has to be set higher than the DISTINCT_COUNT number.
        final long UPPER_LIMIT_NUM_TO_ADD = 15000000000L;

        // Set the number of updates made to our KMVs before refreshing the graph visualisation. The smaller this is,
        // the more detail that can be seen in the results. However, it will take the program much longer to arrive at
        // large count values
        final int UPDATES_PER_FRAME = 10000;

        // Prepare line charts
        stage.setTitle("Comparing percentage error of implementations of distinct counting");
        final NumberAxis distinctItems = new NumberAxis();
        distinctItems.tickLabelFontProperty().set(Font.font(20));
        final NumberAxis percentageError = new NumberAxis();
        percentageError.tickLabelFontProperty().set(Font.font(20));
        distinctItems.setLabel("Distinct Items");
        percentageError.setLabel("Algorithm percentage error");
        final LineChart<Number, Number> LINE_CHART_ACCURACY = new LineChart<>(distinctItems, percentageError);
        LINE_CHART_ACCURACY.setStyle("-fx-font-size: " + 24 + "px;");
        LINE_CHART_ACCURACY.setTitle("Comparing percentage error of implementations of distinct counting");
        LINE_CHART_ACCURACY.setAnimated(false);
        LINE_CHART_ACCURACY.setCreateSymbols(false);

        Stage secondStage = new Stage();
        secondStage.setTitle("Comparing space usage of implementations of distinct counting");
        final NumberAxis distinctItems2 = new NumberAxis();
        distinctItems2.tickLabelFontProperty().set(Font.font(20));
        final NumberAxis spaceUsed = new NumberAxis();
        spaceUsed.tickLabelFontProperty().set(Font.font(20));
        distinctItems2.setLabel("Distinct Items");
        spaceUsed.setLabel("Space used");
        final LineChart<Number, Number> LINE_CHART_SPACE = new LineChart<>(distinctItems2, spaceUsed);
        LINE_CHART_SPACE.setStyle("-fx-font-size: " + 24 + "px;");
        LINE_CHART_SPACE.setTitle("Comparing space usage of implementations of distinct counting");
        LINE_CHART_SPACE.setAnimated(false);
        LINE_CHART_SPACE.setCreateSymbols(false);

        Stage thirdStage = new Stage();
        thirdStage.setTitle("Comparing estimates of implementations of distinct counting");
        final NumberAxis distinctItems3 = new NumberAxis();
        distinctItems3.tickLabelFontProperty().set(Font.font(20));
        final NumberAxis algorithmEstimate = new NumberAxis();
        algorithmEstimate.tickLabelFontProperty().set(Font.font(20));
        distinctItems3.setLabel("Distinct Items");
        algorithmEstimate.setLabel("Algorithm estimate");
        final LineChart<Number, Number> LINE_CHART_ESTIMATE = new LineChart<>(distinctItems3, algorithmEstimate);
        LINE_CHART_ESTIMATE.setStyle("-fx-font-size: " + 24 + "px;");
        LINE_CHART_ESTIMATE.setTitle("Comparing estimates of implementations of distinct counting");
        LINE_CHART_ESTIMATE.setAnimated(false);
        LINE_CHART_ESTIMATE.setCreateSymbols(false);

        // Add lines to line charts for each of our sketches
        XYChart.Series<Number, Number> kmvAccuracy = new XYChart.Series<>();
        kmvAccuracy.setName("KMV Accuracy");
        LINE_CHART_ACCURACY.getData().add(kmvAccuracy);

        XYChart.Series<Number, Number> kmvSpace = new XYChart.Series<>();
        kmvSpace.setName("KMV Space");
        LINE_CHART_SPACE.getData().add(kmvSpace);

        XYChart.Series<Number, Number> kmvEstimate = new XYChart.Series<>();
        kmvEstimate.setName("KMV Estimate");
        LINE_CHART_ESTIMATE.getData().add(kmvEstimate);

        XYChart.Series<Number, Number> cpcAccuracy = new XYChart.Series<>();
        cpcAccuracy.setName("CPC Accuracy");
        LINE_CHART_ACCURACY.getData().add(cpcAccuracy);

        XYChart.Series<Number, Number> cpcSpace = new XYChart.Series<>();
        cpcSpace.setName("CPC Space");
        LINE_CHART_SPACE.getData().add(cpcSpace);

        XYChart.Series<Number, Number> cpcEstimate = new XYChart.Series<>();
        cpcEstimate.setName("CPC Estimate");
        LINE_CHART_ESTIMATE.getData().add(cpcEstimate);

        XYChart.Series<Number, Number> hllAccuracy = new XYChart.Series<>();
        hllAccuracy.setName("HLL Accuracy");
        LINE_CHART_ACCURACY.getData().add(hllAccuracy);

        XYChart.Series<Number, Number> hllSpace = new XYChart.Series<>();
        hllSpace.setName("HLL Space");
        LINE_CHART_SPACE.getData().add(hllSpace);

        XYChart.Series<Number, Number> hllEstimate = new XYChart.Series<>();
        hllEstimate.setName("HLL Estimate");
        LINE_CHART_ESTIMATE.getData().add(hllEstimate);

        // Set up sketches
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
                double hllPercentageError = 0;
                double kmvPercentageError = 0;
                double cpcPercentageError = 0;
                long count = trueDistinctCount.query();

                // Calculate the percentage error for sketches
                if (count > 0) {
                    hllPercentageError = Math.abs(hllSketch.getEstimate() - count) / count * 100;
                    kmvPercentageError = (double) Math.abs(count - kmvSketch.query()) / count * 100;
                    cpcPercentageError = Math.abs(cpcSketch.getEstimate() - count) / count * 100;
                }

                //Plot on graphs for each sketch

                hllAccuracy.getData().add(new XYChart.Data<>(count, hllPercentageError));
                hllSpace.getData().add(new XYChart.Data<>(count, hllSketch.getCompactSerializationBytes()));
                hllEstimate.getData().add(new XYChart.Data<>(count, hllSketch.getEstimate()));

                kmvAccuracy.getData().add(new XYChart.Data<>(count, kmvPercentageError));
                kmvSpace.getData().add(new XYChart.Data<>(count, kmvSketch.getBytesUsed()));
                kmvEstimate.getData().add(new XYChart.Data<>(count, kmvSketch.query()));

                cpcAccuracy.getData().add(new XYChart.Data<>(count, cpcPercentageError));
                cpcSpace.getData().add(new XYChart.Data<>(count, (cpcSketch.toByteArray()).length));
                cpcEstimate.getData().add(new XYChart.Data<>(count, cpcSketch.getEstimate()));

                // Generate random numbers and update our sketches
                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    long randomNumber = rand.nextLong(UPPER_LIMIT_NUM_TO_ADD);
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

        Scene scene3 = new Scene(LINE_CHART_ESTIMATE, 800, 600);
        thirdStage.setScene(scene3);
        thirdStage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}
