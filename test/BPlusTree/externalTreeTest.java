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
    externalConfiguration conf = new externalConfiguration(4, 4, Integer.class, Integer.class);

    @Test
    void init() throws IOException {
        ts.makeIntBPT(5, Arrays.asList(30, 7, 21, 35, 45, 1, 2, 4, 6, 9, 10, 22, 31));
        assertEquals("| 4 7 10 30 |\n| 1 2 | 4 6 | 7 9 | 10 21 22 | 30 31 35 45 |", ts.bpt().printBasic());
        externalTree<Integer, Integer> extree = new externalTree<Integer, Integer>(ts.bpt(), "resource/database/f0", conf);
    }

    @Test
    void searchNode() {
    }
}
