package counting.distinctcounting;

import java.util.TreeMap;
import java.util.TreeSet;

public class BasicDistinctCountingTree {


    private final TreeSet<Long> items_seen = new TreeSet<>();

    public void update(long item) {
        items_seen.add(item);
    }

    public long query() {
        return items_seen.size();
    }

}
