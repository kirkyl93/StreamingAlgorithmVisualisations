public class BasicCounter {

    private int count = 0;

    public void update() {
        count += 1;
    }

    public int getCount() {
        return count;
    }

    public void merge(BasicCounter bc) {
        count += bc.getCount();
    }
}
