package counting.approximatecounting;

/** Every counter should be expected to implement these methods.
*/

public interface Counter {

    void update();
    long query();

}
