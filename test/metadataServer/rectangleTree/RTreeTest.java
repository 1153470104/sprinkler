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


    /**
     * test with split
     */
    @Test
    void add1() {
        rTree.add(1, 2, 1, 3, null);
        rTree.add(2, 5, 1, 3, null);
        rTree.add(4, 7, 1, 3, null);
        rTree.add(6, 9, 2, 3, null);
        rTree.add(5, 8, 3, 4, null);
        rTree.add(5, 8, 2, 4, null);
        rTree.toString();
        System.out.println();

        rTree.add(4, 7, 1, 3, null);
        rTree.add(6, 9, 2, 3, null);
        rTree.toString();
        System.out.println();

        rTree.add(5, 8, 3, 4, null);
        rTree.toString();
    }

    @Test
    void searchTree() {

    }
}
