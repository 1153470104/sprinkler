package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTNode.*;
import BPlusTree.configuration.externalConfiguration;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * implementation of common functions in memory B+ tree
 * key is unique, value is unique, one key has a corresponding value
 * the tree is used to store the data of input stream with index of keys
 *
 * internal node:
 *  k_{-1}=-inf     k0          k1         k2        k_{n}=+inf
 *            [p0)      [p1)         [p2)       [p3)
 *              k_{i-1} <= pi < k_{i}, 0 <= i <= n
 *
 *  leaf node:
 *  k_{-1}=-inf     k0          k1         k2        k_{n}=+inf
 *                  v0          v1         v2
 *                  vi, 0 <= i < n
 *
 *  node split:
 *          [ k1 k2 k3 k4 ]
 *
 *  This split would result in the following:
 *
 *              [ k3 ]
 *              /   \
 *            /      \
 *          /         \
 *     [ k1 k2 ]   [ k3 k4 ]
 *
 * @param <K> key's type
 *       TODO the problem is we can't know the type of value on tree's level...
 *
 */
public abstract class BPlusTree<K extends Comparable, V>{
    protected boolean onlyRoot; // judge if there's only one root node
    protected int m; // capacity of node
    protected int maxNumber;
    protected int minNumber;
    protected BPTNode<K> root; //root pointer
    protected boolean templateBased = false; // to show if it's template tree
    protected int entryNum = 0; // the total entry number


    //由于这四个元素和对象声明没用绑在一块，所以使用的时候一定要注意，别忘了
    protected int timeStart; //the start time inserting time of the tree
    protected int timeEnd; // the time of stopping using the tree

    //key start仅仅用于分配，没有在addKey的时候对其进行检验，所以一定要注意
    protected K keyStart = null; // the smallest key that could be in the tree
    protected K keyEnd = null; // the biggest key that could be in the tree

    /**
     * basic init of BPlusTree
     * @param m the capacity of every node
     */
    public BPlusTree(int m){
        this.m = m;
        this.maxNumber = m-1;
        this.minNumber = (int) (Math.ceil(m / 2.0) -1);
    }

    /**
     * the basic implementation of addKey
     * increase the total number of key by one
     * @param key the key to be inserted
     */
    public void addKey(BPTKey<K> key) {
        entryNum = entryNum+1;
    }

    /**
     * Abstract method that all classes must implement that
     * search keys in a domain which two boundaries is key1 & key2
     * return a list of all keys in that domain
     * @param key1 the start key of the searching domain
     * @param key2 the end key of the searching domain
     * @return a list of all keys between key1 & key2
     */
    public abstract List<BPTKey<K>> search(K key1, K key2);

    public abstract List<BPTKey<K>> search(int timeStart, int timeEnd, K key1, K key2);

    /**
     * getter of start time
     * @return the start time of the tree
     */
    public int getTimeStart() {
        return timeStart;
    }

    /**
     * getter of end time
     * @return the end time of the tree
     */
    public int getTimeEnd() {
        return timeEnd;
    }

    /**
     * getter of start time
     * @return the start time of the tree
     */
    public K getKeyStart() {
        return keyStart;
    }

    /**
     * getter of end time
     * @return the end time of the tree
     */
    public K getKeyEnd() {
        return keyEnd;
    }

    /**
     * 本函数用于打印基本信息，以及树结构
     */
    public void printInfo() {
        System.out.println("time domain: "+String.valueOf(timeStart) + " to "+ String.valueOf(timeEnd));
//        System.out.println("key domain: "+keyStart.toString() + " to "+ keyEnd.toString());
        System.out.print("tree's m: " + String.valueOf(m) + "; entry's num: " + String.valueOf(entryNum));
        if(this.isTemplate())  {
            System.out.println(" is templated");
        } else {
            System.out.println();
        }
        printBasic();
    }

    /**
     * 本函数用于返回一个包含整个的树结构图示的字符串，节点之间以'|'隔开
     * 每个 '|' 的间隔中是一个node所含的key内容，key之间只有空格来分割，如下所示
     * | 10 |
     * | 4 7 | 30 35 |
     * | 1 2 | 4 6 | 7 9 | 10 21 22 | 30 31 | 35 36 45 ||
     */
    public String printBasic() {
        Queue<BPTNode<K>> nodeQueue = new LinkedList<>();
        nodeQueue.add(root);
        StringBuilder sb = new StringBuilder();
        int levelNum = 1;  // how many keys in this level
        int levelCount = 0;  // how many keys in next level so far we know
        int currentOut = 0;  // how many keys are been printed in this level
        while (!nodeQueue.isEmpty()) {
            BPTNode<K> node = nodeQueue.remove();
            int keyNum = node.keyLength();
//            System.out.println("node key length: " + Integer.toString(keyNum) + " child length" + Integer.toString(node.childLength()));
            sb.append("| ");
            System.out.print("| ");
            for (int i = 0; i < keyNum; i++) {
                sb.append(node.getKey(i).key().toString()).append(" ");
                System.out.print(node.getKey(i).key().toString() + " ");
            }
            int childNum = node.childLength();
            levelCount += childNum;
            for(int i = 0; i < childNum; i++) {
                BPTNode<K> child = node.getChild(i);
                nodeQueue.add(child);
            }
            currentOut += 1;
            if (currentOut == levelNum && levelCount > 0) {
                sb.append("|\n");
                System.out.print("|\n");
                levelNum = levelCount;
                levelCount = 0;
                currentOut = 0;
            }
        }
        sb.append("|");
        System.out.print("|");
        System.out.println();
        return sb.toString();
    }

