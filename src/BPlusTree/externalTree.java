package BPlusTree;

import BPlusTree.BPTNode.externalNode;
import BPlusTree.configuration.externalConfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class externalTree<K extends Comparable> {
    private int timeStart;
    private int timeEnd;
    private K keyStart;
    private K keyEnd;

    private externalConfiguration conf;
    private RandomAccessFile treeFile;
    private long totalPages;
    private externalNode root;

    public externalTree(BPlusTree<K> tree, String filePath, externalConfiguration conf) throws IOException {
        this.timeStart = ((BPlusTree<K>)tree).getTimeStart();
        this.timeEnd = ((BPlusTree<K>)tree).getTimeEnd();
        this.keyStart = ((BPlusTree<K>)tree).getKeyStart();
        this.keyEnd = ((BPlusTree<K>)tree).getKeyEnd();
        this.conf = conf;
        this.treeFile = tree.storeFile(filePath, conf);
        this.totalPages = treeFile.length() / conf.pageSize;
    }
}