package counting.distinctcounting;

import java.util.TreeSet;

/** A simple implementation of a distinct counter using Java's in-built implementation of a balanced binary search tree.
 * It uses a red-black tree under the hood to guarantee O(log(n)) updates, deletions and queries.
 */

public class BasicDistinctCountingTree {


    private final TreeSet<Long> items_seen = new TreeSet<>();

    public void update(long item) {
        items_seen.add(item);
    }

    public long query() {
        return items_seen.size();
    }

}
