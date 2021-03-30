package metadataServer.rectangleTree;

import BPlusTree.externalTree;

import java.util.ArrayList;
import java.util.List;

/**
 * the non-leaf node of R tree
 * @param <K> the type of up-down coordinate data type
 * @param <V> the type of horizontal coordinate data type
 */
public abstract class RTreeNode<K, V> {
    protected K top;
    protected K bottom;
    protected V left;
    protected V right;
    protected List<RTreeNode<K, V>> childList;

    public RTreeNode() {
        this.childList = new ArrayList<>();
    }

    /**
     * the function to search the corresponding position child node
     * with the corner position as input
     * @param top the top boundary of search area
     * @param bottom the bottom boundary of search area
     * @param left the left boundary of search area
     * @param right the right boundary of search area
     * @return the spatial corresponding child node
     */
    public RTreeNode<K, V> searchNode(K top, K bottom, V left, V right) {
        // TODO
        return null;
    }

    public void add(RTreeNode<K, V> node) {
        this.childList.add(node);
    }

    public abstract externalTree getExternalTree();
}
