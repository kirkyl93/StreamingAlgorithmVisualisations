package counting.frequencycounting;

import java.util.Random;

public class main {

    public static void main(String[] args) {

        Random rand = new Random();

        CountMinSketchOriginal cms = new CountMinSketchOriginal(7, 2000);
        CountMinSketchConservative cmsc = new CountMinSketchConservative(7, 2000);
        CountMinMeanSketch cmms = new CountMinMeanSketch(7, 2000);
        BasicFrequencyCounter bfc = new BasicFrequencyCounter();

        long currentSum = 0;
        for (int i = 0; i < 100000; i++) {
            long randNum = Math.abs(rand.nextInt(10000000));
            long randWeight = rand.nextInt(2);
            cms.update(randNum, randWeight);
            bfc.update(randNum, randWeight);
            cmsc.update(randNum, randWeight);
            cmms.update(randNum, randWeight);
            currentSum += randWeight;
        }


        System.out.println();
        System.out.println();

        System.out.println("True Count");
        System.out.println(bfc.query(5));

        System.out.println(cms.query(5));
        System.out.println();
        System.out.println(cmsc.query(5));
        System.out.println();
        System.out.println("clever Count");
        System.out.println(cmms.query(5));

        System.out.println(currentSum);

    }
}
