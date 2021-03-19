package BPlusTree;

import BPlusTree.configuration.externalConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test of external tree
 */
class externalTreeTest {
    testTool ts = new testTool();
    externalConfiguration IIconf = new externalConfiguration(4, 4, Integer.class, Integer.class);
    externalConfiguration ISconf = new externalConfiguration(4, 3, Integer.class, String.class);

    @Test
    void init0() throws IOException {
        ts.makeIntBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31));
//        assertEquals("| 4 7 10 30 |\n| 1 2 | 4 6 | 7 9 | 10 21 22 | 30 31 35 45 |", ts.bpt().printBasic());
        externalTree<Integer> extree = new externalTree<Integer>(ts.bpt(), "resource/database/f0", IIconf);
    }

    /**
     * 2 level tree's test
     * @throws IOException throws when any I/O operation fails
     */
    @Test
    void readNode0() throws IOException{
//        ts.makeIntBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31));
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31));
//        assertEquals("| 4 7 10 30 |\n| 1 2 | 4 6 | 7 9 | 10 21 22 | 30 31 35 45 |", ts.bpt().printBasic());
        externalTree<Integer> extree = new externalTree<Integer>(ts.bpt(), "resource/database/f0", ISconf);
        assertEquals("0,4,1024,4|7|10|30", extree.readNode(1 * ISconf.pageSize).toString());
        assertEquals("1,2,2048,1:  1|2:  2", extree.readNode(2 * ISconf.pageSize).toString());
        assertEquals("1,4,6144,30: 30|31: 31|35: 35|45: 45", extree.readNode(6 * ISconf.pageSize).toString());
    }

    /**
     * test 2 level tree
     * @throws IOException throws when any I/O operation fails
     */
    @Test
    void domainSearch0() throws IOException {
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31));
        externalTree<Integer> extree = new externalTree<Integer>(ts.bpt(), "resource/database/f0", ISconf);
        assertEquals("4:  4|6:  6|7:  7", extree.valueListPrint(extree.searchNode(4, 8)));
        assertEquals("1:  1|2:  2|4:  4|6:  6|7:  7", extree.valueListPrint(extree.searchNode(-1, 8)));
        assertEquals("21: 21|22: 22|30: 30|31: 31", extree.valueListPrint(extree.searchNode(21, 31)));

    }

}
