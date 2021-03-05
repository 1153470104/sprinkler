package client;

import indexServer.*;
import queryServer.*;

import java.io.FileNotFoundException;
import java.io.IOException;


public class singleTreeClient {
    private static singleIndexServer indexServer;
    private static singleQueryServer queryServer;

    static {
        try {
            indexServer = new singleIndexServer("resource/data/100000s.txt", 10);
            queryServer = new singleQueryServer();
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
