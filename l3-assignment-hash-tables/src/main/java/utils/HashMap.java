package utils;

import java.util.Arrays;

/**
 * The implementation of hash table based on chaining.
 * If the key is an object, e.g. class Car object,
 * do not forget to override methods equals(Object o) and hashCode().
 *
 * @param <K> key type of hash table
 * @param <V> value type of hash table
 * @author darius.matulis@ktu.lt
 * @task review and comprehend all the methods presented.
 */
public class HashMap<K, V> implements EvaluableMap<K, V> {

    public static final int DEFAULT_INITIAL_CAPACITY = 8;
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;
    public static final HashManager.HashType DEFAULT_HASH_TYPE = HashManager.HashType.DIVISION;

    // hash table
    protected Node<K, V>[] table;
    // the amount of key-value pairs within the hash table
    protected int size = 0;
    // load factor
    protected float loadFactor;
    // hash function
    protected HashManager.HashType ht;
    //--------------------------------------------------------------------------
    //  Parameters of a hash table
    //--------------------------------------------------------------------------
    // Maximum chain length of the hash table
    protected int maxChainSize = 0;
    // the amount of rehashes
    protected int rehashesCounter = 0;
    // Chain index of the last placed key-value pair in the hash table
    protected int lastUpdatedChain = 0;
    // The amount of chains
    protected int chainsCounter = 0;

    // 4 overloaded constructors are created in the class
    public HashMap() {
        this(DEFAULT_HASH_TYPE);
    }

    public HashMap(HashManager.HashType ht) {
        this(DEFAULT_INITIAL_CAPACITY, ht);
    }

    public HashMap(int initialCapacity, HashManager.HashType ht) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, ht);
    }

    public HashMap(float loadFactor, HashManager.HashType ht) {
        this(DEFAULT_INITIAL_CAPACITY, loadFactor, ht);
    }

    public HashMap(int initialCapacity, float loadFactor, HashManager.HashType ht) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }

        if ((loadFactor <= 0.0) || (loadFactor > 1.0)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        this.table = new Node[initialCapacity];
        this.loadFactor = loadFactor;
        this.ht = ht;
    }

    /**
     * Checks if the table hash is empty.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of key-value pairs in the hash table.
     *
     * @return the number of key-value pairs.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Clears hash table.
     */
    @Override
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
        lastUpdatedChain = 0;
        maxChainSize = 0;
        rehashesCounter = 0;
        chainsCounter = 0;
    }

    /**
     * Checks if the key exists in the hash table.
     *
     * @param key
     * @return true if the key exists in the hash table, else false.
     */
    @Override
    public boolean contains(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null in contains(K key)");
        }

        return get(key) != null;
    }

    /**
     * Adds the new key-value pair to the hash table.
     *
     * @param key
     * @param value
     * @return added value.
     */
    @Override
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is null in put(K key, V value)");
        }
        int index = HashManager.hash(key.hashCode(), table.length, ht);
        if (table[index] == null) {
            chainsCounter++;
        }

        Node<K, V> node = getInChain(key, table[index]);
        if (node == null) {
            table[index] = new Node<>(key, value, table[index]);
            size++;

            if (size > table.length * loadFactor) {
                rehash();
            } else {
                lastUpdatedChain = index;
            }
        } else {
            node.value = value;
            lastUpdatedChain = index;
        }

        return value;
    }

    /**
     * Returns the value associated with the key.
     *
     * @param key
     * @return the value associated with the key.
     */
    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null in get(K key)");
        }

        int index = HashManager.hash(key.hashCode(), table.length, ht);
        Node<K, V> node = getInChain(key, table[index]);
        return node == null ? null : node.value;
    }

    /**
     * Removes the pair from the hash table.
     *
     * @param key
     * @return value associated with key or null, if no value is associated with the key.
     */
    @Override
    public V remove(K key) {
        if (key == null) {
            return null;
        } else {
            int index = HashManager.hash(key.hashCode(), this.table.length, this.ht);
            Node startNode;
            if (this.table[index].key == key) {
                startNode = this.table[index];
                this.table[index] = this.table[index].next;
                --this.size;
                return startNode.value;
            } else {
                startNode = this.table[index];

                for(Node<K, V> prevNode = startNode; startNode != null; startNode = startNode.next) {
                    if (startNode.key == key) {
                        prevNode.next = startNode.next;
                        --this.size;
                        break;
                    }

                    prevNode = startNode;
                }

                return startNode.value;
            }
        }
    }

    /**
     * Reshuffling
     */
    private void rehash() {
        HashMap<K, V> newMap = new HashMap<>(table.length * 2, loadFactor, ht);
        for (int i = 0; i < table.length; i++) {
            while (table[i] != null) {
                newMap.put(table[i].key, table[i].value);
                table[i] = table[i].next;
            }
        }
        table = newMap.table;
        maxChainSize = newMap.maxChainSize;
        chainsCounter = newMap.chainsCounter;
        lastUpdatedChain = newMap.lastUpdatedChain;
        rehashesCounter++;
    }

    /**
     * Searching on a single chain
     *
     * @param key
     * @param node
     * @return key-value pair
     */
    private Node<K, V> getInChain(K key, Node<K, V> node) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null in getInChain(K key, Node node)");
        }
        int chainSize = 0;
        for (Node<K, V> n = node; n != null; n = n.next) {
            chainSize++;
            if ((n.key).equals(key)) {
                return n;
            }
        }
        maxChainSize = Math.max(maxChainSize, chainSize + 1);
        return null;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Node<K, V> node : table) {
            if (node != null) {
                for (Node<K, V> n = node; n != null; n = n.next) {
                    result.append(n).append(System.lineSeparator());
                }
            }
        }
        return result.toString();
    }

    public boolean replace(K key, V oldValue, V newValue) {
        if (key == null) {
            return false;
        } else {
            int index = HashManager.hash(key.hashCode(), this.table.length, this.ht);
            if (this.table[index].key == key) {
                this.table[index].value = newValue;
                return true;
            } else {
                for(Node<K, V> startNode = this.table[index]; startNode != null; startNode = startNode.next) {
                    if (startNode.value == oldValue) {
                        startNode.value = newValue;
                        return true;
                    }
                }

                return false;
            }
        }
    }

    public boolean containsValue(Object value) {
        int index = HashManager.hash(value.hashCode(), this.table.length, this.ht);
        return this.table[index] != null;
    }

    /**
     * Returns the maximum length of the chain.
     *
     * @return Maximum chain length.
     */
    @Override
    public int getMaxChainSize() {
        return maxChainSize;
    }

    /**
     * Returns the number of rehashes occurred in the hash table.
     *
     * @return Number of reshuffles.
     */
    @Override
    public int getRehashesCounter() {
        return rehashesCounter;
    }

    /**
     * Returns the capacity of the hash table.
     *
     * @return Hash table capacity.
     */
    @Override
    public int getTableCapacity() {
        return table.length;
    }

    /**
     * Returns the index of the last updated chain.
     *
     * @return the index of the last updated chain.
     */
    @Override
    public int getLastUpdated() {
        return lastUpdatedChain;
    }

    /**
     * Returns the number of chains.
     *
     * @return the number of chains.
     */
    @Override
    public int getNumberOfOccupied() {
        return chainsCounter;
    }

    protected static class Node<K, V> {

        // Key
        protected K key;
        // Value
        protected V value;
        // Pointer to the next node in the chain
        protected Node<K, V> next;

        protected Node() {
        }

        protected Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
}
