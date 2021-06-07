package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.BPTNode.externalLeaf;
import BPlusTree.BPTNode.externalNode;
import BPlusTree.BPTNode.externalNonLeaf;
import BPlusTree.configuration.configuration;
import BPlusTree.keyType.MortonCode;
import dispatcher.dataTool;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
 * class of external tree
 *
 * the structure of B+ tree on disk:
 *
 *     header：first block 用于存储基本信息，以及根节点的块位置
 *   | pageSize | keySize | valueSize | rootPointer |
 *
 *     externalNonLeaf：存储非叶节点的信息与数据
 *   |nodeType | length | pointer | key | pointer | key | ... | pointer |
 *
 *     externalLeaf：存储叶节点的信息与数据
 *   | nodeType | length | prev | next | key | value | key | ... | value |
 *
 * @param <K> the key's type
 */
public class externalTree<K extends Comparable, V> {
    private int timeStart;
    private int timeEnd;
    private K keyStart;
    private K keyEnd;

    private configuration conf;
    private RandomAccessFile treeFile;
    private long totalPages;
    private externalNode root;
    private int m;
    private String filePath;

    private bloomFilter bf;
    private Map<Long, bloomFilter> blockBloomFilterList;

    /**
     * init of an external tree
     * @param tree the tree in memory
     * @param filePath the file to store external tree
     * @param conf the configuration of external tree
     * @throws IOException be thrown when an I/O operation fails
     */
    public externalTree(BPlusTree<K, V> tree, String filePath, configuration conf) throws IOException {
        this.timeStart = ((BPlusTree<K, V>)tree).getTimeStart();
        this.timeEnd = ((BPlusTree<K, V>)tree).getTimeEnd();
        this.keyStart = ((BPlusTree<K, V>)tree).getKeyStart();
        this.keyEnd = ((BPlusTree<K, V>)tree).getKeyEnd();
        this.conf = conf;

        this.bf = tree.bf;
        this.blockBloomFilterList = new HashMap<>();

        this.treeFile = tree.storeFile(filePath, conf, this.blockBloomFilterList);
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
    public List<BPTKey<K>> searchDomain(int tStart, int tEnd, K key1, K key2) throws IOException {
        // TODO the abuse & misuse of generic would finally kill this system
        // TODO which I should optimize the core structure of key & value
        List<BPTKey<K>> domainKeys = new LinkedList<>();
        if(key1.compareTo(key2) != -1) {
            System.out.println("oops, the domain do not exists");
            return domainKeys;
        }
        externalNode<K> cur = this.readNode(1*conf.pageSize);
        long nextIndex = 0;
        while(cur.getNodeType()!=1){
            int pointerIndex = cur.searchKey(key1);
            nextIndex = ((externalNonLeaf)cur).getPointer(pointerIndex);
            cur = this.readNode(nextIndex);
        }
        while(cur.searchKey(key2)!=-1) {
            if(blockBloomFilterList.get(nextIndex).isInRegion(tStart, tEnd)) {
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
            }
            nextIndex = ((externalLeaf)cur).getNextLeaf();
            if(nextIndex == -1) {
                return domainKeys;
            }
            cur = this.readNode(nextIndex);

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
        List<BPTKey<K>> domainKeys = new LinkedList<>();

        // this part is using bloom filter to test if there's entry in that time gap
        if (!bf.isInRegion(timeStart, timeEnd)) { /*emmm既然一个bf.isInRegion搞定，为什么要写个新函数*/
            return domainKeys;
        }
        List<BPTKey<K>> rawKeys = this.searchDomain(tStart, tEnd, key1, key2);
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
