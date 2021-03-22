package metadataServer;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.BPlusTree;
import BPlusTree.externalTree;
import BPlusTree.keyType.MortonCode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class singleMetaServer {
    private List<externalTree<MortonCode>> externalTreeList;
    private String dataPath;
    private int boundTime;
    private BPlusTree<MortonCode> inMemoryTree;

    public singleMetaServer(String dataPath) {
        externalTreeList = new ArrayList<>();
        this.dataPath = dataPath;
    }

    /**
     * update boundary time & in memory tree
     * @param time the current boundary time
     * @param tree the current in memory tree
     */
    public void update(int time, BPlusTree<MortonCode> tree) {
        boundTime = time;
        inMemoryTree = tree;
    }

    public void addTree(externalTree<MortonCode> tree) {
        this.externalTreeList.add(tree);
    }

    public int length(){
        return externalTreeList.size();
    }

    public String getDataPath() {
        return dataPath;
    }

    // TODO synchronization should be considered
    // TODO this part just have to make sure no more new external tree being flushed into metadata
    public List<BPTKey<MortonCode>> searchKey(
            int startTime, int endTime, MortonCode startkey, MortonCode endkey) throws IOException, NullPointerException {

        List<BPTKey<MortonCode>> keyList = new LinkedList<>();
        // if the time region covers the external part of data
        if(this.boundTime > startTime) {
            for(externalTree<MortonCode> tree: externalTreeList) {
//                System.out.println("metaServer");
                List<BPTKey<MortonCode>> treeKeyList = new LinkedList<>();
                if(tree.getTimeStart() <= startTime && tree.getTimeEnd() >= startTime) {
                    treeKeyList = tree.searchNode(startTime, endTime, startkey, endkey);
                } else if(tree.getTimeStart() >= startTime && tree.getTimeEnd() <= endTime) {
                    treeKeyList = tree.searchNode(startkey, endkey);
                } else if(tree.getTimeStart() <= endTime && tree.getTimeEnd() >= endTime) {
                    treeKeyList = tree.searchNode(startTime, endTime, startkey, endkey);
                }
                keyList.addAll(treeKeyList);
            }
        }

        // search the in memory data
        if(this.boundTime < endTime) {
//            System.out.println("test query out");
            List<BPTKey<MortonCode>> treeKeyList = inMemoryTree.search(startTime, endTime, startkey, endkey);
//            System.out.println("test query out2");
            keyList.addAll(treeKeyList);
        }

        return keyList;
    }
}
