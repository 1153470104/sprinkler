package BPlusTree.BPTKey;

public class BPTValueKey<K extends Comparable, V> extends BPTKey<K>{
    public V getValue() {
        return value;
    }

    private V value;

    public BPTValueKey(K key, V value){
        super(key);
        this.value = value;
    }

    public BPTKey<K> getValueKey(){
        return new BPTValueKey<K, V>(this.key, this.value);
    }

    @Override
    public BPTKey<K> copyKey() {
//        super.copyKey();
        return new BPTValueKey<K, V>(this.key, this.value);
    }

    @Override
    public String toString() {
        return "BPTValueKey{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
