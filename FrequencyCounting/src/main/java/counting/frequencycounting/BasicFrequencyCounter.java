package counting.frequencycounting;

import java.util.HashMap;

public class BasicFrequencyCounter {

    private final HashMap<Long, Long> items = new HashMap<>();

    public void update(long item, long weight) {

        if (items.containsKey(item)) {
            items.put(item, items.get(item) + weight);
        }
        else {
            items.put(item, weight);
        }
    }

    public long query(long item) {
        if (items.containsKey(item)) {
            return items.get(item);
        }

        return 0;
    }

}


