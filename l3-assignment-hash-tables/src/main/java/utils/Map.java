package utils;

/**
 * The interface describes the hash map ADT.
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface Map<K, V> {

    /**
     * Checks if the hash table is empty.
     *
     * @return true if empty
     */
    boolean isEmpty();

    /**
     * Returns the number of key-value pairs in the hash table.
     *
     * @return the number of key-value pairs in the hash table.
     */
    int size();

    /**
     * Clears the hash table.
     */
    void clear();

    /**
     * Adds a new key-value pair to the hash table.
     *
     * @param key
     * @param value
     * @return added value
     */
    V put(K key, V value);

    /**
     * Returns the value associated with the key.
     *
     * @param key
     * @return the value associated with the key.
     */
    V get(K key);

    /**
     * Removes the pair from the hash table.
     *
     * @param key
     * @return value associated with key or null, if no value is associated with the key.
     */
    V remove(K key);

    /**
     * Checks if the key exists in the hash table.
     *
     * @param key
     * @return true if the key exists in the hash table, else false.
     */
    boolean contains(K key);

    /**
     * Replaces the existing value with the new value and returns true.
     * If the key does not exist in the hash table or its value does not match the old value specified in the method argument,
     * the change is not performed and false is returned.
     *
     * @param key key.
     * @param oldValue old value.
     * @param newValue new value.
     * @return true if the change occurred, else false
     */
    boolean replace(K key, V oldValue, V newValue);

    /**
     * Checks the specified value exists in the hash table
     *
     * @param value value.
     * @return true if one or more values exist, else false
     */
    boolean containsValue(Object value);
}
