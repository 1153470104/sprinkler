package indexServer;

import BPlusTree.*;
import BPlusTree.BPTKey.BPTKey;
import BPlusTree.configuration.configuration;
import BPlusTree.keyType.MortonCode;
import dispatcher.*;
import metadataServer.singleMetaServer;

import java.io.IOException;

/**
 * index server for single in memory node version
 */
public class singleIndexServer {
//    List<BPlusTree<K>> bptList;
    private BPlusTree<MortonCode, String> currentBpt;
    private int time;
    private configuration conf;
    private singleMetaServer metaServer;
//    private List<externalTree<MortonCode>> externalTreeList;
//    private String externalBase;
//    private List<BPlusTree<MortonCode>> treeList; // 1.0版：用于测试无外存b树时的系统

    dataTool dt;

    public singleIndexServer(String dataPath, configuration conf, singleMetaServer metaServer) throws IOException {
        this.dt = new dataTool(dataPath);
//        this.externalTreeList = new ArrayList<>();
        /*jesus!!! one bug occur: Long.class was mis-write into long.class
        * so that, the conf.readKey function fails !!! */
        this.conf = new configuration(8, 21, Long.class, String.class);
        currentBpt = new BPlusTreeScratched<MortonCode, String>(conf);
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
                ((BPlusTreeTemplated)currentBpt).balance(false);
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
                metaServer.addTree(new externalTree<MortonCode, String>(
                        currentBpt, metaServer.getDataPath()+ metaServer.length(), conf));
                //create a new template tree
                currentBpt = new BPlusTreeTemplated<MortonCode, String>(currentBpt);
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
