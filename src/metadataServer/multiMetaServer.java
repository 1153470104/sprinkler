package metadataServer;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPlusTree;
import BPlusTree.externalTree;
import BPlusTree.keyType.MortonCode;
import metadataServer.rectangleTree.RTree;
import metadataServer.rectangleTree.RTreeLeaf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * the multi-server meta server
 * maintain the data storage information
 */
public class multiMetaServer {
    private RTree<MortonCode> externalTreeChunk;
    private String dataPath;
    private int[] boundTime;
    private BPlusTree<MortonCode, String>[] inMemoryTreeArray;

    /**
     * the init function of meta server
     * @param dataPath the path to store the external tree
     * @param num the number of index server
     * @param RTreeM the m of RTree
     */
    public multiMetaServer(String dataPath, int num, int RTreeM) {
        this.externalTreeChunk = new RTree<>(RTreeM);
        this.dataPath = dataPath;
        this.inMemoryTreeArray = new BPlusTree[num];
        this.boundTime = new int[num];
        for(int i = 0; i < num; i++) {
            inMemoryTreeArray[i] = null;
            boundTime[i] = -1;
        }
    }

    /**
     * update boundary time & in memory tree
     * @param time the current boundary time
     * @param tree the current in memory tree
     */
    public void update(int time, BPlusTree<MortonCode, String> tree, int id) {
        boundTime[id] = time;
        inMemoryTreeArray[id] = tree;
    }

    public void addTree(externalTree<MortonCode, String> tree) {
        this.externalTreeChunk.add(tree.getKeyStart(), tree.getKeyEnd(), tree.getTimeStart(), tree.getTimeEnd(), tree);
    }

    public int length(){
        return externalTreeChunk.size();
    }

    public String getDataPath() {
        return dataPath;
    }

    /**
     * the function to search the key according to a specific boundary
     * @param startTime the start boundary of time
     * @param endTime the end boundary of time
     * @param startkey the start key boundary of key
     * @param endkey the end key boundary of key
     * @return the key-value list in the query boundary
     * @throws IOException throws when any io operation fails
     * @throws NullPointerException throws when any input key is null
     */
    // TODO synchronization should be considered
    // TODO this part just have to make sure no more new external tree being flushed into metadata
    public List<BPTKey<MortonCode>> searchKey(
            int startTime, int endTime, MortonCode startkey, MortonCode endkey) throws IOException, NullPointerException {
        List<BPTKey<MortonCode>> keyList = new LinkedList<>();

        // search the in memory data
        boolean allInMemory = true;
        for(int i = 0; i < this.boundTime.length; i++) {
            if(boundTime[i] < endTime) {
                keyList.addAll(inMemoryTreeArray[i].search(startTime, endTime, startkey, endkey));
            }
            if(boundTime[i] > startTime) {
                allInMemory = false;
            }
        }

        // if the time region covers the external part of data
        if(!allInMemory) {
            List<externalTree> externalTreeList = externalTreeChunk.searchTree(startkey, endkey, startTime, endTime);
            for(externalTree tree: externalTreeList) {
                keyList.addAll(tree.searchNode( startTime, endTime, startkey, endkey));
            }
        }
        return keyList;
    }
}
