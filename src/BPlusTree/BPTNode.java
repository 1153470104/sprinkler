package BPlusTree;

public interface BPTNode {
    public int insertKey( int index, BPTKey<Integer> key);
    public void addChild(BPTNode child);
    public void insertChild( int index, BPTNode childNode);
    public int checkout();
    public int keyLength();
    public int childLength();
    public BPTNode getFather();
    public void setIsLeaf(boolean bool);
    public int searchKey(BPTKey<Integer> key);
    public boolean isLeaf();
    public BPTKey<Integer> getKey(int index);
    public void deleteKey(int index);
    public BPTNode getChild(int index);
    public BPTNode deleteChild(int index);
    public void setFather(BPTNode father);

    public void checkLeafLink();
    public void setLeafPrev(BPTNode prev);
    public void setLeafNext(BPTNode next);
    public BPTNode getLeafPrev();
    public BPTNode getLeafNext();
}
