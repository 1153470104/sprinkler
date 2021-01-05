package BPlusTree;

public interface BPTNode {
    public int insertKey( int index, BPTKey<Integer> key);
    public int checkout();
    public int keyLength();
    public int childLength();
    public BPTNonLeaf getFather();
    public int searchKey(BPTKey<Integer> key);
    public boolean isLeaf();
    public BPTKey<Integer> getKey(int index);
    public void deleteKey(int index);
    public BPTNode getChild(int index);
    public void deleteChild(int index);
}
