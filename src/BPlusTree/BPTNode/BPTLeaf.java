package BPlusTree.BPTNode;


public class BPTLeaf<K extends Comparable> extends BPTNode<K> {

    public BPTLeaf(int m, BPTNonLeaf<K> fatherNode){
        super(m, fatherNode);
    }
}
