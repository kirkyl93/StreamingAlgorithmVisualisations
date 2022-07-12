package counting.distinctcounting;

// A simple implementation of distinct counting. We add every new item to the hashset. The hashset will automatically
// deal with duplicate items. We can simply return the length of the hashset when we query the count.

import java.util.HashSet;

public class BasicDistinctCounting {

    private final HashSet<Long> items_seen = new HashSet<>();

    public void update(long item) {
        items_seen.add(item);
    }

    public long query() {
        return items_seen.size();
    }

}


