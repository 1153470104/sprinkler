package client;

import indexServer.multiIndexServer;
import metadataServer.multiMetaServer;
import queryServer.multiQueryServer;

import java.io.IOException;

public class multiTreeClient {
    private static multiIndexServer indexServer;
    private static multiQueryServer queryServer;
//    private static multiMetaServer metaServer = new multiMetaServer("resource/database/0319-");

    static {
//        try {
//            indexServer = new multiIndexServer(
//                    "resource/data/100000s.txt", 20, metaServer);
//            queryServer = new multiQueryServer(metaServer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * indexing thread
     */
    static class indexThread extends Thread{
        public void run(){
            try {
                indexServer.startIndexing();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * querying thread
     */
    static class queryThread extends Thread{
//        public void run(){
//            try {
//                queryServer.querying();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public static void main(String [] args){
        multiTreeClient.indexThread index = new multiTreeClient.indexThread();
        index.start();

        multiTreeClient.queryThread query = new multiTreeClient.queryThread();
        query.start();
    }
}
