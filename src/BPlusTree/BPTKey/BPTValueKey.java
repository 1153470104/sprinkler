package BPlusTree.BPTKey;

/**
 *
 * the BPTKey class with value
 *
 * TODO though now I think it's so useless
 *
 */
public class BPTValueKey<K extends Comparable, V> extends BPTKey<K>{

    private V value;

    public BPTValueKey(K key, V value){
        super(key);
        this.value = value;
    }

    /**
     * the override function of father class
     * this copy function would return  a deep copy of BPTKey
     *
     * @return a deep copy of this BPTKey
     */
    @Override
    public BPTKey<K> copyKey() {
        return new BPTValueKey<K, V>(this.key, this.value);
    }

    /**
     * getter function of value
     * @return value of this BPTKey
     */
    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        // very redundant for a toString function
        // just be used when I want to check sth
        return "BPTValueKey{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
