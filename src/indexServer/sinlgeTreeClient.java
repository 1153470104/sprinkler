package indexServer;

import BPlusTree.*;
import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTNode.BPTNode;
import dispatcher.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class sinlgeTreeClient<K extends Comparable> {
//    List<BPlusTree<K>> bptList;
    private BPlusTree<K> currentBpt;
    private List<externalBPlusTree<K>> externalBPlusTreeList;

    //两种方式一种stream，一种直接读取为list放在内存里
    dataTool dt;
    dataStream ds;

    public sinlgeTreeClient(String dataPath, int m) {
        dt = new dataTool(dataPath);
        externalBPlusTreeList = new ArrayList<>();
        currentBpt = new BPlusTreeScratched<K>(m);
//        bptList = new ArrayList<>();
//        bptList.add(currentBpt);
    }

    public void start() throws IOException {
        //get data
        List<BPTKey> keyList = dt.entryList();

        //iterate & deal with data
        for (BPTKey kenEntry: keyList) {
            currentBpt.addKey(kenEntry);
            if(currentBpt.isTemplated()) {
                ((BPlusTreeTemplated)currentBpt).balance();
            }

            //if block full, store it in the disk
            if (currentBpt.isBlockFull()) {
                externalBPlusTreeList.add(new externalBPlusTree<K>(currentBpt));
                currentBpt = new BPlusTreeTemplated<K>((BPlusTreeCommon<K>)currentBpt);
            }
        }

    }
}
