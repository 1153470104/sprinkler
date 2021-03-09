package BPlusTree.BPTNode;

/**
 *
 * class of nonLeaf node
 *
 */
public class BPTNonLeaf<K extends Comparable> extends BPTNode<K> {

    public BPTNonLeaf(int m, BPTNonLeaf<K> fatherNode) {
        super(m, fatherNode);
        // seems that it's the only useful things this extends class do...
        // TODO BPTNode class structure should be fixed
        this.isLeaf = false;
    }

}
