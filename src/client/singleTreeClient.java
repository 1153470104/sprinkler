package client;

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
            indexServer = new singleIndexServer(
                    "resource/data/100000s.txt", 20, metaServer);
            queryServer = new singleQueryServer(metaServer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class indexThread extends Thread{
        public void run(){
            try {
                indexServer.startIndexing();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class queryThread extends Thread{
        public void run(){
            queryServer.querying();
        }
    }

    public static void main(String [] args){
        indexThread index = new indexThread();
        index.start();

        queryThread query = new queryThread();
        query.start();
    }
}
