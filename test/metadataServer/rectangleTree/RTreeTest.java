package metadataServer.rectangleTree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RTreeTest {
    RTree<Integer> rTree = new RTree<>(5);

    /**
     * test without split
     */
    @Test
    void add0() {
        rTree.add(5, 10, 10, 20, null);
        rTree.add(10, 20, 10, 20, null);
        rTree.add(20, 30, 10, 20, null);
        rTree.add(30, 40, 10, 20, null);
        rTree.toString();
    }

    @Test
    void size() {

    }

    @Test
    void searchTree() {

    }
}
