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

        final int K_VALUE = 10000;

        final long DISTINCT_COUNT = 100000000;

        final int UPDATES_PER_FRAME = 100000;

        stage.setTitle("Comparing accuracy of implementations of distinct counting");
        final NumberAxis distinctItems = new NumberAxis();
        final NumberAxis algorithmEstimate = new NumberAxis();
        distinctItems.setLabel("Distinct Items");
        algorithmEstimate.setLabel("Algorithm percentage error");
        final LineChart<Number, Number> LINE_CHART = new LineChart<>(distinctItems, algorithmEstimate);
        LINE_CHART.setTitle("Comparing accuracy of implementations of distinct counting");
        LINE_CHART.setAnimated(false);
        LINE_CHART.setCreateSymbols(false);

        Stage secondStage = new Stage();
        secondStage.setTitle("Comparing space usage of implementations of distinct counting");
        final NumberAxis distinctItems2 = new NumberAxis();
        final NumberAxis spaceUsed = new NumberAxis();
        distinctItems2.setLabel("Distinct Items");
        spaceUsed.setLabel("Space used");
        final LineChart<Number, Number> LINE_CHART2 = new LineChart<>(distinctItems2, spaceUsed);
        LINE_CHART2.setTitle("Comparing space usage of implementations of distinct counting");
        LINE_CHART2.setAnimated(false);
        LINE_CHART2.setCreateSymbols(false);

        XYChart.Series<Number, Number> kmvAccuracy = new XYChart.Series<>();
        kmvAccuracy.setName("KMV Accuracy");
        LINE_CHART.getData().add(kmvAccuracy);
        XYChart.Series<Number, Number> kmvSpace = new XYChart.Series<>();
        kmvSpace.setName("KMV Space");
        LINE_CHART2.getData().add(kmvSpace);
        XYChart.Series<Number, Number> cpcAccuracy = new XYChart.Series<>();
        cpcAccuracy.setName("CPC Accuracy");
        LINE_CHART.getData().add(cpcAccuracy);
        XYChart.Series<Number, Number> cpcSpace = new XYChart.Series<>();
        cpcSpace.setName("CPC Space");
        LINE_CHART2.getData().add(cpcSpace);
        XYChart.Series<Number, Number> hllAccuracy = new XYChart.Series<>();
        hllAccuracy.setName("HLL Accuracy");
        LINE_CHART.getData().add(hllAccuracy);
        XYChart.Series<Number, Number> hllSpace = new XYChart.Series<>();
        hllSpace.setName("HLL Space");
        LINE_CHART2.getData().add(hllSpace);
        XYChart.Series<Number, Number> thetaAccuracy = new XYChart.Series<>();
        thetaAccuracy.setName("Theta Accuracy");
        LINE_CHART.getData().add(thetaAccuracy);
        XYChart.Series<Number, Number> thetaSpace = new XYChart.Series<>();
        thetaSpace.setName("Theta Space");
        LINE_CHART2.getData().add(thetaSpace);

        UpdateSketch thetaSketch = UpdateSketch.builder().build();
        HllSketch hllSketch = new HllSketch(16);
        PairwiseKMV kmvSketch = new PairwiseKMV(K_VALUE);
        CpcSketch cpcSketch = new CpcSketch(16);

        BasicDistinctCounting trueDistinctCount = new BasicDistinctCounting();

        new AnimationTimer() {
            private long count;

            @Override
            public void handle(long current) {

                if (count >= DISTINCT_COUNT) {
                    return;
                }

                for (int i = 0; i < UPDATES_PER_FRAME; i++) {
                    long randomNumber = rand.nextLong(10000000000L);
                    thetaSketch.update(randomNumber);
                    hllSketch.update(randomNumber);
                    kmvSketch.update(randomNumber);
                    cpcSketch.update(randomNumber);
                    trueDistinctCount.update(randomNumber);

                }



                thetaAccuracy.getData().add(new XYChart.Data<>(trueDistinctCount.query(), Math.abs(thetaSketch.getEstimate() - trueDistinctCount.query()) / trueDistinctCount.query() * 100));
                thetaSpace.getData().add(new XYChart.Data<>(trueDistinctCount.query(), thetaSketch.getCurrentBytes()));

                hllAccuracy.getData().add(new XYChart.Data<>(trueDistinctCount.query(), Math.abs(hllSketch.getEstimate() - trueDistinctCount.query()) / trueDistinctCount.query() * 100));
                hllSpace.getData().add(new XYChart.Data<>(trueDistinctCount.query(), hllSketch.getCompactSerializationBytes()));

                double percentageError = (double) Math.abs(trueDistinctCount.query() - kmvSketch.query()) / trueDistinctCount.query() * 100;

                kmvAccuracy.getData().add(new XYChart.Data<>(trueDistinctCount.query(), percentageError));
                kmvSpace.getData().add(new XYChart.Data<>(trueDistinctCount.query(), kmvSketch.getBytesUsed()));

                cpcAccuracy.getData().add(new XYChart.Data<>(trueDistinctCount.query(), Math.abs(cpcSketch.getEstimate() - trueDistinctCount.query()) / trueDistinctCount.query() * 100));
                cpcSpace.getData().add(new XYChart.Data<>(trueDistinctCount.query(), (cpcSketch.toByteArray()).length));
                count = trueDistinctCount.query();

            }
        }.start();

        Scene scene = new Scene(LINE_CHART, 800, 600);
        stage.setScene(scene);
        stage.show();

        Scene scene2 = new Scene(LINE_CHART2, 800, 600);
        secondStage.setScene(scene2);
        secondStage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}
