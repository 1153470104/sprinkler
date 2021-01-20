package BPlusTree;

import javax.swing.*;
import java.util.List;

public interface BPlusTree {
    public void addKey(BPTKey<Integer> key);
//    public void balance(int checkNum, BPTNode node);
    public List<BPTKey> search(Integer key1, Integer key2);
    public void Combine(BPTNode childNode1, BPTNode childNode2);
    public int split(BPTNode node);
    public String printBasic();
    public String printData();
}
