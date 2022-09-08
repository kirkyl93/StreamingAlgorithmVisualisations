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

/** This class visualises how our count-min sketches perform. It creates four charts:
 * 1) A dynamic visualisation of the max error percentage (in terms of L1-norm) of all the items added
 * 2) A dynamic visualisation of the average error percentage (in terms of L1-norm) of all items added
 * 3) A dynamic visualisation of the percentage of items that fail to deliver an answer within the epsilon level of accuracy.
 * This is directly compared with the delta output guarantee.
 * 4) A dynamic visualisation of the average absolute errors for all the items added
 * */

public class CountMinSketchesComparison extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        Random rand = new Random();

        // This sets the d value of the count-min sketch
        final int NUMBER_OF_HASH_FUNCTIONS = 7;

        // This sets the t value of the count-min sketch
        final int NUMBER_OF_SLOTS_PER_ROW = 200;

        // Set the number of times the update function is called
        final long UNIQUE_ITEMS_TO_ADD = 10000000;

        // Set the range (from 0) of the items to be potentially added
        final long MAX_ITEM_TO_ADD = 400000000;

        // Set the probability that an item has a weight > 1. 0.5 represents a 50% chance
        final double PROBABILITY_OF_LARGE_UPDATE_WEIGHT = 0.001;

        // In the case that the item is to have a weight > 1, set the upper limit of this weight to be added
        final int MAX_WEIGHT_TO_ADD = 40000000;

        // Set the number of update operations per frame update
        final int UPDATES_PER_FRAME = 5000;



        // Initialise our count-min sketches - we have a count min sketch original, a count-min conservative,
        // and a count-mean-min
        CountMinSketch cmOriginal = new CountMinSketchOriginal(NUMBER_OF_HASH_FUNCTIONS, NUMBER_OF_SLOTS_PER_ROW);
        CountMinSketch cmConservative = new CountMinSketchConservative(NUMBER_OF_HASH_FUNCTIONS, NUMBER_OF_SLOTS_PER_ROW);
        CountMinSketch cmMean = new CountMeanMinSketch(NUMBER_OF_HASH_FUNCTIONS, NUMBER_OF_SLOTS_PER_ROW);

        // Calculate the theoretical guarantees from the number of rows and columns
        double epsilonGuarantee = (double) 2 / NUMBER_OF_SLOTS_PER_ROW;
        double deltaGuarantee = Math.pow(0.5, NUMBER_OF_HASH_FUNCTIONS);
        double percentageGuarantee = deltaGuarantee * 100;

        // Set up line charts
        stage.setTitle("Comparing Count Min Sketches average absolute errors");
        final NumberAxis distinctItems = new NumberAxis();
        final NumberAxis averageError = new NumberAxis();
        distinctItems.setLabel("Distinct Items");
        averageError.setLabel("Average error");
        final LineChart<Number, Number> LINE_CHART_ABSOLUTE_ERROR = new LineChart<>(distinctItems, averageError);
        LINE_CHART_ABSOLUTE_ERROR.setTitle("Comparing Count Min Sketches average absolute errors");
        LINE_CHART_ABSOLUTE_ERROR.setAnimated(false);
        LINE_CHART_ABSOLUTE_ERROR.setCreateSymbols(false);

        Stage secondStage = new Stage();
        secondStage.setTitle("Average error as a percentage of total weights");
        final NumberAxis distinctItems2 = new NumberAxis();
        final NumberAxis percentage = new NumberAxis();
        distinctItems2.setLabel("Distinct Items");
        percentage.setLabel("Error");
        final LineChart<Number, Number> LINE_CHART_PERCENTAGE_TOTAL_WEIGHT = new LineChart<>(distinctItems2, percentage);
        LINE_CHART_PERCENTAGE_TOTAL_WEIGHT.setTitle("Average error as a percentage of total weights");
        LINE_CHART_PERCENTAGE_TOTAL_WEIGHT.setAnimated(false);
        LINE_CHART_PERCENTAGE_TOTAL_WEIGHT.setCreateSymbols(false);

        Stage thirdStage = new Stage();
        thirdStage.setTitle("Max error observed as a percentage of total weights");
        final NumberAxis distinctItems3 = new NumberAxis();
        final NumberAxis maxError = new NumberAxis();
        distinctItems3.setLabel("Distinct Items");
        maxError.setLabel("Max error");
        final LineChart<Number, Number> LINE_CHART_MAX_ERROR = new LineChart<>(distinctItems3, maxError);
        LINE_CHART_MAX_ERROR.setTitle("Max error observed as a percentage of total weights");
        LINE_CHART_MAX_ERROR.setAnimated(false);
        LINE_CHART_MAX_ERROR.setCreateSymbols(false);

        Stage fourthStage = new Stage();
        fourthStage.setTitle("Percentage of items with error levels above the guarantee");
        final NumberAxis distinctItems4 = new NumberAxis();
        final NumberAxis errorPercentage = new NumberAxis();
        distinctItems4.setLabel("Distinct Items");
        errorPercentage.setLabel("Percentage of items with estimates above epsilonW");
        final LineChart<Number, Number> LINE_CHART_ERROR_PERCENTAGE = new LineChart<>(distinctItems4, errorPercentage);
        LINE_CHART_ERROR_PERCENTAGE.setTitle("Percentage of items with error levels above the theoretical guarantee");
        LINE_CHART_ERROR_PERCENTAGE.setAnimated(false);
        LINE_CHART_ERROR_PERCENTAGE.setCreateSymbols(false);

        // Set up absolute error lines and add to relevant line chart
        XYChart.Series<Number, Number> cmOriginalAverageAbsoluteErrorLine = new XYChart.Series<>();
        cmOriginalAverageAbsoluteErrorLine.setName("cmOriginalLine");
        XYChart.Series<Number, Number> cmConservativeAverageAbsoluteErrorLine = new XYChart.Series<>();
        cmConservativeAverageAbsoluteErrorLine.setName("cmConservativeLine");
        XYChart.Series<Number, Number> cmMeanAverageAbsoluteErrorLine = new XYChart.Series<>();
        cmMeanAverageAbsoluteErrorLine.setName("cmMeanLine");

        LINE_CHART_ABSOLUTE_ERROR.getData().add(cmOriginalAverageAbsoluteErrorLine);
        LINE_CHART_ABSOLUTE_ERROR.getData().add(cmConservativeAverageAbsoluteErrorLine);
        LINE_CHART_ABSOLUTE_ERROR.getData().add(cmMeanAverageAbsoluteErrorLine);

        // Set up percentage error line and add to relevant line chart
        XYChart.Series<Number, Number> cmOriginalPercentageErrorLine = new XYChart.Series<>();
        cmOriginalPercentageErrorLine.setName("cmOriginalLine");
        XYChart.Series<Number, Number> cmConservativePercentageErrorLine = new XYChart.Series<>();
        cmConservativePercentageErrorLine.setName("cmConservativeLine");
        XYChart.Series<Number, Number> cmMeanPercentageErrorLine = new XYChart.Series<>();
        cmMeanPercentageErrorLine.setName("cmMeanLine");

        LINE_CHART_PERCENTAGE_TOTAL_WEIGHT.getData().add(cmOriginalPercentageErrorLine);
        LINE_CHART_PERCENTAGE_TOTAL_WEIGHT.getData().add(cmConservativePercentageErrorLine);
        LINE_CHART_PERCENTAGE_TOTAL_WEIGHT.getData().add(cmMeanPercentageErrorLine);

        // Set up max percentage error line and add to relevant line chart
        XYChart.Series<Number, Number> cmOriginalMaxErrorLine = new XYChart.Series<>();
        cmOriginalMaxErrorLine.setName("cmOriginalLine");
        XYChart.Series<Number, Number> cmConservativeMaxErrorLine = new XYChart.Series<>();
        cmConservativeMaxErrorLine.setName("cmConservativeLine");
        XYChart.Series<Number, Number> cmMeanMaxErrorLine = new XYChart.Series<>();
        cmMeanMaxErrorLine.setName("cmMeanLine");

        LINE_CHART_MAX_ERROR.getData().add(cmOriginalMaxErrorLine);
        LINE_CHART_MAX_ERROR.getData().add(cmConservativeMaxErrorLine);
        LINE_CHART_MAX_ERROR.getData().add(cmMeanMaxErrorLine);

        // Set up error percentage line and add to relevant line chart
        XYChart.Series<Number, Number> cmOriginalErrorPercentageLine = new XYChart.Series<>();
        cmOriginalErrorPercentageLine.setName("cmOriginalErrorPercentageLine");
        XYChart.Series<Number, Number> cmConservativeErrorPercentageLine = new XYChart.Series<>();
        cmConservativeErrorPercentageLine.setName("cmConservativeErrorPercentageLine");
        XYChart.Series<Number, Number> cmMeanErrorPercentageLine = new XYChart.Series<>();
        cmMeanErrorPercentageLine.setName("cmMeanErrorPercentageLine");

        XYChart.Series<Number, Number> errorPercentageAllowed = new XYChart.Series<>();
        errorPercentageAllowed.setName("Error percentage allowed");

        LINE_CHART_ERROR_PERCENTAGE.getData().add(cmOriginalErrorPercentageLine);
        LINE_CHART_ERROR_PERCENTAGE.getData().add(cmConservativeErrorPercentageLine);
        LINE_CHART_ERROR_PERCENTAGE.getData().add(cmMeanErrorPercentageLine);
        LINE_CHART_ERROR_PERCENTAGE.getData().add(errorPercentageAllowed);

        // We also need a frequency counter to store the true count to check the accuracy of our sketches
        BasicFrequencyCounter trueFrequencyCounter = new BasicFrequencyCounter();

        new AnimationTimer() {
            private long currentDistinctCount;
            private long totalCount;
            double maxCMSError;
            double maxCMSCError;
            double maxCMMSError;


            @Override
            public void handle(long current) {

                if (currentDistinctCount > UNIQUE_ITEMS_TO_ADD) {
                    return;
                }



                // Keep track of the absolute errors of each of our sketches
                long cmOriginalAbsoluteErrorSum = 0;
                long cmConservativeAbsoluteErrorSum = 0;
                long cmMeanAbsoluteErrorSum = 0;


                // Also keep track of the max error we have seen
                double cmOriginalMaxError = 0;
                double cmConservativeMaxError = 0;
                double cmMeanMaxError = 0;

                int cmOriginalErrors = 0;
                int cmConservativeErrors = 0;
                int cmMeanErrors = 0;
                double allowableError = epsilonGuarantee * totalCount;

                // Loop through all entries of the hash map (these are all the items we have added to the sketch)
                for (Map.Entry<Long, Long> set: trueFrequencyCounter.items.entrySet()) {
                    // Retrieve the weight of each item
                    long keyWeight = set.getValue();

                    // Find the difference between the true weight and what our sketches estimate. Also keep track of all estimates outside the epsilon guarantee
                    long cmOriginalAbsoluteError = Math.abs(keyWeight - cmOriginal.query(set.getKey()));
                    cmOriginalAbsoluteErrorSum += cmOriginalAbsoluteError;
                    cmOriginalMaxError = Math.max(cmOriginalMaxError, cmOriginalAbsoluteError);
                    if (cmOriginalAbsoluteError > allowableError) {
                        cmOriginalErrors ++;
                    }

                    long cmConservativeAbsoluteError = Math.abs(keyWeight - cmConservative.query(set.getKey()));
                    cmConservativeAbsoluteErrorSum += cmConservativeAbsoluteError;
                    cmConservativeMaxError = Math.max(cmConservativeMaxError, cmConservativeAbsoluteError);
                    if (cmConservativeAbsoluteError > allowableError) {
                        cmConservativeErrors++;
                    }

                    long cmMeanAbsoluteError = Math.abs(keyWeight - cmMean.query(set.getKey()));
                    cmMeanAbsoluteErrorSum += cmMeanAbsoluteError;
                    cmMeanMaxError = Math.max(cmMeanMaxError, cmMeanAbsoluteError);
                    if (cmMeanAbsoluteError > allowableError) {
                        cmMeanErrors++;
                    }

                }


                double cmOriginalAverageError = 0;
                double cmOriginalPercentageError = 0;
                double cmOriginalFailurePercentage = 0;

                double cmConservativeAverageError = 0;
                double cmConservativePercentageError = 0;
                double cmConservativeFailurePercentage = 0;

                double cmMeanAverageError = 0;
                double cmMeanPercentageError = 0;
                double cmMeanFailurePercentage = 0;





                long currentCount = trueFrequencyCounter.items.size();
                if (currentCount > 0) {

                    // Calculate the average absolute and percentage error, as well as the maximum percentage error
                    cmOriginalAverageError = (double) cmOriginalAbsoluteErrorSum / currentCount;
                    cmOriginalPercentageError = cmOriginalAverageError / totalCount * 100;
                    cmOriginalMaxError = cmOriginalMaxError / totalCount * 100;
                    maxCMSError = Math.max(cmOriginalMaxError, maxCMSError);

                    cmConservativeAverageError = (double) cmConservativeAbsoluteErrorSum / currentCount;
                    cmConservativePercentageError = cmConservativeAverageError / totalCount * 100;
                    cmConservativeMaxError = cmConservativeMaxError / totalCount * 100;
                    maxCMSCError = Math.max(cmConservativeMaxError, maxCMSCError);

                    cmMeanAverageError = (double) cmMeanAbsoluteErrorSum / currentCount;
                    cmMeanPercentageError = cmMeanAverageError / totalCount * 100;
                    cmMeanMaxError = cmMeanMaxError / totalCount * 100;
                    maxCMMSError = Math.max(cmMeanMaxError, maxCMMSError);

                    cmOriginalFailurePercentage = (double) cmOriginalErrors / currentCount * 100;
                    cmConservativeFailurePercentage = (double) cmConservativeErrors / currentCount * 100;
                    cmMeanFailurePercentage = (double) cmMeanErrors / currentCount * 100;

                }

                // Add data points to our line charts
                cmOriginalAverageAbsoluteErrorLine.getData().add(new XYChart.Data<>(currentCount, cmOriginalAverageError));
                cmConservativeAverageAbsoluteErrorLine.getData().add(new XYChart.Data<>(currentCount, cmConservativeAverageError));
                cmMeanAverageAbsoluteErrorLine.getData().add(new XYChart.Data<>(currentCount, cmMeanAverageError));

                cmOriginalPercentageErrorLine.getData().add(new XYChart.Data<>(currentCount, cmOriginalPercentageError));
                cmConservativePercentageErrorLine.getData().add(new XYChart.Data<>(currentCount, cmConservativePercentageError));
                cmMeanPercentageErrorLine.getData().add(new XYChart.Data<>(currentCount, cmMeanPercentageError));

                cmOriginalMaxErrorLine.getData().add(new XYChart.Data<>(currentCount, cmOriginalMaxError));
                cmConservativeMaxErrorLine.getData().add(new XYChart.Data<>(currentCount, cmConservativeMaxError));
                cmMeanMaxErrorLine.getData().add(new XYChart.Data<>(currentCount, cmMeanMaxError));

                cmOriginalErrorPercentageLine.getData().add(new XYChart.Data<>(currentCount, cmOriginalFailurePercentage));
                cmConservativeErrorPercentageLine.getData().add(new XYChart.Data<>(currentCount, cmConservativeFailurePercentage));
                cmMeanErrorPercentageLine.getData().add(new XYChart.Data<>(currentCount, cmMeanFailurePercentage));
                errorPercentageAllowed.getData().add(new XYChart.Data<>(currentCount, percentageGuarantee));

                // Update the sketches with new items
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

        Scene scene = new Scene(LINE_CHART_ABSOLUTE_ERROR, 800, 600);
        stage.setScene(scene);

        stage.show();

        Scene scene2 = new Scene(LINE_CHART_PERCENTAGE_TOTAL_WEIGHT, 800, 600);
        secondStage.setScene(scene2);
        secondStage.show();

        Scene scene3 = new Scene(LINE_CHART_MAX_ERROR, 800, 600);
        thirdStage.setScene(scene3);
        thirdStage.show();

        Scene scene4 = new Scene(LINE_CHART_ERROR_PERCENTAGE, 800, 600);
        fourthStage.setScene(scene4);
        fourthStage.show();


    }

}
