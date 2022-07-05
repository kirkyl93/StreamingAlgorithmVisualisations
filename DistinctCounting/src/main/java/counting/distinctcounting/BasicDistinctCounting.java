package counting.distinctcounting;

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


