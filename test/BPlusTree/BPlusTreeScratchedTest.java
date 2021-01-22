package BPlusTree;

import BPTKey.BPTKey;
import BPTKey.BPTValueKey;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BPlusTreeScratchedTest {
    private List<BPTKey<Integer>> keyList;
    private BPlusTree<Integer> bpt;

    public void initBPT(int m) {
        bpt = new BPlusTreeScratched<Integer>(m);
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

    public void addList(List<Integer> list) {
        for(Integer i: list) {
            bpt.addKey(new BPTValueKey<Integer, String>(i, Integer.toString(i)));
        }
    }

    public void makeBPT(int m, int length) {
        initBPT(m);
        initKeyList(length);
        addList();
    }

    public void makeBPT(int m, List<Integer> list) {
        initBPT(m);
        addList(list);
    }

    public String keyListString(List<BPTKey<Integer>> kList) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        for(BPTKey<Integer> n: kList) {
            sb.append(" ").append(n.getKey()).append(" |");
        }
        return sb.toString();
    }

    /**
     * test content
     * 1. no insert
     */
    @org.junit.jupiter.api.Test
    void addKey0() {
        makeBPT(5, 0);
        assertEquals("| |", bpt.printBasic());
    }

    /**
     * test content
     * 1. single level insert
     * 2. 1 level & one split
     */
    @org.junit.jupiter.api.Test
    void addKey1() {
        makeBPT(5, 3);
        assertEquals("| 0 1 2 |", bpt.printBasic());
        System.out.println();
        makeBPT(5, 5);
        assertEquals("| 2 |\n| 0 1 | 2 3 4 |", bpt.printBasic());
    }
    /**
     * test content
     * 1. 2 level bigger data input
     * 2. 2 level & multiple splits
     */
    @org.junit.jupiter.api.Test
    void addKey2() {
        makeBPT(5, 10);
        assertEquals("| 2 4 6 |\n| 0 1 | 2 3 | 4 5 | 6 7 8 9 |", bpt.printBasic());
        System.out.println();
        makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31));
        assertEquals("| 4 7 10 30 |\n| 1 2 | 4 6 | 7 9 | 10 21 22 | 30 31 35 45 |", bpt.printBasic());
    }

    /**
     * test content
     * 1. 3 level edge split
     * 2. 3 level inner split
     */
    @org.junit.jupiter.api.Test
    void addKey3() {
        makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36/*, 39, 49*/));
        assertEquals("| 10 |\n| 4 7 | 30 35 |\n| 1 2 | 4 6 | 7 9 | 10 21 22 | 30 31 | 35 36 45 |", bpt.printBasic());
        System.out.println();
        makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36, 13, 15));
        assertEquals("| 10 |\n| 4 7 | 15 30 35 |\n| 1 2 | 4 6 | 7 9 | 10 13 | 15 21 22 | 30 31 | 35 36 45 |", bpt.printBasic());
    }

    @org.junit.jupiter.api.Test
    void printData() {
        System.out.print("damn");
        makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36/*, 39, 49*/));
        System.out.print("damn");
        assertEquals("| 1 | 2 | 4 | 6 | 7 | 9 | 10 | 21 | 22 | 30 | 31 | 35 | 36 | 45 |", bpt.printData());

        makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36, 13, 15));
        assertEquals("| 1 | 2 | 4 | 6 | 7 | 9 | 10 | 13 | 15 | 21 | 22 | 30 | 31 | 35 | 36 | 45 |", bpt.printData());
    }

    @Test
    void search() {
        makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36, 13, 15));
        assertEquals("| 10 | 13 | 15 |", keyListString(bpt.search(10, 17)));
        assertEquals("| 9 | 10 | 13 | 15 |", keyListString(bpt.search(8, 15)));
    }

//    @org.junit.jupiter.api.Test
//    void search() {
//    }
//
//    @org.junit.jupiter.api.Test
//    void split() {
//    }
}