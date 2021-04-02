package metadataServer.rectangleTree;

import BPlusTree.externalTree;

import java.util.ArrayList;
import java.util.List;

/**
 * the non-leaf node of R tree
 * @param <K> the type of up-down coordinate data type
 */
public abstract class RTreeNode<K> {
    protected K top;
    protected K bottom;
    protected int timeStart;
    protected int timeEnd;
    protected List<RTreeNode<K>> childList;

    public RTreeNode() {
        this.childList = new ArrayList<>();
    }

    /**
     * the function to search the corresponding position child node
     * with the corner position as input
     * @param top the top boundary of search area
     * @param bottom the bottom boundary of search area
     * @param start the left boundary of search area aka. start time of this area
     * @param end the right boundary of search area. end time of this area
     * @return the spatial corresponding child node
     */
    public RTreeNode<K> searchNode(K top, K bottom, int start, int end) {
        // TODO
        return null;
    }

    public List<RTreeLeaf<K>> searchChunk(K top, K bottom, int start, int end) {
        // TODO
        return null;
    }

    public int getLength() {
        return childList.size();
    }

    public void add(RTreeNode<K> node) {
        this.childList.add(node);
    }

    public RTreeNode<K> split() {
        // TODO
        return null;
    }

    public abstract externalTree getTree(int index);
}
