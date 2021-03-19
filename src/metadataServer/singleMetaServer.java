package metadataServer;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.externalTree;
import BPlusTree.keyType.MortonCode;

import java.util.ArrayList;
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
}
