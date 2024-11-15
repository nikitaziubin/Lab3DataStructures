package utils;

/**
 * Interface describes the getter methods of hash map parameters.
 *
 * @param <K> raktas
 * @param <V> reikšmė
 */
public interface EvaluableMap<K, V> extends Map<K, V> {

    /**
     * Returns the maximum length of the chain.
     *
     * @return Maximum chain length.
     */
    default int getMaxChainSize(){
        return -1;
    }

    /**
     * Returns the number of rehashes of a hash table.
     *
     * @return Number of rehashes.
     */
    int getRehashesCounter();

    /**
     * Returns the capacity of the hash table.
     *
     * @return Hash table capacity.
     */
    int getTableCapacity();

    /**
     * Returns the index of the last added element of the hash table array.
     *
     * @return The index of the last updated element of the hash table array.
     */
    int getLastUpdated();

    /**
     * Returns the number of items populated in the hash table array.
     *
     * @return the number of elements occupied by the hash table array.
     */
    int getNumberOfOccupied();
}
