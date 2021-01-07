package BPlusTree;

import java.util.ArrayList;
import java.util.List;

public class mainDemo {
    private List<BPTKey<Integer>> keyList;
    private BPlusTree bpt;

    public mainDemo(){
    }

    public void initBPT(int m) {
        bpt = new BPlusTreeCommon(m);
    }

    public void initKeyList(int number) {
        keyList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            keyList.add(new BPTValueKey<Integer, String>(i, Integer.toString(i)));
        }
    }

    public void addList() {
        for(BPTKey<Integer> key: keyList){
            bpt.addKey(key);
        }
    }

    public void makeBPT(int m, int length) {
        initBPT(m);
        initKeyList(length);
        addList();
    }
    public static void main(String[] args) {
        mainDemo main1 = new mainDemo();
        main1.makeBPT(5, 3);
        main1.bpt.printbasic();
    }
}

