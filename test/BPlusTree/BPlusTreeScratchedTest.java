package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BPlusTreeScratchedTest {
    testTool ts = new testTool();

    /**
     * test content
     * 1. no insert
     */
    @org.junit.jupiter.api.Test
    void addKey0() {
        ts.makeBPT(5, 0);
        assertEquals("| |", ts.bpt().printBasic());
    }

    /**
     * test content
     * 1. single level insert
     * 2. 1 level & one split
     */
    @org.junit.jupiter.api.Test
    void addKey1() {
        ts.makeBPT(5, 3);
        assertEquals("| 0 1 2 |", ts.bpt().printBasic());
        System.out.println();
        ts.makeBPT(5, 5);
        assertEquals("| 2 |\n| 0 1 | 2 3 4 |", ts.bpt().printBasic());
    }
    /**
     * test content
     * 1. 2 level bigger data input
     * 2. 2 level & multiple splits
     */
    @org.junit.jupiter.api.Test
    void addKey2() {
        ts.makeBPT(5, 10);
        assertEquals("| 2 4 6 |\n| 0 1 | 2 3 | 4 5 | 6 7 8 9 |", ts.bpt().printBasic());
        System.out.println();
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31));
        assertEquals("| 4 7 10 30 |\n| 1 2 | 4 6 | 7 9 | 10 21 22 | 30 31 35 45 |", ts.bpt().printBasic());
    }

    /**
     * test content
     * 1. 3 level edge split
     * 2. 3 level inner split
     */
    @org.junit.jupiter.api.Test
    void addKey3() {
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36/*, 39, 49*/));
        assertEquals("| 10 |\n| 4 7 | 30 35 |\n| 1 2 | 4 6 | 7 9 | 10 21 22 | 30 31 | 35 36 45 |", ts.bpt().printBasic());
        System.out.println();
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36, 13, 15));
        assertEquals("| 10 |\n| 4 7 | 15 30 35 |\n| 1 2 | 4 6 | 7 9 | 10 13 | 15 21 22 | 30 31 | 35 36 45 |", ts.bpt().printBasic());

        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 15, 1, 12, 4, 6, 9, 40, 22, 28, 36, 13, 50, 18, 19, 2, 5, 41, 42));
        assertEquals("| 21 |\n| 4 7 12 15 | 30 36 41 |\n" +
                "| 1 2 | 4 5 6 | 7 9 | 12 13 | 15 18 19 | 21 22 28 | 30 35 | 36 40 | 41 42 50 |", ts.bpt().printBasic());
    }

    @org.junit.jupiter.api.Test
    void printData() {
        System.out.print("damn");
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36/*, 39, 49*/));
        System.out.print("damn");
        assertEquals("| 1 | 2 | 4 | 6 | 7 | 9 | 10 | 21 | 22 | 30 | 31 | 35 | 36 | 45 |", ts.bpt().printData());

        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36, 13, 15));
        assertEquals("| 1 | 2 | 4 | 6 | 7 | 9 | 10 | 13 | 15 | 21 | 22 | 30 | 31 | 35 | 36 | 45 |", ts.bpt().printData());
    }

    @Test
    void search() {
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31, 36, 13, 15));
        assertEquals("| 10 | 13 | 15 |", ts.keyListString(ts.bpt().search(10, 17)));
        assertEquals("| 9 | 10 | 13 | 15 |", ts.keyListString(ts.bpt().search(8, 15)));
    }

//    @org.junit.jupiter.api.Test
//    void search() {
//    }
//
//    @org.junit.jupiter.api.Test
//    void split() {
//    }
}
