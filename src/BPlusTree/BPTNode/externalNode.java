package BPlusTree.BPTNode;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.configuration.configuration;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
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

    /**
     * init a node from scratch
     * @param nodeType the type short num of the external node
     * @param length the capacity of the external node
     * @param pageIndex the page index of the node
     */
    public externalNode(short nodeType, int length, long pageIndex) {
        this.nodeType = nodeType;
        this.length = length;
        this.pageIndex = pageIndex;
        keyList = new LinkedList<>();
    }

    /**
     * init a node from another in memory node
     * @param node in memory node
     */
    public externalNode(BPTNode<K> node) {
        if(node.isLeaf()) {
            this.nodeType = 1;
        } else {
            this.nodeType = 0;
        }
        keyList = node.getKeyList();
        length = keyList.size();
    }

    /**
     * search the might be key position
     * index:     0   1   2   3
     * keys:      1   3   6   9
     *          /  |   |   |   \
     * index:  0   1   2   3    4
     * if search key is 10, return 4
     *    search key is 1, return 1
     * @param key the key to search
     * @return index of the pointer which contains the key
     */
    public int searchKey(K key){
        int len = keyList.size();
        for(int i = 0; i < len; i++) {
//            System.out.println(keyList.get(i).key());
            if(key.compareTo(keyList.get(i).key()) == -1) {
                return i;
            }
        }
        return len;
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
    public void writeNode(RandomAccessFile r, configuration conf) throws IOException {
    }

    /**
     * add no-value key
     * @param key the key in BPTKey
     */
    public void addKey(K key) {
        this.keyList.add(new BPTKey<K>(key));
    }

    /**
     * get the nodeType
     * @return the node-type short value
     */
    public short getNodeType() {
        return nodeType;
    }

    /**
     * get the key number of this node
     * @return the key-list length, the capacity
     */
    public int getLength() {
        return length;
    }

    /**
     * get the content of node
     * this is a common realization of node
     * @return a string of basic content in node
     */
    public String toString() {
        return nodeType + "," +
                length + "," +
                pageIndex + ",";
    }
}
