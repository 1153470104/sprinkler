package client;

import BPlusTree.configuration.configuration;
import indexServer.*;
import metadataServer.singleMetaServer;
import queryServer.*;

import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * the client of the whole system
 * use two threads to realize synchronous indexing & query
 */
public class singleTreeClient {
    private static singleIndexServer indexServer;
    private static singleQueryServer queryServer;
    private static singleMetaServer metaServer = new singleMetaServer("resource/database/0319-");

    static {
        try {
            configuration conf = new configuration(8, 21, Long.class, String.class);
            indexServer = new singleIndexServer(
                    "resource/data/100000s.txt", conf, metaServer);
            queryServer = new singleQueryServer(metaServer);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        public void run(){
            try {
                queryServer.querying();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String [] args){
        indexThread index = new indexThread();
        index.start();

        queryThread query = new queryThread();
        query.start();
    }
}
