package BPlusTree.BPTNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * class of non leaf external node
 *
 */
public class externalNonLeaf<K extends Comparable> extends externalNode<K>{
    private List<Long> pointerList;

    public externalNonLeaf(BPTNode<K> node) {
        super(node);
        pointerList = new ArrayList<>();
    }

     /**
     * write node of non leaf node into the tree file
     */
    @Override
    public void writeNode() {
        super.writeNode();
    }

    /**
     * append a pointer to point list of this external node
     * @param point
     */
    public void addPointer(long point) {
        pointerList.add(point);
    }
}
