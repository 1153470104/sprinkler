package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.BPTNode.externalLeaf;
import BPlusTree.BPTNode.externalNode;
import BPlusTree.BPTNode.externalNonLeaf;
import BPlusTree.configuration.externalConfiguration;
import BPlusTree.keyType.MortonCode;
import dispatcher.dataTool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

/**
 * class of external tree
 *
 * @param <K> the key's type
 */
public class externalTree<K extends Comparable> {
    private int timeStart;
    private int timeEnd;
    private K keyStart;
    private K keyEnd;

    private externalConfiguration conf;
    private RandomAccessFile treeFile;
    private long totalPages;
    private externalNode root;
    private int m;
    private String filePath;

    /**
     * init of an external tree
     * @param tree the tree in memory
     * @param filePath the file to store external tree
     * @param conf the configuration of external tre
     * @throws IOException be thrown when an I/O operation fails
     */
    public externalTree(BPlusTree<K> tree, String filePath, externalConfiguration conf) throws IOException {
        this.timeStart = ((BPlusTree<K>)tree).getTimeStart();
        this.timeEnd = ((BPlusTree<K>)tree).getTimeEnd();
        this.keyStart = ((BPlusTree<K>)tree).getKeyStart();
        this.keyEnd = ((BPlusTree<K>)tree).getKeyEnd();
        this.conf = conf;
        this.treeFile = tree.storeFile(filePath, conf);
        this.totalPages = treeFile.length() / conf.pageSize;
        this.m = tree.m;
        this.filePath = filePath;
    }

    /**
     * read a node on disk according its page index
     * @param index the page index of the node
     * @return a external node
     * @throws IOException fails when an I/O operation fails
     */
    public externalNode<K> readNode(long index) throws IOException {
        treeFile.seek(index);
        byte[] buffer = new byte[conf.pageSize];
        treeFile.read(buffer);
        ByteBuffer bbuffer = ByteBuffer.wrap(buffer);bbuffer.order(ByteOrder.BIG_ENDIAN);
        short nodeType = bbuffer.getShort();
        int length = bbuffer.getInt();
        externalNode<K> node = null;
        if( nodeType == 0) { // non leaf node
            //get the header
            node = new externalNonLeaf<K>(nodeType, length, index);
            // TODO get the key-value pair
            ((externalNonLeaf)node).addPointer(bbuffer.getLong());
            for(int i = 0; i < length; i++) {
                node.addKey((K)conf.readKey(bbuffer));
                ((externalNonLeaf)node).addPointer(bbuffer.getLong());
            }
        } else if( nodeType == 1) { // leaf node
            // get the header
            long prev = bbuffer.getLong();
            long next = bbuffer.getLong();
            node = new externalLeaf<K>(nodeType, length, index, prev, next);
            // TODO get the key-pointer pair
            for(int i = 0; i < length; i++) {
                ((externalLeaf<K>)node).addKey((K)conf.readKey(bbuffer), conf.readValue(bbuffer));
            }
        }

        return node;
    }

    /**
     * search the nodes between key1 & key2
     * @param key1 the start search key
     * @param key2 the end search key
     * @return the list of keys in that domain
     * @throws IOException throws when any I/O operation fails
     */
    public List<BPTKey<K>> searchNode(K key1, K key2) throws IOException {
        // TODO the abuse & misuse of generic would finally kill this system
        // TODO which I should optimize the core structure of key & value
        List<BPTKey<K>> domainKeys = new LinkedList<>();
        if(key1.compareTo(key2) != -1) {
            System.out.println("oops, the domain do not exists");
            return domainKeys;
        }
        externalNode<K> cur = this.readNode(1*conf.pageSize);
        while(cur.getNodeType()!=1){
            System.out.print("node level ");
            System.out.println(cur.toString());
            int pointerIndex = cur.searchKey(key1);
            cur = this.readNode(((externalNonLeaf)cur).getPointer(pointerIndex));
        }
        while(cur.searchKey(key2)!=-1) {
            System.out.println("leaf level");
            int start = cur.searchKey(key1);
            if (start == -1) start = 0;
            int end = cur.searchKey(key2);
            for(int i = start; i < end; i++) {
                //getKey already get the key-value pair
                domainKeys.add(((externalLeaf)cur).getKey(i));
            }
            if(end < cur.getLength()) {
                // if the key2 doesn't extend out the boundary,
                // test if the last key is equals to key2
                // if so, add the end key-value pair into domain-keys
                BPTKey temp = ((externalLeaf)cur).getKey(end);
                if(temp.key().compareTo(key2) == 0) {
                    domainKeys.add(temp);
                }
            }
            long next = ((externalLeaf)cur).getNextLeaf();
            if(next == -1) {
                return domainKeys;
            }
            cur = this.readNode(next);

        }
        return domainKeys;
    }

    /**
     * a sinple realization of time-key search function on one tree,
     * version1 without any optimization, just use the prev function to get all
     *          then do a traversal again
     * @param tStart the start search time
     * @param tEnd the end search time
     * @param key1 the start search key
     * @param key2 the end search key
     * @return the list of BPTValueKeys which in the time domain & region domain
     * @throws IOException thrown when any I/O function fails
     */
    public List<BPTKey<K>> searchNode(int tStart, int tEnd, K key1, K key2) throws IOException {
        System.out.println("externalTree");
        List<BPTKey<K>> domainKeys = new LinkedList<>();
        List<BPTKey<K>> rawKeys = this.searchNode(key1, key2);
        for(BPTKey k: rawKeys) {
            //TODO this place is not generic any more,
            //TODO the timestamp should have a better way to be brought in
            if(dataTool.inTimeDomain((BPTValueKey<MortonCode, String>) k, tStart, tEnd)) {
                domainKeys.add(k);
            }
        }
        return domainKeys;
    }

    public String valueListPrint(List<BPTKey<K>> valueList) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < valueList.size(); i++) {
            stringBuilder.append(valueList.get(i).key()).append(":");
            stringBuilder.append(((BPTValueKey)valueList.get(i)).getValue()).append("|");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }

    public int getTimeStart() {
        return timeStart;
    }

    public int getTimeEnd() {
        return timeEnd;
    }

    public K getKeyStart() {
        return keyStart;
    }

    public K getKeyEnd() {
        return keyEnd;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public int getM() {
        return m;
    }

    public String getFilePath() {
        return filePath;
    }
}
