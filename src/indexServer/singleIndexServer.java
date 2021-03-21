package indexServer;

import BPlusTree.*;
import BPlusTree.BPTKey.BPTKey;
import BPlusTree.configuration.externalConfiguration;
import BPlusTree.keyType.MortonCode;
import dispatcher.*;
import metadataServer.singleMetaServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * index server for single in memory node version
 */
public class singleIndexServer {
//    List<BPlusTree<K>> bptList;
    private BPlusTree<MortonCode> currentBpt;
    private int time;
    private externalConfiguration conf;
    private singleMetaServer metaServer;
//    private List<externalTree<MortonCode>> externalTreeList;
//    private String externalBase;
//    private List<BPlusTree<MortonCode>> treeList; // 1.0版：用于测试无外存b树时的系统

    dataTool dt;

    public singleIndexServer(String dataPath, int m, singleMetaServer metaServer) throws IOException {
        this.dt = new dataTool(dataPath);
//        this.externalTreeList = new ArrayList<>();
        this.conf = new externalConfiguration(8, 21, long.class, String.class);
        currentBpt = new BPlusTreeScratched<MortonCode>(m);
//        this.externalBase = externalBase;
        this.metaServer = metaServer;
//        bptList = new ArrayList<>();
//        bptList.add(currentBpt);
    }

    /**
     * the indexing starting function
     * @throws IOException is thrown when a data I/O operation fails
     */
    public void startIndexing() throws IOException {
        //get data
        System.out.println("****************** Index start ******************");
        BPTKey<MortonCode> keyEntry = dt.getEntry();
        this.time = dt.getTime(); // use dt to get dynamic time, update every time after getEntry operation
        currentBpt.setStartTime(this.time);
        /* forget to update at very first,
        so null pointer exception occurs in metaServer's search*/
        metaServer.update(-1, currentBpt);
        //iterate & deal with data
        boolean flushed = false;
        while (keyEntry != null) {
            currentBpt.addKey(keyEntry);
            if(currentBpt.isTemplate()) {
                ((BPlusTreeTemplated)currentBpt).balance();
            }

            // 1.0版本
//            if (currentBpt.isBlockFull()) {
//                System.out.print("finish data region ");
////                System.out.println(treeList.size());
//                currentBpt.printInfo();
//                currentBpt.setEndTime(this.time);
//                currentBpt = new BPlusTreeTemplated<MortonCode>((BPlusTree<MortonCode>)currentBpt);
//                flushed = true;
//            }

            // 2.0版本
            // if block full, store it in the disk
            if (currentBpt.isBlockFull()) {
                System.out.print("finish data region ");
                currentBpt.setEndTime(this.time);
                currentBpt.printInfo();
                //flush old tree into disk
                metaServer.addTree(new externalTree<MortonCode>(
                        currentBpt, metaServer.getDataPath()+ metaServer.length(), conf));
                //create a new template tree
                currentBpt = new BPlusTreeTemplated<MortonCode>(currentBpt);
                metaServer.update(time, currentBpt); //update the time in metaServer
                flushed = true;
            }

            try{
                Thread.sleep(50);
                keyEntry = dt.getEntry();
                this.time = dt.getTime();
                if(flushed) {
                    currentBpt.setStartTime(this.time);
                    flushed= false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
