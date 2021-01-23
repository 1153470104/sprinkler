package BPlusTree.BPTKey;

public class BPTValueKey<K extends Comparable, V> extends BPTKey<K>{
    private V value;

    public BPTValueKey(K key, V value){
        super(key);
        this.value = value;
    }

    public BPTKey<K> getValueKey(){
        return new BPTValueKey<K, V>(this.key, this.value);
    }
}
