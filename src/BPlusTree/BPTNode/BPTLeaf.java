package BPlusTree.BPTNode;


/**
 *
 * a class of LeafNode
 *
 * TODO the structure need to be optimized
 */
public class BPTLeaf<K extends Comparable> extends BPTNode<K> {

    public BPTLeaf(int m, BPTNonLeaf<K> fatherNode){
        super(m, fatherNode);
    }
}
