package client;

import dispatcher.dispatcher;
import indexServer.multiIndexServer;
import metadataServer.multiMetaServer;
import queryServer.multiQueryServer;

import java.io.IOException;

public class multiTreeClient {
    private static multiIndexServer indexServer1;
    private static multiIndexServer indexServer2;
    private static multiIndexServer indexServer3;
    private static multiIndexServer indexServer4;
    private static multiQueryServer queryServer;

    private static multiMetaServer metaServer = new multiMetaServer("resource/database/0406-", 4, 16);

    static {
        try {
            int indexM = 20;
            dispatcher dp = new dispatcher("resource/data/100000s.txt", 4, 500);

            indexServer1 = new multiIndexServer(indexM, metaServer, dp, 0);
            indexServer2 = new multiIndexServer(indexM, metaServer, dp, 1);
            indexServer3 = new multiIndexServer(indexM, metaServer, dp, 2);
            indexServer4 = new multiIndexServer(indexM, metaServer, dp, 3);
            queryServer = new multiQueryServer(metaServer);
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
     * querying thread
     */
    static class queryThread extends Thread{
        public void run(){
            try {
                queryServer.querying();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String [] args) throws InterruptedException {
        multiTreeClient.indexThread1 index1 = new multiTreeClient.indexThread1();
        multiTreeClient.indexThread2 index2 = new multiTreeClient.indexThread2();
        multiTreeClient.indexThread3 index3 = new multiTreeClient.indexThread3();
        multiTreeClient.indexThread4 index4 = new multiTreeClient.indexThread4();
        multiTreeClient.queryThread query = new multiTreeClient.queryThread();

        index1.start();
        index2.start();
        index3.start();
        index4.start();

        Thread.sleep(50);
        query.start();
    }
}
