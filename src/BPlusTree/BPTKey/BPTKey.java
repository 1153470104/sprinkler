package BPlusTree.BPTKey;

import java.util.Objects;

public class BPTKey<K extends Comparable>{

    protected K key;

    public BPTKey(K key) {
        this.key = key;
    }

    public BPTKey<K> copyKey() {
        return new BPTKey<K>(this.key);
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

    public K key() {
        return key;
    }


}
