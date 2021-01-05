package BPlusTree;

public interface BPTNode {
    public int insertKey( int index, BPTKey<Integer> key);
    public int checkout();
    public int keyLength();
    public int childLength();
    public BPTNode getFather();
    public int searchKey(BPTKey<Integer> key);
    public boolean isLeaf();
    public BPTNode getChild(int index);
}
