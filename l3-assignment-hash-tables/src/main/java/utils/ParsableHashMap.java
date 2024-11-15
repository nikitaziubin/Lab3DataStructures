package utils;

import demo.CarsGenerator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static gui.Table.ARROW;

public class ParsableHashMap<K, V extends Parsable<V>> extends HashMap<K, V> implements ParsableMap<K, V> {

    private final Function<String, K> keyCreateFunction;   // function for creation of a key object
    private final Function<String, V> valueCreateFunction; // function for creation of a value object

    /**
     * Constructor with parameters keyCreateFunction, valueCreateFunction and ht.
     * Default capacity and load factor are initialized.
     *
     * @param keyCreateFunction
     * @param valueCreateFunction
     * @param ht
     */
    public ParsableHashMap(Function<String, K> keyCreateFunction,
                           Function<String, V> valueCreateFunction,
                           HashManager.HashType ht) {

        this(keyCreateFunction, valueCreateFunction, DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, ht);
    }

    /**
     * Constructor with parameters keyCreateFunction, valueCreateFunction, initialCapacity, loadFactor and ht.
     *
     * @param keyCreateFunction
     * @param valueCreateFunction
     * @param initialCapacity
     * @param loadFactor
     * @param ht
     */
    public ParsableHashMap(Function<String, K> keyCreateFunction,
                           Function<String, V> valueCreateFunction,
                           int initialCapacity,
                           float loadFactor,
                           HashManager.HashType ht) {

        super(initialCapacity, loadFactor, ht);
        this.keyCreateFunction = keyCreateFunction;
        this.valueCreateFunction = valueCreateFunction;
    }

    @Override
    public V put(String key, String value) {
        return super.put(
                create(keyCreateFunction, key, "No key-creation function defined"),
                create(valueCreateFunction, value, "No value-creation function defined")
        );
    }

    /**
     * Creates a hash table form the filePath file data.
     *
     * @param filePath
     */
    @Override
    public void load(String filePath) {
        if (filePath == null || filePath.length() == 0) {
            return;
        }
        clear();
        try (BufferedReader fReader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            fReader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .forEach(line -> put(CarsGenerator.generateId(), line));
        } catch (FileNotFoundException e) {
            Ks.ern("Suitable data file not found: " + e.getLocalizedMessage());
        } catch (IOException | UncheckedIOException e) {
            Ks.ern("File reading error: " + e.getLocalizedMessage());
        }
    }

    /**
     * Saves the hash table as a string to the file fName for later use.
     *
     * @param filePath
     */
    @Override
    public void save(String filePath) {
        throw new UnsupportedOperationException("Saving of hash table to the file currently is not supported");
    }

    /**
     * Prints the hash table to the Ks.ouf("")
     *
     * @param delimiter delimiter of the string
     */
    @Override
    public void println(String delimiter) {
        if (super.isEmpty()) {
            Ks.oun("The hash table is empty");
            return;
        }

        Ks.oufln("****** Hash table ******");
        Ks.printMapModel(delimiter, getMapModel());
        Ks.oufln("****** The total number of key-value pairs is " + super.size());
    }

    @Override
    public String[][] getMapModel() {
        String[][] result = new String[table.length][];
        int count = 0;
        for (Node<K, V> n : table) {
            List<String> list = new ArrayList<>();
            list.add("[ " + count + " ]");
            while (n != null) {
                list.add(ARROW);
                list.add(n.toString());
                n = n.next;
            }
            result[count] = list.toArray(new String[0]);
            count++;
        }
        return result;
    }

    private static <T, R> R create(Function<T, R> function, T data, String errorMessage) {
        return Optional.ofNullable(function)
                .map(f -> f.apply(data))
                .orElseThrow(() -> new IllegalStateException(errorMessage));
    }
}
