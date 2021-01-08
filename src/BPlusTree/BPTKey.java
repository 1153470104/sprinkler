package BPlusTree;

public class BPTKey<K>{

    protected K key;

    public BPTKey(K key) {
        this.key = key;
    }

    public BPTKey<K> getKey() {
        return new BPTKey<K>(this.key);
    }

    @Override
    public String toString() {
        return "Key: " + this.key;
    }
}
