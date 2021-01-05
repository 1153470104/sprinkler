package BPlusTree;

public class BPlusTreeCommon implements BPlusTree{
    private boolean onlyRoot;
    private int m;
    private BPTNode root;

    public BPlusTreeCommon(int m){
        this.m = m;
        this.onlyRoot = true;
    }

    @Override
    public void addKey(BPTKey<Integer> key) {
        int checkNum = 0;
        int index = 0;
        BPTNode node = root;
        if(onlyRoot) {
            index = node.searchKey(key);
            checkNum = node.insertKey(index, key);
            if (checkNum == 1) {
                split(node);
                this.root = node.getFather();
            }
        } else {
            while (node.isLeaf()) {
                index = node.searchKey(key);
                node = node.getChild(index);
            }
            index = node.searchKey((key));
            checkNum = node.insertKey(index, key);
            if (checkNum == 1) {
                while(this.split(node) != -1)
                    node = node.getFather();
            }
            this.root = node.getFather();
        }
    }

    @Override
    public void balance(int checkNum, BPTNode node) {
    }

    @Override
    public String search(String key) {
        return null;
    }

    @Override
    public void print() {

    }

    @Override
    public int split(BPTNode node) {
        return 0;

    }

    @Override
    public void Combine(BPTNode childNode1, BPTNode childNode2) {
    }
}
