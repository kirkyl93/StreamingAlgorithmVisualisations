package counting.distinctcounting;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.datasketches.cpc.CpcSketch;
import org.apache.datasketches.hll.HllSketch;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/** This class runs Apache DataSketches' implementations of HLL and CPC a user-defined number of times and plots the
 * median and 90th percentile error recorded. This class creates a single chart:
 * 1) A static visualisation of the median and 90th percentile errors as the true count increases
 *
 * Unfortunately it was not possible to provide a dynamic visualisation because of the implementation details of the
 * DataSketches builds. The final chart, once generated (it may take a while when NUMBER_OF_SKETCHES/NUMBER_OF_ITERATIONS
 * is large or when UPDATES_PER_ITERATION is low) should provide good information on the performance of the algorithms.
 * c
 */

public class MedianHLLCPCError extends Application {

    @Override
    public void start(Stage stage) {

        Random rand = new Random();

        // Set this value between 4 and 21. This controls the sketch memory usage for HLL and CPC (between 4 and 21)
        int LG_K_VALUE = 10;

        // Set the number of HLL and CPCs run simultaneously
        int NUMBER_OF_SKETCHES = 20;

        // Set the max number that our random number generator can generate. In order for the algorithm to work, this
        // has to be set higher than the DISTINCT_COUNT number.
        final long UPPER_LIMIT_NUM_TO_ADD = 15000000000L;

        // Set the number of updates made to our sketches before adding results to our graph. The smaller this is,
        // the more detail that can be seen in the results. However, it will take the program much longer to arrive at
        // large count values.
        final int UPDATES_PER_ITERATION = 10000;

        // Set the number of iterations to be run.
        final int NUMBER_OF_ITERATIONS = 1000;

        // Prepare line chart
        stage.setTitle("Median and 90th percentile line - CPC and HLL");
        final NumberAxis distinctItems = new NumberAxis();
        distinctItems.tickLabelFontProperty().set(Font.font(20));
        final NumberAxis algorithmEstimate = new NumberAxis();
        algorithmEstimate.tickLabelFontProperty().set(Font.font(20));
        distinctItems.setLabel("Distinct Items");
        algorithmEstimate.setLabel("Algorithm percentage error");
        final LineChart<Number, Number> LINE_CHART = new LineChart<>(distinctItems, algorithmEstimate);
        LINE_CHART.setStyle("-fx-font-size: " + 24 + "px;");
        LINE_CHART.setTitle("Median and 90th percentile line - CPC and HLL");
        LINE_CHART.setAnimated(false);
        LINE_CHART.setCreateSymbols(false);

        // Set up lines for median and 90th percentile error for both HLL and CPC
        XYChart.Series<Number, Number> HLLMedianLine = new XYChart.Series<>();
        HLLMedianLine.setName("HLL Median Line");
        XYChart.Series<Number, Number> HLLPercentile90Line = new XYChart.Series<>();
        HLLPercentile90Line.setName("HLL 90th Percentile Line");


        XYChart.Series<Number, Number> CPCMedianLine = new XYChart.Series<>();
        CPCMedianLine.setName("CPC Median Line");
        XYChart.Series<Number, Number> CPCPercentile90Line = new XYChart.Series<>();
        CPCPercentile90Line.setName("CPC 90th Percentile Line");

        // Set the first point on both lines to (0,0)
        HLLMedianLine.getData().add(new XYChart.Data<>(0, 0));
        HLLPercentile90Line.getData().add(new XYChart.Data<>(0, 0));

        CPCMedianLine.getData().add(new XYChart.Data<>(0, 0));
        CPCPercentile90Line.getData().add(new XYChart.Data<>(0, 0));

        // Set up arraylists to store absolute errors of our sketches
        ArrayList<ArrayList<Double>> allHLLAbsoluteErrors = new ArrayList<>(NUMBER_OF_ITERATIONS);
        ArrayList<ArrayList<Double>> allCPCAbsoluteErrors = new ArrayList<>(NUMBER_OF_ITERATIONS);
        ArrayList<Long> trueDistinctCounts = new ArrayList<>(NUMBER_OF_ITERATIONS);
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            allHLLAbsoluteErrors.add(new ArrayList<>(NUMBER_OF_SKETCHES));
            allCPCAbsoluteErrors.add(new ArrayList<>(NUMBER_OF_SKETCHES));
            trueDistinctCounts.add(0L);
        }

