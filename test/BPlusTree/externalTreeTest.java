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
    void init() throws IOException {
        ts.makeIntBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31));
//        assertEquals("| 4 7 10 30 |\n| 1 2 | 4 6 | 7 9 | 10 21 22 | 30 31 35 45 |", ts.bpt().printBasic());
        externalTree<Integer> extree = new externalTree<Integer>(ts.bpt(), "resource/database/f0", IIconf);
    }

    @Test
    void readNode() throws IOException{
//        ts.makeIntBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31));
        ts.makeBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31));
//        assertEquals("| 4 7 10 30 |\n| 1 2 | 4 6 | 7 9 | 10 21 22 | 30 31 35 45 |", ts.bpt().printBasic());
        externalTree<Integer> extree = new externalTree<Integer>(ts.bpt(), "resource/database/f0", ISconf);
        assertEquals("0,4,1024,4|7|10|30", extree.readNode(1 * ISconf.pageSize).toString());
        assertEquals("1,2,2048,1:  1|2:  2", extree.readNode(2 * ISconf.pageSize).toString());
    }

    @Test
    void searchNode() {
    }
}
