package BPlusTree.BPTNode;

import BPlusTree.BPTKey.BPTKey;

import java.util.Collections;
import java.util.List;

/**
 *
 * class covers common functions of external node
 *
 */
public class externalNode<K extends Comparable> {
    // same data is set in the father class
    protected short nodeType;
    protected int length;
    protected long pageIndex;
    protected List<BPTKey<K>> keyList;

    // 好像并不需要指向父节点的指针
//    // used to get father's index, in order to write the pointer properly
//    protected int fatherIndex;
//
//    public void setFatherIndex(int fatherIndex) {
//        this.fatherIndex = fatherIndex;
//    }

    public externalNode(BPTNode<K> node) {
        if(node.isLeaf()) {
            this.nodeType = 0;
        } else {
            this.nodeType = 1;
        }
        keyList = node.getKeyList();
        length = keyList.size();
    }

    /**
     * set the node type
     * @param nodeType the node type short to be set
     */
    public void setNodeType(short nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * set the amount of key in this node
     * @param length the amount of key length to be set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * pageIndex should be set by the tree store function, as storeFile in BPlusTree
     */
    public void setPageIndex(long pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * the specific external node write functions are implemented in the children class
     */
    public void writeNode() {
    }
}
