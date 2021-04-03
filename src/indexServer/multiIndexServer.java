package indexServer;

import BPlusTree.*;
import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPlusTree;
import BPlusTree.configuration.externalConfiguration;
import BPlusTree.keyType.MortonCode;
import dispatcher.dataTool;
import dispatcher.dispatcher;
import metadataServer.multiMetaServer;

import java.io.IOException;

/**
 * this Server is use to index data with multi other index server
 */
public class multiIndexServer {
    private BPlusTree<MortonCode, String> currentBpt;
    private int time;
    private externalConfiguration conf;
    private multiMetaServer metaServer;
    private int id;

    private dispatcher dp;

    public multiIndexServer(int m, multiMetaServer metaServer, dispatcher dp, int id) throws IOException {
        this.id = id;
        this.dp = dp;
        /*jesus!!! one bug occur: Long.class was mis-write into long.class
         * so that, the conf.readKey function fails !!! */
        this.conf = new externalConfiguration(8, 21, Long.class, String.class);
        currentBpt = new BPlusTreeScratched<MortonCode, String>(m);
        this.metaServer = metaServer;
    }

    /**
     * the indexing starting function
     * @throws IOException is thrown when a data I/O operation fails
     */
    public void startIndexing() throws IOException {
        //get data
        System.out.println("****************** Index start ******************");
        //TODO 还得考虑getEntry null的问题
        BPTKey<MortonCode> keyEntry = null;  // TODO maybe这一块需要考虑synchronize
        // 但这一块就不考虑数据输入会终止这件事情了。。。假设是数据流
        // TODO 未来肯定要加数据中断之类的处理，这里先没有。。。
        while (keyEntry == null) {
            keyEntry = dp.getEntry(id);
        }
        this.time = dp.getTime(); // use dp to get dynamic time, update every time after getEntry operation
        currentBpt.setStartTime(this.time);
        /* forget to update at very first,
        so null pointer exception occurs in metaServer's search*/
        metaServer.update(-1, currentBpt, this.id);
        //iterate & deal with data
        boolean flushed = false;
        while (true) {
            currentBpt.addKey(keyEntry);
            if(currentBpt.isTemplate()) {
                ((BPlusTreeTemplated)currentBpt).balance();
            }

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
                metaServer.update(time, currentBpt, id); //update the time in metaServer
                flushed = true;
            }

            try{
                Thread.sleep(12);  //终止12ms，使得
                keyEntry = null;  // TODO maybe这一块需要考虑synchronize
                while (keyEntry == null) {
                    keyEntry = dp.getEntry(id);
                }
                this.time = dp.getTime();
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
