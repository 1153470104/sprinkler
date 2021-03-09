package BPlusTree.BPTKey;

import java.util.Objects;

/**
 *
 * class for key's storage
 *
 */
public class BPTKey<K extends Comparable>{

    protected K key;

    public BPTKey(K key) {
        this.key = key;
    }

    /**
     * a copy of BPTKey
     * only copy key & ignore its value, whether if it has one
     *
     * @return the copy of BPTKey with key
     */
    public BPTKey<K> copyKey() {
        return new BPTKey<K>(this.key);
    }

    /**
     * it's a getter function
     * use key() rather getKey() is to distinguish anther getter function in BPTNode
     * @return key
     */
    public K key() {
        return key;
    }

    public String toString() {
        return "Key: " + this.key;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BPTKey)) return false;
        BPTKey<?> bptKey = (BPTKey<?>) o;
        return Objects.equals(key, bptKey.key);
    }

    public int hashCode() {
        return Objects.hash(key);
    }
}