        // Set the median and 90th percentile position in array
        int medianValue = NUMBER_OF_SKETCHES / 2;
        int percentile90Value = (NUMBER_OF_SKETCHES / 10) * 9;

        long hllBytesUsed = 0L;
        long cpcBytesUsed = 0L;

        for (int i = 0; i < NUMBER_OF_SKETCHES; i++) {

            // Create a new sketch
            HllSketch hll = new HllSketch(LG_K_VALUE);
            CpcSketch cpc = new CpcSketch(LG_K_VALUE);
            BasicDistinctCountingHash trueCount = new BasicDistinctCountingHash();

            // Update hll, cpc and trueCount with random numbers
            for (int j = 0; j < NUMBER_OF_ITERATIONS; j++) {
                for (int k = 0; k < UPDATES_PER_ITERATION; k++) {
                    long randNum = rand.nextLong(UPPER_LIMIT_NUM_TO_ADD);
                    hll.update(randNum);
                    cpc.update(randNum);
                    trueCount.update(randNum);
                }

                // Store absolute errors in previously created arraylist
                allHLLAbsoluteErrors.get(j).add(Math.abs(hll.getEstimate() - trueCount.query()));
                allCPCAbsoluteErrors.get(j).add(Math.abs(cpc.getEstimate() - trueCount.query()));
                trueDistinctCounts.set(j, trueDistinctCounts.get(j) + trueCount.query());
            }

            hllBytesUsed += hll.getCompactSerializationBytes();
            cpcBytesUsed += cpc.toByteArray().length;
            System.out.println("Made it through " + i + " sketch");
            System.out.println("HLL is: " + hll.getCompactSerializationBytes());
            System.out.println("CPC is: " + cpc.toByteArray().length);
        }


        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            //Retrieve absolute errors and average true count
            ArrayList<Double> hllAbsoluteErrors = allHLLAbsoluteErrors.get(i);
            ArrayList<Double> cpcAbsoluteErrors = allCPCAbsoluteErrors.get(i);
            double trueCount = (double) trueDistinctCounts.get(i) / NUMBER_OF_SKETCHES;

            // Sort errors and find median and 90th percentile errors for HLL and CPC
            Collections.sort(hllAbsoluteErrors);
            Collections.sort(cpcAbsoluteErrors);

            double hllMedianPercentageError = hllAbsoluteErrors.get(medianValue) / trueCount * 100;
            double hllPercentile90PercentageError = hllAbsoluteErrors.get(percentile90Value) / trueCount * 100;
            double cpcMedianPercentageError = cpcAbsoluteErrors.get(medianValue) / trueCount * 100;
            double cpcPercentile90PercentageError = cpcAbsoluteErrors.get(percentile90Value) / trueCount * 100;

            //Update lines
            HLLMedianLine.getData().add(new XYChart.Data<>(trueCount, hllMedianPercentageError));
            HLLPercentile90Line.getData().add(new XYChart.Data<>(trueCount, hllPercentile90PercentageError));

            CPCMedianLine.getData().add(new XYChart.Data<>(trueCount, cpcMedianPercentageError));
            CPCPercentile90Line.getData().add(new XYChart.Data<>(trueCount, cpcPercentile90PercentageError));

            System.out.println("Added one of those points on line you know what I mean?");

        }

        System.out.println("HLL size is: " + (double) hllBytesUsed / NUMBER_OF_SKETCHES);
        System.out.println("CPC size is: " + (double) cpcBytesUsed / NUMBER_OF_SKETCHES);

        // Prepare scene
        LINE_CHART.getData().add(HLLMedianLine);
        LINE_CHART.getData().add(HLLPercentile90Line);

        LINE_CHART.getData().add(CPCMedianLine);
        LINE_CHART.getData().add(CPCPercentile90Line);


        Scene scene = new Scene(LINE_CHART, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

