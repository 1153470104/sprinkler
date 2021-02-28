package BPlusTree;

import BPlusTree.BPTKey.BPTValueKey;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BPlusTreeTemplatedTest {
    testTool ts = new testTool();
    BPlusTree<Integer> copyTree;

    /**
     * test content
     * 1. single level insert
     * 2. 1 level & one split
     */
    @org.junit.jupiter.api.Test
    void template1() {
        ts.makeBPT(5, 5);
        copyTree = new BPlusTreeTemplated<Integer>(ts.bpt());
//        assertEquals("| 2 |\n| 0 1 | 2 3 4 |", ts.bpt().printBasic());
        assertEquals("| 2 |\n| | |", copyTree.printBasic());
    }

    /**
     * test content
     * 1. 2 level bigger data input
     * 2. 2 level & multiple splits
     */
    @org.junit.jupiter.api.Test
    void template2() {
        ts.makeBPT(5, 10);
        copyTree = new BPlusTreeTemplated<Integer>(ts.bpt());
//        assertEquals("| 2 4 6 |\n| 0 1 | 2 3 | 4 5 | 6 7 8 9 |", ts.bpt().printBasic());
        assertEquals("| 2 4 6 |\n| | | | |", copyTree.printBasic());
        System.out.println();
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31));
        copyTree = new BPlusTreeTemplated<Integer>(ts.bpt());
//        assertEquals("| 4 7 10 30 |\n| 1 2 | 4 6 | 7 9 | 10 21 22 | 30 31 35 45 |", ts.bpt().printBasic());
        assertEquals("| 4 7 10 30 |\n| | | | | |", copyTree.printBasic());
    }

    /**
     * test content
     * 1. 3 level edge split
     * 2. 3 level inner split
     */
    @org.junit.jupiter.api.Test
    void template3() {
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36/*, 39, 49*/));
        copyTree = new BPlusTreeTemplated<Integer>(ts.bpt());
//        assertEquals("| 10 |\n| 4 7 | 30 35 |\n| 1 2 | 4 6 | 7 9 | 10 21 22 | 30 31 | 35 36 45 |", ts.bpt().printBasic());
        assertEquals("| 10 |\n| 4 7 | 30 35 |\n| | | | | | |", copyTree.printBasic());
        System.out.println();
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36, 13, 15));
        copyTree = new BPlusTreeTemplated<Integer>(ts.bpt());
