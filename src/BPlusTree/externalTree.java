package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTNode.externalLeaf;
import BPlusTree.BPTNode.externalNode;
import BPlusTree.BPTNode.externalNonLeaf;
import BPlusTree.configuration.externalConfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * class of external tree
 *
 * @param <K> the key's type
 */
public class externalTree<K extends Comparable, V> {
    private int timeStart;
    private int timeEnd;
    private K keyStart;
    private K keyEnd;

    private externalConfiguration conf;
    private RandomAccessFile treeFile;
    private long totalPages;
    private externalNode root;
    private int m;

    /**
     * init of an external tree
     * @param tree the tree in memory
     * @param filePath the file to store external tree
     * @param conf the configuration of external tree
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
            long prev = bbuffer.getLong();
            long next = bbuffer.getLong();
            node = new externalLeaf<K, V>(nodeType, length, index, prev, next);
            // TODO get the key-value pair
            ((externalNonLeaf)node).addPointer(bbuffer.getLong());
            for(int i = 0; i < length; i++) {
                node.addKey((K)conf.readKey(bbuffer));
                ((externalNonLeaf)node).addPointer(bbuffer.getLong());
            }
        } else if( nodeType == 1) { // leaf node
            // get the header
            node = new externalNonLeaf<K>(nodeType, length, index);
            // TODO get the key-pointer pair
            for(int i = 0; i < length; i++) {
                ((externalLeaf<K, V>)node).addKey((K)conf.readKey(bbuffer), (V)conf.readValue(bbuffer));
            }
        }

        return node;
    }

    public List<BPTKey<K>> searchNode(String time1, String time2, K key1, K key2) {
        return null;
    }
}
