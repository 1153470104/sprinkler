package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTNode.BPTNode;

import java.util.List;

public interface BPlusTree<K extends Comparable> {
    public void addKey(BPTKey<K> key);
    public List<BPTKey<K>> search(K key1, K key2);
//    public int split(BPlusTree.BPTNode<K> node);
    public String printBasic();
    public String printData();
    public boolean isBlockFull();
    public boolean isTemplated();
    public BPTNode<K> rootCopy();
    public int getM();
    public void flushOut();
    public void setStartTime(int startTime);
    public void setEndTime(int endTime);
    public void setKeyStart(K start);
    public void setKeyEnd(K end);
//    public String writeInDisk();
}
