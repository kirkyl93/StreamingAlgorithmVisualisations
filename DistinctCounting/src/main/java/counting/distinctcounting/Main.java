package counting.distinctcounting;


public class Main {

    public static void main(String[] args) {

        KMV kmv = new KMV(10000);
        BasicDistinctCounting bdc = new BasicDistinctCounting();

        FourwiseHash fwh = new FourwiseHash();

        System.out.println(fwh.hash(5));
        System.out.println(fwh.hash(5));
        System.out.println(fwh.hash(6));
        System.out.println(fwh.hash(7));
        System.out.println(fwh.hash(20));
        System.out.println(fwh.hash(44));
        System.out.println(fwh.hash(52));
    }
}
