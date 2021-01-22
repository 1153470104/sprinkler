package BPTNode;


public class BPTLeaf<K extends Comparable> extends BPTNodeCommon<K>{

    public BPTLeaf(int m, BPTNonLeaf<K> fatherNode){
        super(m, fatherNode);
    }
}
