package BPlusTree.BPTNode;

/**
 *
 * class of nonLeaf node
 *
 * TODO BPTNode class structure should be fixed
 */
public class BPTNonLeaf<K extends Comparable> extends BPTNode<K> {

    public BPTNonLeaf(int m, BPTNonLeaf<K> fatherNode) {
        super(m, fatherNode);
        // seems that it's the only useful things this extends class do...
        this.isLeaf = false;
    }

}
