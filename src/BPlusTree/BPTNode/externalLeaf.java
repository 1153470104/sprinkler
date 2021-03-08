package BPlusTree.BPTNode;

import BPlusTree.BPTKey.BPTValueKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class externalLeaf<K extends Comparable> extends externalNode<K>{
    private List<Object> valueList;

    private long prevLeaf;
    private long nextLeaf;

    public externalLeaf(BPTNode<K> node) {
        super(node);
        valueList = new ArrayList<>();
        for(int i = 0; i < keyList.size(); i++) {
            ((BPTValueKey<K, Object>)keyList.get(i).key()).getValue();
        }
    }

    @Override
    public void writeNode() {
        super.writeNode();
    }

    // prev & next node's setting relys on the outer function
    public void setPrevLeaf(long prevLeaf) {
        this.prevLeaf = prevLeaf;
    }

    public void setNextLeaf(long nextLeaf) {
        this.nextLeaf = nextLeaf;
    }

}