    /**
     * 本函数用于返回一个包含所有 存储条目key 的一个String
     * 每个 '|' 的间隔中是一个条目的key内容，如下所示
     * | 1 | 2 | 4 | 6 | 7 | 9 | 10 | 21 | 22 | 30 | 31 | 35 | 36 | 45 |
     */
    public String printData() {
        StringBuilder sb = new StringBuilder();
        BPTNode<K> node = root;
        while(node.childLength() > 0) {
            node = node.getChild(0);
//            System.out.println(node.childLength());
//            System.out.println("here");
        }
        do {
            for(int i = 0; i < node.keyLength(); i++){
                sb.append("| ").append(node.getKey(i).key().toString()).append(" ");
//                System.out.print(node.getKey(i).key.toString());
            }
            node = node.getLeafNext();
        } while(node != null);
        if (sb.length() == 0) {
            sb.append("| |");
        } else {
            sb.append("|");
        }
//        System.out.print("|");
//        System.out.print(sb.toString());
        System.out.println();
        return sb.toString();
    }

    /**
     * test if the block is full
     * by detect if the entry num reaches some number, which is 5000 now
     * TODO current implementation is very rude, don't take storage space into account
     * @return a boolean value of if the block is full
     */
    public boolean isBlockFull() {
        if (this.entryNum < 1000) {
            return false;
        }
        return true;
    }

    /**
     * to show if the tree is template based tree
     * @return current templateBased value
     */
    public boolean isTemplate() {
        return this.templateBased;
    }

    /**
     * a frame copy of this tree
     * remove all leaf node's key-value pair, only return a frame of the tree
     * @return a tree without value in leaf node
     */
    public BPTNode<K> rootCopy() {
        BPTNode<K> newRoot;
        BPTNode<K> tempNewRoot;
        BPTNode<K> tempRoot;

        Queue<BPTNode<K>> nodeQueue = new LinkedList<>();
        Queue<BPTNode<K>> copyQueue = new LinkedList<>();
        tempRoot = root;
        newRoot = tempRoot.valueCopy(null);
        tempNewRoot = newRoot;

        try{
            while(true) {
                for(int i = 0; i < tempRoot.childLength(); i++) {
                    BPTNode<K> theChild = tempRoot.getChild(i);
                    if (theChild.isLeaf()) {
                        tempNewRoot.addChild(new BPTNode<>(m, tempNewRoot));
                    } else {
                        nodeQueue.add(theChild);
                        BPTNode<K> copyChild = theChild.valueCopy(tempNewRoot);
                        tempNewRoot.addChild(copyChild);
                        copyQueue.add(copyChild);
                    }
                }
                tempNewRoot = copyQueue.remove();
                tempRoot = nodeQueue.remove();
            }
        } catch(NoSuchElementException e) {
            System.out.println("copy finished!");
        }

        // 维护底层的 prev next 结构
        Queue<BPTNode<K>> tempQueue = new LinkedList<>();
        tempQueue.add(newRoot);
        while(tempQueue.size() > 0){
            tempNewRoot = tempQueue.peek();
            if(tempNewRoot.isLeaf()){
                break;
            }else{
                for(int i = 0; i < tempNewRoot.childLength(); i++){
                    tempQueue.add(tempNewRoot.getChild(i));
                }
                tempQueue.remove();
            }
        }
        BPTNode<K> prevNode;
        BPTNode<K> nextNode;
        prevNode = tempQueue.remove();
        while(tempQueue.size() > 0){
            nextNode = tempQueue.remove();
            prevNode.setLeafNext(nextNode);
            nextNode.setLeafPrev(prevNode);
            prevNode = nextNode;
        }

        return newRoot;
    }

    /**
     * getter of capacity m
     * @return current m
     */
    public int getM() {
        return m;
    }

    /**
     * set the start time of the tree
     * @param start the start time to be set
     */
    public void setStartTime(int start) {
        this.timeStart = start;
    }

