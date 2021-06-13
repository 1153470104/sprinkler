package client;

import BPlusTree.configuration.configuration;
import dispatcher.dispatcher;
import indexServer.multiIndexServer;
import metadataServer.multiMetaServer;
import queryServer.multiQueryServer;

import java.io.IOException;

/**
 * the client of multi-server system
 * use 4 index thread & 1 query server to realize the system
 */
public class multiQueryClient {
    private static multiIndexServer indexServer1;
    private static multiIndexServer indexServer2;
    private static multiIndexServer indexServer3;
    private static multiIndexServer indexServer4;
    public static multiQueryServer queryServer1;
    public static multiQueryServer queryServer2;

    private static multiMetaServer metaServer = new multiMetaServer("resource/database/05018-", 4, 16);

    static {
        try {
            int indexM = 20;
            configuration conf = new configuration(8, 21, Long.class, String.class, 4);
            dispatcher dp = new dispatcher("resource/data/data_sort/", 4);

            indexServer1 = new multiIndexServer(conf, metaServer, dp, 0);
            indexServer2 = new multiIndexServer(conf, metaServer, dp, 1);
            indexServer3 = new multiIndexServer(conf, metaServer, dp, 2);
            indexServer4 = new multiIndexServer(conf, metaServer, dp, 3);
            queryServer1 = new multiQueryServer(metaServer);
            queryServer2 = new multiQueryServer(metaServer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * indexing thread1, with 2 3 4 below
     */
    static class indexThread1 extends Thread{
        public void run(){
            try {
                indexServer1.startIndexing();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class indexThread2 extends Thread{
        public void run(){
            try {
                indexServer2.startIndexing();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class indexThread3 extends Thread{
        public void run(){
            try {
                indexServer3.startIndexing();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class indexThread4 extends Thread{
        public void run(){
            try {
                indexServer4.startIndexing();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * querying thread 1
     */
    static class queryThread1 extends Thread{
        public void run(){
            try {
                queryServer1.querying();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * querying thread 2
     */
    static class queryThread2 extends Thread{
        public void run(){
            try {
                queryServer2.querying();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void startSystem() throws InterruptedException {
        multiQueryClient.indexThread1 index1 = new multiQueryClient.indexThread1();
        multiQueryClient.indexThread2 index2 = new multiQueryClient.indexThread2();
        multiQueryClient.indexThread3 index3 = new multiQueryClient.indexThread3();
        multiQueryClient.indexThread4 index4 = new multiQueryClient.indexThread4();
        multiQueryClient.queryThread1 query1 = new multiQueryClient.queryThread1();
        multiQueryClient.queryThread2 query2 = new multiQueryClient.queryThread2();

        index1.start();
        index2.start();
        index3.start();
        index4.start();

        Thread.sleep(50);
        query1.start();
        query2.start();
    }

    public static void main(String [] args) throws InterruptedException {
        multiQueryClient.startSystem();
    }
}


