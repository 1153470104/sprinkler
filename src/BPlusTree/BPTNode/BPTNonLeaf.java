package BPlusTree.BPTNode;

public class BPTNonLeaf<K extends Comparable> extends BPTNode<K> {

    public BPTNonLeaf(int m, BPTNonLeaf<K> fatherNode) {
        super(m, fatherNode);
        this.isLeaf = false;
    }

}