    /**
     * set the end time of the tree
     * @param end the end time to be set
     */
    public void setEndTime(int end) {
        this.timeEnd = end;
    }

    /**
     * set the key bound of the tree
     * the rule is: if the boundary is bigger, set it
     *              if the boundary is smaller, ignore it
     * origin:    [  ]    [    ]     []
     * input:   [  ]       [ ]      [   ]
     * result:  [    ]    [    ]    [   ]
     * always return the biggest bound which cover prev & input bound
     *
     * @param start the new start bound
     * @param end the new end bound
     */
    public void setKeyBound(K start, K end) {
        if(keyStart == null && keyEnd == null) {
            keyStart = start;
            keyEnd = end;
        }
        if(keyStart != null && keyStart.compareTo(start) == 1) {
            keyStart = start;
        }
        if(keyEnd != null && keyEnd.compareTo(end) == 1) {
            keyEnd = end;
        }
    }
    /**
     * set the start key of the tree
     * @param start start key of the tree
     */
    public void setKeyStart(K start) {
        this.keyStart = start;
    }

    /**
     * set the end of the tree
     * @param end end key of the tree
     */
    public void setKeyEnd(K end) {
        this.keyEnd = end;
    }

    /**
     * store the tree into an external file
     * @param filePath the file path of the external file
     * @param conf the configuration defines pageSize
     * @return a RandomAccessFile that store the tree's data
     * @throws IOException be thrown when an I/O operation fails
     */
    public RandomAccessFile storeFile(String filePath, externalConfiguration conf)
            throws IOException {

        RandomAccessFile rf = new RandomAccessFile(filePath, "rw");

        // iterate to calculate the number of node
        BPTNode<K> temp = root;
        long pageNum = 2; //contain header page & root page
        Deque<BPTNode<K>> deque = new LinkedList<>();
        deque.add(temp);
        while(!temp.isLeaf()) {
            for(int i = 0; i < temp.childLength(); i++) {
                deque.add(temp.getChild(i));
                pageNum++;
            }
            deque.removeFirst();
            temp = deque.getFirst();
        }
        rf.setLength(pageNum * conf.pageSize);
        writeFileHeader(rf, conf); // write the header of external tree

        //iterate to write every node into treefile rf
        //这里deque temp 复用了上面的，可能会有问题可能没用问题，以后如果出问题了要往这儿想
        deque = new LinkedList<>(); // use to store node wait to be transform into external node
        Deque<Long> fatherDeque = new LinkedList<>(); // use to store the father node of every
        long current = 1; //calculate the temp node's index
        long pageCount = 1; // calculate the child node's page index
        temp = root;
        deque.add(temp);
        boolean reachLeaf = false;
//        fatherDeque.add((long)-1);

        while(deque.size()>0) {
//            System.out.println("num: "+deque.size());
            temp = deque.getFirst();
            //if temp is non leaf node, add it's children to deque & write temp on disk
            if(!temp.isLeaf()) {
                externalNode<K> node = new externalNonLeaf<K>(temp);
                node.setPageIndex(current * conf.pageSize);
                for(int i = 0; i < temp.childLength(); i++) {
                    deque.add(temp.getChild(i));
                    pageCount++; // plus plus before & add pointer after
                    ((externalNonLeaf)node).addPointer(pageCount * conf.pageSize);
                }
                node.writeNode(rf, conf);
                deque.pop();
                // seems that external tree don't need father node
//                node.setFatherIndex(fatherDeque.pop());
                // if temp is leaf, just write temp on disk
            } else {
                externalNode<K> node = new externalLeaf<K>(temp);
                node.setPageIndex(current * conf.pageSize);
                //set the prev leaf & avoid prevLeaf's overflow
                if(reachLeaf) {
                    ((externalLeaf)node).setPrevLeaf((current-1) * conf.pageSize);
                } else {
                    ((externalLeaf)node).setPrevLeaf(-1);
                    reachLeaf = true;
                }
                // set next leaf & avoid nextLeaf's overflow
                if(deque.size() == 1) {
                    ((externalLeaf)node).setNextLeaf(-1);
                } else {
                    ((externalLeaf)node).setNextLeaf((current+1) * conf.pageSize);
                }
                node.writeNode(rf, conf);
                deque.pop();
            }
            current++;
        }

        return rf;
    }

    /**
     * write header data into a tree-file
     * @param treeFile the file that a header need to be write in
     * @param conf external tree configuration
     * @throws IOException
     */
    private void writeFileHeader(RandomAccessFile treeFile, externalConfiguration conf)
            throws IOException {
        treeFile.seek(0L);
        treeFile.writeInt(conf.pageSize);
        treeFile.writeInt(conf.keySize);
        treeFile.writeInt(conf.valueSize);
        treeFile.writeLong(conf.pageSize); // this is the pointer to the root page
    }
}
