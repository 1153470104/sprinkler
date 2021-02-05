package BPlusTree;

import BPlusTree.BPTKey.BPTValueKey;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BPlusTreeTemplatedTest {
    testTool ts = new testTool();
    BPlusTree<Integer> copyTree;


    /* the tests below is all for the root copy */


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
        copyTree.addKey(new BPTValueKey<Integer, String>(5, Integer.toString(5)));
//        assertEquals("| 2 4 6 |\n| 0 1 | 2 3 | 4 5 | 6 7 8 9 |", ts.bpt().printBasic());
        assertEquals("| 2 4 6 |\n| | | | |", copyTree.printBasic());
    }

    /**
     * layer 2 & 3
     * with split without balance process
     */
    @org.junit.jupiter.api.Test
    void addkey2() {
    }

    /**
     * layer 2 & 3
     * with split with balance process
     * & without split but with balance process
     */
    @org.junit.jupiter.api.Test
    void addkey3() {
    }

    @Test
    void search() {
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36, 13, 15));
//        copyTree = new BPlusTreeTemplated<Integer>(ts.bpt());
//        assertEquals("| 10 | 13 | 15 |", ts.keyListString(ts.bpt().search(10, 17)));
//        assertEquals("| 9 | 10 | 13 | 15 |", ts.keyListString(ts.bpt().search(8, 15)));
    }

}
