package BPlusTree;

import java.util.ArrayList;
import java.util.Arrays;
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

    public void addList(List<Integer> list) {
        for(Integer i: list) {
            bpt.addKey(new BPTValueKey<Integer, String>(i, Integer.toString(i)));
        }
    }

    public void makeBPT(int m, List<Integer> list) {
        initBPT(m);
        addList(list);
    }

    public static void main(String[] args) {
        mainDemo main1 = new mainDemo();
        main1.makeBPT(5, 3);
        System.out.println(main1.bpt.printData());

        main1.makeBPT(5, 13);
        System.out.println(main1.bpt.printData());

        main1.bpt.printData();
        main1.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36/*, 39, 49*/));
        System.out.println(main1.bpt.printData());

        main1.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36, 13, 15));
        System.out.println(main1.bpt.printData());
    }
}

