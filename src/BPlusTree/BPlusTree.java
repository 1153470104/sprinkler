package BPlusTree;

public interface BPlusTree {
    public void addKey(BPTKey<Integer> key);
    public void balance(int checkNum, BPTNode node);
    public String search(String key);
    public void Combine(BPTNode childNode1, BPTNode childNode2);
    public int split(BPTNode node);
    public void print();
}
