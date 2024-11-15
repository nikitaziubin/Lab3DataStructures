package utils;

/**
 * @param <K>
 * @param <V>
 */
public interface ParsableMap<K, V> extends EvaluableMap<K, V> {

    V put(String key, String value);

    void load(String filePath);

    void save(String filePath);

    void println(String delimiter);

    /**
     * Returns a hash table view as string for visualisation purpose. E.g.:
     * [0] -> key-value pair 1 -> key-value pair 3
     * [1] -> key-value pair 4 -> key-value pair 2
     * ...
     *
     * @return Returns a hash table view as a two-dimensional array of strings
     */
    String[][] getMapModel();
}
