package counting.frequencycounting;

public class CountMinSketchOriginal extends CountMinSketch {


    public CountMinSketchOriginal(int d, int t) {
        super(d, t);
    }


    public void update(long item, long weight) {
        for (int i = 0; i < d; i++) {
            int hashValue = (int) (hashFunctions[i].hash(item) % t);
            matrix[i][hashValue] += weight;
        }
    }
}

