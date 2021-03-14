package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTNode.externalNode;
import BPlusTree.configuration.externalConfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
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

    public BPlusTree<K> extractTree() {
        return null;
    }

    public List<BPTKey<K>> searchNode(String time1, String time2, K key1, K key2) {
        return null;
    }
}
