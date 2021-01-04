package BPlusTree;

public interface BPTNode {
    public int addKey(BPTKey<String> key);
    public int checkout();
    public int keyLength();
    public int childLength();
    public BPTNode getFather();
}
