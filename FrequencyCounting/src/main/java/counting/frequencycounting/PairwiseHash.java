package counting.frequencycounting;

import java.util.Random;

/** A class that chooses a hash function from a family of pairwise independent functions. It inherits from a base
 * hash function class
 *
 */

public class PairwiseHash extends Hash {

    @Override
    // The hash function is in the form ax + b
    public long hashFunction(long x) {
        return Math.abs(super.getA() * x + super.getB()) % super.getPrime();
    }
}


