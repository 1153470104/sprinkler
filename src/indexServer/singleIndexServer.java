package indexServer;

import BPlusTree.*;
import BPlusTree.BPTKey.BPTKey;
import BPlusTree.keyType.MortonCode;
import dispatcher.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class singleIndexServer {
//    List<BPlusTree<K>> bptList;
    private BPlusTree<MortonCode> currentBpt;
    private List<externalTree<MortonCode>> externalTreeList;
    private List<BPlusTree<MortonCode>> treeList; // 1.0版：用于测试无外存b树时的系统
    private int time;

    dataTool dt;

    public singleIndexServer(String dataPath, int m) throws IOException {
        this.dt = new dataTool(dataPath);
        this.externalTreeList = new ArrayList<>();
        this.treeList = new ArrayList<>();
        this.treeList.add(new BPlusTreeScratched<MortonCode>(m));
        currentBpt = treeList.get(0);
//        bptList = new ArrayList<>();
//        bptList.add(currentBpt);
    }

    public void startIndexing() throws IOException {
        //get data
        System.out.println("****************** Index start ******************");
        BPTKey<MortonCode> keyEntry = dt.getEntry();
        this.time = dt.getTime(); // use dt to get dynamic time, update every time after getEntry operation
        currentBpt.setStartTime(this.time);
        //iterate & deal with data
        boolean flushed = false;
        while (keyEntry != null) {
            currentBpt.addKey(keyEntry);
            if(currentBpt.isTemplated()) {
                ((BPlusTreeTemplated)currentBpt).balance();
            }

            // 1.0版本
            if (currentBpt.isBlockFull()) {
                System.out.print("finish data region ");
                System.out.println(treeList.size());
                currentBpt.printInfo();
                currentBpt.setEndTime(this.time);
                treeList.add(new BPlusTreeTemplated<MortonCode>((BPlusTree<MortonCode>)currentBpt));
                currentBpt = treeList.get(treeList.size()-1);
                flushed = true;
            }

//            //if block full, store it in the disk
//            if (currentBpt.isBlockFull()) {
//                externalBPlusTreeList.add(new externalBPlusTree<MortonCode>(currentBpt));
//                currentBpt = new BPlusTreeTemplated<MortonCode>((BPlusTreeCommon<MortonCode>)currentBpt);
//            }

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
