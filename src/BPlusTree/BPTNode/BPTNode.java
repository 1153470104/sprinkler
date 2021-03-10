package BPlusTree.BPTNode;

import BPlusTree.BPTKey.BPTKey;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * class that realize all common function of the node types
 *
 */
public class BPTNode<K extends Comparable> {
    protected int m;
    protected int maxNumber;
    protected int minNumber;
    protected int keyLength;
    protected int childLength;
    protected List<BPTKey<K>> keyList;
    protected List<BPTNode<K>> childrenList;
    protected BPTNode<K> fatherNode;
    protected boolean isLeaf;

    // TODO reorganize the frame of BPTNode
    private BPTNode<K> leafPrev = null; // those elements should be realized in leaf node implement
    private BPTNode<K> leafNext = null; // but due to some convenience I put them here

    public BPTNode(int m, BPTNode<K> fatherNode){
        this.m = m;
        this.maxNumber = m-1;
        this.minNumber = (int) (Math.ceil(m / 2.0) -1);
        this.keyLength = 0;
        this.childLength = 0;
        this.keyList = new ArrayList<>();
        this.childrenList = new ArrayList<>();
        this.fatherNode = fatherNode;
        this.isLeaf = true;
    }

    /**
     * insert key into keyList according to index
     *
     * @param index the index of position in keyList
     * @param key the key to be inserted
     * @return the checkout() result of this node
     *         the information about overflow or not
     */
    public int insertKey(int index, BPTKey<K> key) {
        this.keyList.add(index, key);
        this.keyLength += 1;
        return this.checkout();
    }

    /**
     * append child node pointer to childList
     *
     * @param child the child node to be added
     */
    public void addChild(BPTNode<K> child) {
        this.childrenList.add(child);
        this.childLength += 1;
    }

    /**
     * insert childNode into childList according to index
     *
     * @param index the index of position in childList
     * @param childNode the childNode to be inserted
     */
    public void insertChild( int index, BPTNode<K> childNode){
        this.childrenList.add(index, childNode);
        this.childLength += 1;
    }

    /**
     * to check if the node is overflowed or too empty
     *     -1 means too empty
     *     1 means overflowed
     *     0 means perfect
     *
     * @return the check number which reflects state of the node
     */
    public int checkout() {
        if (keyLength < this.minNumber){
            return -1;
        } else if(keyLength > maxNumber) {
            return 1;
        }
        return 0;
    }

    /**
     * similar to the clone function, but not a whole copy
     *
     * this function copy the keyList, assign a fatherNode
     * childList is left empty
     * this.fatherNode should not be use!
     * because the father node should be a new built one
     *
     * @param father a new father param  used to be assigned
     * @return the frame copy of a node
     *
     */
    public BPTNode<K> valueCopy(BPTNode<K> father) {
        BPTNode<K> node = new BPTNode<K>(this.m, father);
        node.keyList = new ArrayList<>();
        node.childrenList = new ArrayList<>();
        node.keyList.addAll(this.keyList);
        node.keyLength = this.keyLength;
        node.childLength = 0;
        node.isLeaf = false;
        return node;
    }

    /**
     * maybe use to update nonLeaf node in some dilemma
     * but it's unused til now
     */
    public void checkLeafLink() {
        if(!isLeaf){
            BPTNode<K> LeafPrev = null;
            BPTNode<K> Leafnext = null;
        }
    }

    /**
     * search for the probable position of key in keyList
     *
     * @param key the key use to search its position
     * @return a int index of probable position of the key
     */
    public int searchKey(BPTKey<K> key) {
        if (this.keyLength == 0) {
            return 0;
        }
        for(int i = 0; i < this.keyLength; i++){
            K listKey = this.keyList.get(i).key();
            K inputKey = key.key();
            if (inputKey.compareTo(listKey) == -1) {
                return i;
            } else if (inputKey == listKey){
                // if the searching-key happen to be the same of a key in keyList
                // return the position after the corresponding key
                // for B+ tree node's partition key its self should be in the latter part
                return i+1;
            }
        }
        return this.keyLength;
    }

    /**
     * get the key on the particular index
     * @param index the index to get the key
     * @return the key at index
     */
    public BPTKey<K> getKey(int index) {
        return this.keyList.get(index);
    }

    /**
     * remove key according to index
     * @param index the index of the key you want to remove
     */
    public void deleteKey(int index) {
        this.keyList.remove(index);
        this.keyLength -= 1;
    }

    /**
     * a child getter according to the index
     * @param index the index to get the childNode
     * @return the childNode on the index position
     */
    public BPTNode<K> getChild(int index) {
        if (index > this.childLength-1) {
            return null;
        }
        return this.childrenList.get(index);
    }

    /**
     * delete child according to a index
     * @param index the index of the child you want to delete
     * @return the childNode just being delete from the childList
     */
    public BPTNode<K> deleteChild(int index) {
        BPTNode<K> node = null;
        if (index < this.childLength) {
            node = this.childrenList.remove(index);
            this.childLength -= 1;
        }
        return node;
    }

    /**
     * should not be abusively used
     * @return the raw keyList
     */
    public List<BPTKey<K>> getKeyList() {
        return keyList;
    }

    /**
     * should not be abusively used
     * @return the raw childrenList
     */
    public List<BPTNode<K>> getChildrenList() {
        return childrenList;
    }

    /**
     * setter of next node of this leaf
     * @param next the next node of this node
     */
    public void setLeafNext(BPTNode<K> next) {
        if(isLeaf) {
            leafNext = next;
        }
    }

    /**
     * setter of prev node of this leaf
     * @param prev the prev node of this node
     */
    public void setLeafPrev(BPTNode<K> prev) {
        if(isLeaf) {
            leafPrev = prev;
        }
    }

    /**
     * father node setter of this node
     * @param father the father node
     */
    public void setFather(BPTNode<K> father) {
        this.fatherNode = father;
    }

    /**
     * get the father of this node
     * @return current father node
     */
    public BPTNode<K> getFather() {
        return this.fatherNode;
    }

    /**
     * get the amount of keys
     * @return the amount of keys
     */
    public int keyLength() {
        return this.keyLength;
    }

    /**
     *  get the amount of children
     * @return the amount of children
     */
    public int childLength() {
        return this.childLength;
    }

    /**
     * get the previous leaf of this node
     * never used, weird :(
     *
     * @return the previous leaf
     */
    public BPTNode<K> getLeafPrev() {
        return leafPrev;
    }

    /**
     * get the next leaf of this node
     *
     * without add nullPointer detection
     * the param itself could be null
     * so outer function should be write properly
     *
     * @return the next leaf
     */
    public BPTNode<K> getLeafNext() {
        return leafNext;
    }

    /**
     * setter of isLeaf param
     * @param bool the new value of isLeaf
     */
    public void setIsLeaf(boolean bool) {
        this.isLeaf = bool;
    }

    /**
     * getter of isLeaf status
     * @return current isLeaf param
     */
    public boolean isLeaf() {
        return this.isLeaf;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node Key: ");
        for (BPTKey<K> key: this.keyList) {
            sb.append(key.key().toString()).append(" ");
        }
        return sb.toString();
    }
}
