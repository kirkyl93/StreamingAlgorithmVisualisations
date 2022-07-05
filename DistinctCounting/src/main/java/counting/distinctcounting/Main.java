package counting.distinctcounting;


public class Main {

    public static void main(String[] args) {

        KMV kmv = new KMV(1000);
        BasicDistinctCounting bdc = new BasicDistinctCounting();

        for (int i = 0; i < 1000; i++) {
            kmv.update(i);
            bdc.update(i);
        }

        for (int i = 500; i < 1002; i++) {
            kmv.update(i);
            bdc.update(i);
        }

        for (int i = 1003; i < 5000; i++) {
            kmv.update(i);
            bdc.update(i);
        }

        for (int i = 10000; i < 1000000; i ++) {
            kmv.update(i);
            bdc.update(i);
        }

        for (int i = 5000; i < 2000000; i++) {
            kmv.update(i);
            bdc.update(i);
        }

        for (int i = 1000; i < 150000; i++) {
            kmv.update(i);
            bdc.update(i);
        }

        System.out.println(bdc.query());
        System.out.println(kmv.query());

    }
}
