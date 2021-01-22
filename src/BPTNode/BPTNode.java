package BPTNode;

import BPTKey.BPTKey;

public interface BPTNode<K extends Comparable> {
    public int insertKey( int index, BPTKey<K> key);
    public void addChild(BPTNode<K> child);
    public void insertChild( int index, BPTNode<K> childNode);
    public int checkout();
    public int keyLength();
    public int childLength();
    public BPTNode<K> getFather();
    public void setIsLeaf(boolean bool);
    public int searchKey(BPTKey<K> key);
    public boolean isLeaf();
    public BPTKey<K> getKey(int index);
    public void deleteKey(int index);
    public BPTNode<K> getChild(int index);
    public BPTNode<K> deleteChild(int index);
    public void setFather(BPTNode<K> father);

    public void checkLeafLink();
    public void setLeafPrev(BPTNode<K> prev);
    public void setLeafNext(BPTNode<K> next);
    public BPTNode<K> getLeafPrev();
    public BPTNode<K> getLeafNext();
}
