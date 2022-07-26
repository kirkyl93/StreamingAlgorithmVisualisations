package counting.frequencycounting;

import java.util.Map;
import java.util.Random;

public class main {

    public static void main(String[] args) {

        Random rand = new Random();

        CountMinSketchOriginal cms = new CountMinSketchOriginal(7, 20000);
        CountMinSketchConservative cmsc = new CountMinSketchConservative(7, 20000);
        CountMinMeanSketch cmms = new CountMinMeanSketch(7, 20000);
        BasicFrequencyCounter bfc = new BasicFrequencyCounter();

        long currentSum = 0;
        for (int i = 0; i < 10000000; i++) {
            long randNum = Math.abs(rand.nextInt(1000000));
            currentSum += randNum;

            long randWeight = 1;
            if (rand.nextDouble() < 0.7) {
                randWeight = rand.nextInt(1,71000);
            }

            cms.update(randNum, randWeight);
            bfc.update(randNum, randWeight);
            cmsc.update(randNum, randWeight);
            cmms.update(randNum, randWeight);
            currentSum += randWeight;
        }

        long cmsAbsoluteError = 0;
        long cmscAbsoluteError = 0;
        long cmmsAbsoluteError = 0;

        int distinct_items = 0;
        for (Map.Entry<Long, Long> set: bfc.items.entrySet()) {
            distinct_items++;

            if (set.getValue() > 32 && cmms.query(set.getKey()) == 0) {

                System.out.println("hello");

                cmsc.showMatrix();

                System.out.println(set.getKey());

                System.out.println();

                System.out.println(set.getValue());

                System.out.println();

                System.out.println(cms.query(set.getKey()));

                System.out.println();

                System.out.println(cmsc.query(set.getKey()));

                System.out.println();

                System.out.println(cmms.query(set.getKey()));

                return;
            }

            if (true) {
                continue;
            }

            cmsAbsoluteError += Math.abs(set.getValue() - cms.query(set.getKey()));
            cmscAbsoluteError += Math.abs(set.getValue() - cmsc.query(set.getKey()));
            cmmsAbsoluteError += Math.abs(set.getValue() - cmms.query(set.getKey()));


        }
        System.out.println(currentSum);

        System.out.println(cmsAbsoluteError);
        System.out.println(cmsAbsoluteError / distinct_items);
        System.out.println(cmscAbsoluteError);
        System.out.println(cmscAbsoluteError / distinct_items);
        System.out.println(cmmsAbsoluteError);
        System.out.println(cmmsAbsoluteError / distinct_items);







    }
}
