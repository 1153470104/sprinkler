package BPlusTree;

import javax.swing.*;
import java.util.List;

public interface BPlusTree<K extends Comparable> {
    public void addKey(BPTKey<K> key);
    public List<BPTKey<K>> search(K key1, K key2);
    public int split(BPTNode<K> node);
    public String printBasic();
    public String printData();
}
