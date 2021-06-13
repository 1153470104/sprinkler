package client;

import BPlusTree.configuration.configuration;
import dispatcher.dispatcher;
import indexServer.multiIndexServer;
import metadataServer.multiMetaServer;
import queryServer.multiQueryServer;

import javax.swing.*;
import java.io.IOException;

/**
 * gui client of system
 *
 * with gui, more info could be revealed at a time
 */
public class guiClient {
    private multiIndexServer indexServer1;
    private multiIndexServer indexServer2;
    private multiIndexServer indexServer3;
    private multiIndexServer indexServer4;
    public multiQueryServer queryServer;

    private JTextArea queryArea;
    private JTextArea statusArea;
    private JTextArea dataArea;

    private  multiMetaServer metaServer = new multiMetaServer("resource/database/0406-", 4, 16);

    public guiClient(JTextArea queryArea, JTextArea statusArea, JTextArea dataArea) {
        this.queryArea = queryArea;
        this.statusArea = statusArea;
        this.dataArea = dataArea;
        try {
            int indexM = 20;
            configuration conf = new configuration(8, 21, Long.class, String.class, 2);
            dispatcher dp = new dispatcher("resource/data/data_sort/", 4, 500, dataArea, statusArea);

            indexServer1 = new multiIndexServer(conf, metaServer, dp, 0);
            indexServer2 = new multiIndexServer(conf, metaServer, dp, 1);
            indexServer3 = new multiIndexServer(conf, metaServer, dp, 2);
            indexServer4 = new multiIndexServer(conf, metaServer, dp, 3);
            queryServer = new multiQueryServer(metaServer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * indexing thread1, with 2 3 4 below
     */
    class indexThread1 extends Thread{
        public void run(){
            try {
                indexServer1.guiIndexing(statusArea);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class indexThread2 extends Thread{
        public void run(){
            try {
                indexServer2.guiIndexing(statusArea);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class indexThread3 extends Thread{
        public void run(){
            try {
                indexServer3.guiIndexing(statusArea);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class indexThread4 extends Thread{
        public void run(){
            try {
                indexServer4.guiIndexing(statusArea);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * querying thread
     */
    class queryThread extends Thread{
        public void run(){
            try {
                queryServer.guiQuerying(queryArea, statusArea);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startSystem() throws InterruptedException {
        indexThread1 index1 = new indexThread1();
        indexThread2 index2 = new indexThread2();
        indexThread3 index3 = new indexThread3();
        indexThread4 index4 = new indexThread4();
        queryThread query = new queryThread();

        index1.start();
        index2.start();
        index3.start();
        index4.start();

        Thread.sleep(50);
        query.start();
    }
}
