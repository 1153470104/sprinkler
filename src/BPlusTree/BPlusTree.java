package BPlusTree;

import javax.swing.*;
import java.util.List;

public interface BPlusTree {
    public void addKey(BPTKey<Integer> key);
//    public void balance(int checkNum, BPTNode node);
    public List<BPTKey> search(BPTKey<Integer> key1, BPTKey<Integer> key2);
    public void Combine(BPTNode childNode1, BPTNode childNode2);
    public int split(BPTNode node);
    public String printbasic();
    public String printData();
}
