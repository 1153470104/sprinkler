package indexServer;

import BPlusTree.*;
import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPlusTree;
import BPlusTree.configuration.configuration;
import BPlusTree.keyType.MortonCode;
import dispatcher.dispatcher;
import dispatcher.dispatcher.*;
import metadataServer.multiMetaServer;

import javax.swing.*;
import java.io.IOException;

/**
 * this Server is use to index data with multi other index server
 */
public class multiIndexServer {
    private BPlusTree<MortonCode, String> currentBpt;
    private int time;
    private configuration conf;
    private multiMetaServer metaServer;
    private int id;

    private dispatcher dp;

    public multiIndexServer(configuration conf, multiMetaServer metaServer, dispatcher dp, int id) throws IOException {
        this.id = id;
        this.dp = dp;

        /*jesus!!! one bug occur: Long.class was mis-write into long.class
         * so that, the conf.readKey function fails !!! */
        currentBpt = new BPlusTreeScratched<MortonCode, String>(conf);
        this.conf = new configuration(8, 21, Long.class, String.class);
        this.dp.updateTree(this.currentBpt, id);
        this.metaServer = metaServer;
    }

    /**
     * the indexing starting function
     * @throws IOException is thrown when a data I/O operation fails
     */
    public void startIndexing() throws IOException, InterruptedException {
        //get data
        System.out.println("****************** Index start ******************");
        /* forget to update at very first,
        so null pointer exception occurs in metaServer's search*/
        metaServer.update(-1, currentBpt, this.id);
        BPTKey<MortonCode> keyEntry = null;  // TODO maybe这一块需要考虑synchronize
        // 但这一块就不考虑数据输入会终止这件事情了。。。假设是数据流
        // TODO 未来肯定要加数据中断之类的处理，这里先没有。。。
        //还得考虑getEntry null的问题
        entry newEntry = null; //get out one time, make sure synchronization

        while (newEntry == null) {
            newEntry = dp.getEntry(id); //get out one time, make sure synchronization
        }
        keyEntry = newEntry.key;
        this.time = newEntry.time; // use dp to get dynamic time, update every time after getEntry operation
        currentBpt.setStartTime(this.time);

        //iterate & deal with data
        boolean flushed = false;
        while (true) {
            // in order to use bloom filter the next two statements should be bond together
            currentBpt.addKey(keyEntry);
            if(!currentBpt.isBlockFull()) {
                currentBpt.addBloomfilter(this.time);
            }
            if(currentBpt.isTemplate()) {
                ((BPlusTreeTemplated)currentBpt).balance(false);
            }

            // 2.0版本
            // if block full, store it in the disk
            if (currentBpt.isBlockFull()) {
                // TODO 有问题
                this.dp.balanceSchema(true);  // 目前选择在每次有块被写入外存的时候，reset一次schema
                System.out.print("finish data region ");
                currentBpt.setEndTime(this.time);
                currentBpt.printInfo();

                //flush old tree into disk
                metaServer.addTree(new externalTree<MortonCode, String>(
                        currentBpt, metaServer.getDataPath()+ metaServer.length(), conf));

                System.out.println(id + " server stores a tree\nthe bound: "
                        +currentBpt.getKeyStart()+", "+currentBpt.getKeyEnd()+", "+currentBpt.getTimeStart()+", "+currentBpt.getTimeEnd());
                //create a new template tree
                currentBpt = new BPlusTreeTemplated<MortonCode, String>(currentBpt);
                this.dp.updateTree(this.currentBpt, id); // update the schema of this tree
                metaServer.update(time, currentBpt, id); // update the time in metaServer
                flushed = true;
            } else {
                //只有你正常时候才会需要读一个新的，如果新建树，上一个树没放入的块可以继续放
                newEntry = null;  // TODO maybe这一块需要考虑synchronize
                while (newEntry == null) {
                    /*
                     * // 多线程占用的问题好像蛮严重的。。。
                     * // 会直接导致其中一个线程无法得到 dispatch 的资源
                     * // 所以被迫sleep一会儿才行 又不对了！！！
                     *
                     * 所有的所有，所有的问题在我注释掉所有sleep甚至都不用抛出异常之后，消失了！！！yes！
                     */
                    newEntry = dp.getEntry(id); //get out one time, make sure synchronization
                }
                keyEntry = newEntry.key;
                this.time = newEntry.time; // use dp to get dynamic time, update every time after getEntry operation
                if(flushed) {
                    currentBpt.setStartTime(this.time);
                    flushed= false;
                }
            }
        }
    }


    public void guiIndexing(JTextArea statusArea) throws IOException, InterruptedException {
        //get data
        statusArea.append("****************** Index start ******************\n");
        /* forget to update at very first,
        so null pointer exception occurs in metaServer's search*/
        metaServer.update(-1, currentBpt, this.id);
        BPTKey<MortonCode> keyEntry = null;  // TODO maybe这一块需要考虑synchronize
        // 但这一块就不考虑数据输入会终止这件事情了。。。假设是数据流
        // TODO 未来肯定要加数据中断之类的处理，这里先没有。。。
        //还得考虑getEntry null的问题
        entry newEntry = null; //get out one time, make sure synchronization

        while (newEntry == null) {
            newEntry = dp.getEntry(id); //get out one time, make sure synchronization
        }
        keyEntry = newEntry.key;
        this.time = newEntry.time; // use dp to get dynamic time, update every time after getEntry operation
        currentBpt.setStartTime(this.time);

        //iterate & deal with data
        boolean flushed = false;
        while (true) {
            currentBpt.addKey(keyEntry);
            currentBpt.addBloomfilter(this.time);
            if(currentBpt.isTemplate()) {
                ((BPlusTreeTemplated)currentBpt).balance(false);
            }

            // 2.0版本
            // if block full, store it in the disk
            if (currentBpt.isBlockFull()) {
                dp.initSchema();  // 目前选择在每次有块被写入外存的时候，reset一次schema
                statusArea.append("finish data region \n");
                currentBpt.setEndTime(this.time);
                statusArea.append(currentBpt.getInfo());
                //flush old tree into disk
                metaServer.addTree(new externalTree<MortonCode, String>(
                        currentBpt, metaServer.getDataPath()+ metaServer.length(), conf));
                statusArea.append(id + " server stores a tree\nthe bound: "+currentBpt.getKeyStart()+", "
                        +currentBpt.getKeyEnd()+", "+currentBpt.getTimeStart()+", "+currentBpt.getTimeEnd()+"\n");
                //create a new template tree
                currentBpt = new BPlusTreeTemplated<MortonCode, String>(currentBpt);
                this.dp.updateTree(this.currentBpt, id);
                metaServer.update(time, currentBpt, id); //update the time in metaServer
                flushed = true;
            }

            newEntry = null;  // TODO maybe这一块需要考虑synchronize
            while (newEntry == null) {
                /*
                 * // 多线程占用的问题好像蛮严重的。。。
                 * // 会直接导致其中一个线程无法得到 dispatch 的资源
                 * // 所以被迫sleep一会儿才行 又不对了！！！
                 *
                 * 所有的所有，所有的问题在我注释掉所有sleep甚至都不用抛出异常之后，消失了！！！yes！
                 */
                newEntry = dp.getEntry(id); //get out one time, make sure synchronization
            }
            keyEntry = newEntry.key;
            this.time = newEntry.time; // use dp to get dynamic time, update every time after getEntry operation
            if(flushed) {
                currentBpt.setStartTime(this.time);
                flushed= false;
            }
        }
    }
}
