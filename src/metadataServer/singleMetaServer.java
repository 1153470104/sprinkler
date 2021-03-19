package metadataServer;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.externalTree;
import BPlusTree.keyType.MortonCode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class singleMetaServer {
    private List<externalTree<MortonCode>> externalTreeList;
    private String dataPath;

    public singleMetaServer(String dataPath) {
        externalTreeList = new ArrayList<>();
        this.dataPath = dataPath;
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
            int startTime, int endTime, MortonCode startkey, MortonCode endkey) throws IOException {
        List<BPTKey<MortonCode>> keyList = new LinkedList<>();
        for(externalTree<MortonCode> tree: externalTreeList) {
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
        return keyList;
    }
}