//        assertEquals("| 10 |\n| 4 7 | 15 30 35 |\n| 1 2 | 4 6 | 7 9 | 10 13 | 15 21 22 | 30 31 | 35 36 45 |", ts.bpt().printBasic());
        assertEquals("| 10 |\n| 4 7 | 15 30 35 |\n| | | | | | | |", copyTree.printBasic());
    }

    /**
     * layer 2 & 3
     * without split
     */
    @org.junit.jupiter.api.Test
    void addkey1() {
        ts.makeBPT(5, 10);
        copyTree = new BPlusTreeTemplated<Integer>(ts.bpt());
        //use printData to test the
        assertEquals("| |", copyTree.printData());
        copyTree.addKey(new BPTValueKey<Integer, String>(5, Integer.toString(5)));
        assertEquals("| 2 4 6 |\n| | | 5 | |", copyTree.printBasic());
        assertEquals("| 5 |", copyTree.printData());
        System.out.println();
        copyTree.addKey(new BPTValueKey<Integer, String>(0, Integer.toString(0)));
        copyTree.addKey(new BPTValueKey<Integer, String>(10, Integer.toString(10)));
        copyTree.addKey(new BPTValueKey<Integer, String>(11, Integer.toString(11)));
//        assertEquals("| 2 4 6 |\n| 0 1 | 2 3 | 4 5 | 6 7 8 9 |", ts.bpt().printBasic());
        assertEquals("| 2 4 6 |\n| 0 | | 5 | 10 11 |", copyTree.printBasic());
        assertEquals("| 0 | 5 | 10 | 11 |", copyTree.printData());

        // assigned number
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36, 13, 15));
        copyTree = new BPlusTreeTemplated<Integer>(ts.bpt());
        copyTree.addKey(new BPTValueKey<Integer, String>(10, Integer.toString(10)));
        copyTree.addKey(new BPTValueKey<Integer, String>(1, Integer.toString(1)));
        copyTree.addKey(new BPTValueKey<Integer, String>(27, Integer.toString(27)));
        copyTree.addKey(new BPTValueKey<Integer, String>(8, Integer.toString(8)));
        copyTree.addKey(new BPTValueKey<Integer, String>(7, Integer.toString(7)));
        assertEquals("| 10 |\n| 4 7 | 15 30 35 |\n| 1 | | 7 8 | 10 | 27 | | |", copyTree.printBasic());
        copyTree.addKey(ts.IntegerKey(14));
        copyTree.addKey(ts.IntegerKey(16));
        copyTree.addKey(ts.IntegerKey(18));
        copyTree.addKey(ts.IntegerKey(13));
        assertEquals("| 10 |\n| 4 7 | 15 30 35 |\n| 1 | | 7 8 | 10 13 14 | 16 18 27 | | |"
                , copyTree.printBasic());
    }

    /**
     * layer 2 & 3
     * with split without balance process
     */
    @org.junit.jupiter.api.Test
    void addkey2() {
        ts.makeBPT(5, 10);
        copyTree = new BPlusTreeTemplated<Integer>(ts.bpt());
        //use printData to test the
        assertEquals("| |", copyTree.printData());
        copyTree.addKey(new BPTValueKey<Integer, String>(5, Integer.toString(5)));
        assertEquals("| 2 4 6 |\n| | | 5 | |", copyTree.printBasic());
        assertEquals("| 5 |", copyTree.printData());
        System.out.println();
        copyTree.addKey(new BPTValueKey<Integer, String>(0, Integer.toString(0)));
        copyTree.addKey(new BPTValueKey<Integer, String>(10, Integer.toString(10)));
        copyTree.addKey(new BPTValueKey<Integer, String>(11, Integer.toString(11)));
        assertEquals("| 2 4 6 |\n| 0 | | 5 | 10 11 |", copyTree.printBasic());
        copyTree.addKey(new BPTValueKey<Integer, String>(15, Integer.toString(15)));
        copyTree.addKey(new BPTValueKey<Integer, String>(12, Integer.toString(12)));
        assertEquals("| 2 4 6 |\n| 0 | | 5 | 10 11 12 15 |", copyTree.printBasic());
        copyTree.addKey(new BPTValueKey<Integer, String>(13, Integer.toString(13)));
        assertEquals("| 2 4 6 12 |\n| 0 | | 5 | 10 11 | 12 13 15 |", copyTree.printBasic());
        copyTree.addKey(ts.IntegerKey(14));
        copyTree.addKey(ts.IntegerKey(14));
        assertEquals("| 2 4 6 12 |\n| 0 | | 5 | 10 11 | 12 13 14 14 15 |", copyTree.printBasic());


        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36, 13, 15));
        copyTree = new BPlusTreeTemplated<Integer>(ts.bpt());
        copyTree.addKey(new BPTValueKey<Integer, String>(10, Integer.toString(10)));
        copyTree.addKey(new BPTValueKey<Integer, String>(1, Integer.toString(1)));
        copyTree.addKey(new BPTValueKey<Integer, String>(27, Integer.toString(27)));
        copyTree.addKey(new BPTValueKey<Integer, String>(8, Integer.toString(8)));
        copyTree.addKey(new BPTValueKey<Integer, String>(7, Integer.toString(7)));
        copyTree.addKey(ts.IntegerKey(14)); copyTree.addKey(ts.IntegerKey(16));
        copyTree.addKey(ts.IntegerKey(18)); copyTree.addKey(ts.IntegerKey(13));

        copyTree.addKey(ts.IntegerKey(11)); copyTree.addKey(ts.IntegerKey(12));
        assertEquals("| 10 |\n| 4 7 | 12 15 30 35 |\n| 1 | | 7 8 | 10 11 | 12 13 14 | 16 18 27 | | |"
                , copyTree.printBasic());
    }

    /**
     * balance function的问题
     * 暂时只测试二层、三层的树
     */
    @org.junit.jupiter.api.Test
    void balance() {
        //************************* 2-layer test *****************************//
        ts.makeBPT(5, 10);
        copyTree = new BPlusTreeTemplated<Integer>(ts.bpt());
        copyTree.addKey(new BPTValueKey<Integer, String>(5, Integer.toString(5)));
        copyTree.addKey(new BPTValueKey<Integer, String>(3, Integer.toString(5)));
        copyTree.addKey(new BPTValueKey<Integer, String>(0, Integer.toString(0)));
        copyTree.addKey(new BPTValueKey<Integer, String>(10, Integer.toString(10)));
        assertEquals("| 2 4 6 |\n| 0 | 3 | 5 | 10 |", copyTree.printBasic());
        System.out.println();
        ((BPlusTreeTemplated)copyTree).balance();
        assertEquals("| 2 4 6 |\n| 0 | 3 | 5 | 10 |", copyTree.printBasic());
        System.out.println();

        copyTree.addKey(new BPTValueKey<Integer, String>(11, Integer.toString(11)));
        copyTree.addKey(new BPTValueKey<Integer, String>(15, Integer.toString(15)));
        copyTree.addKey(new BPTValueKey<Integer, String>(12, Integer.toString(12)));
        assertEquals("| 2 4 6 |\n| 0 | 3 | 5 | 10 11 12 15 |", copyTree.printBasic());
        System.out.println();
        ((BPlusTreeTemplated)copyTree).balance();
        assertEquals("| 5 11 15 |\n| 0 3 | 5 10 | 11 12 | 15 |", copyTree.printBasic());


        //************************* 3-layer test *****************************//
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36, 13, 15));
        copyTree = new BPlusTreeTemplated<Integer>(ts.bpt());
        copyTree.addKey(new BPTValueKey<Integer, String>(10, Integer.toString(10)));
        copyTree.addKey(new BPTValueKey<Integer, String>(1, Integer.toString(1)));
        copyTree.addKey(new BPTValueKey<Integer, String>(27, Integer.toString(27)));
        copyTree.addKey(new BPTValueKey<Integer, String>(8, Integer.toString(8)));
        copyTree.addKey(new BPTValueKey<Integer, String>(7, Integer.toString(7)));
        copyTree.addKey(ts.IntegerKey(14)); copyTree.addKey(ts.IntegerKey(16));
        copyTree.addKey(ts.IntegerKey(18)); copyTree.addKey(ts.IntegerKey(13));
        copyTree.addKey(ts.IntegerKey(11)); copyTree.addKey(ts.IntegerKey(12));
        assertEquals("| 10 |\n| 4 7 | 12 15 30 35 |\n| 1 | | 7 8 | 10 11 | 12 13 14 | 16 18 27 | | |"
                , copyTree.printBasic());
        System.out.println();
        ((BPlusTreeTemplated)copyTree).balance();
        assertEquals("| 11 |\n| 7 10 | 13 14 18 27 |\n| 1 | 7 8 | 10 | 11 12 | 13 | 14 16 | 18 | 27 |"
                , copyTree.printBasic());

        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 15, 1, 12, 4, 6, 9, 40, 22, 28, 36, 13, 50, 18, 19, 2, 5, 41, 42));
        copyTree = new BPlusTreeTemplated<Integer>(ts.bpt());
        copyTree.addKey(ts.IntegerKey(1)); copyTree.addKey(ts.IntegerKey(2));
        copyTree.addKey(ts.IntegerKey(13)); copyTree.addKey(ts.IntegerKey(19));
        copyTree.addKey(ts.IntegerKey(24)); copyTree.addKey(ts.IntegerKey(26));
        copyTree.addKey(ts.IntegerKey(30)); copyTree.addKey(ts.IntegerKey(37));
        copyTree.addKey(ts.IntegerKey(45)); copyTree.addKey(ts.IntegerKey(46));
        copyTree.addKey(ts.IntegerKey(51)); copyTree.addKey(ts.IntegerKey(56));
        copyTree.addKey(ts.IntegerKey(69)); copyTree.addKey(ts.IntegerKey(90));
        assertEquals("| 21 |\n| 4 7 12 15 | 30 36 41 51 |\n" +
                "| 1 2 | | | 13 | 19 | 24 26 | 30 | 37 | 45 46 | 51 56 69 90 |", copyTree.printBasic());
        System.out.println();
        copyTree.addKey(ts.IntegerKey(91)); copyTree.addKey(ts.IntegerKey(99));
        assertEquals("| 21 |\n| 4 7 12 15 | 30 36 41 51 |\n" +
                "| 1 2 | | | 13 | 19 | 24 26 | 30 | 37 | 45 46 | 51 56 69 90 91 99 |", copyTree.printBasic());
        System.out.println();
        ((BPlusTreeTemplated)copyTree).balance();
        assertEquals("| 51 |\n| 13 24 30 45 | 69 90 91 99 |\n" +
                "| 1 2 | 13 19 | 24 26 | 30 37 | 45 46 | 51 56 | 69 | 90 | 91 | 99 |", copyTree.printBasic());
        assertEquals("| 1 | 2 | 13 | 19 | 24 | 26 | 30 | 37 | 45 | 46 | 51 | 56 | 69 | 90 | 91 | 99 |", copyTree.printData());
    }

    @Test
    void search() {
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36, 13, 15));
//        copyTree = new BPlusTreeTemplated<Integer>(ts.bpt());
//        assertEquals("| 10 | 13 | 15 |", ts.keyListString(ts.bpt().search(10, 17)));
//        assertEquals("| 9 | 10 | 13 | 15 |", ts.keyListString(ts.bpt().search(8, 15)));
    }

}



