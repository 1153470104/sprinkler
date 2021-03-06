package BPlusTree.BPTNode;

import BPlusTree.BPTKey.BPTKey;

import java.util.Collections;
import java.util.List;

public class externalNode<K extends Comparable> {
    // same data is set in the father class
    protected short nodeType;
    protected int elementCount;

    protected long pageIndex;
    protected List<BPTKey<K>> keyList;

    // used to get father's index, in order to write the pointer properly
    protected int fatherIndex;

    public void setFatherIndex(int fatherIndex) {
        this.fatherIndex = fatherIndex;
    }

    public externalNode(BPTNode<K> node) {
        if(node.isLeaf()) {
            this.nodeType = 0;
        } else {
            this.nodeType = 1;
        }
        keyList = node.getKeyList();
        elementCount = keyList.size();
    }

    // pageIndex should be set by the tree store function, as storeFile in BPlusTree
    public void setPageIndex(long pageIndex) {
        this.pageIndex = pageIndex;
    }
}
