package BPlusTree.BPTNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class externalNonLeaf<K extends Comparable> extends externalNode<K>{
    private List<Long> pointerList;

    public externalNonLeaf(BPTNode<K> node) {
        super(node);
        pointerList = new ArrayList<>();
    }

    @Override
    public void writeNode() {
        super.writeNode();
    }

    public void addPointer(long point) {
        pointerList.add(point);
    }
}
