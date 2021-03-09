package BPlusTree.BPTNode;


/**
 *
 * a class of LeafNode
 *
 * TODO I don't know why I have to had a leafNode with just name...
 *
 */
public class BPTLeaf<K extends Comparable> extends BPTNode<K> {

    public BPTLeaf(int m, BPTNonLeaf<K> fatherNode){
        super(m, fatherNode);
    }
}
