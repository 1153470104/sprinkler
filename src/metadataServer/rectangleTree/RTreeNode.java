package metadataServer.rectangleTree;

import BPlusTree.externalTree;
import BPlusTree.keyType.MortonCode;

import java.util.ArrayList;
import java.util.List;

/**
 * the non-leaf node of R tree
 * @param <K> the type of up-down coordinate data type
 */
public abstract class RTreeNode<K> {
    protected int m;
    protected RTreeNode<K> fatherNode;
    protected rectangle<K> rectangle;
    protected List<rectangle<K>> rectangleList;
    private List<RTreeNode<K>> childList;

    public RTreeNode(int m, K top, K bottom, int start, int end, RTreeNode fatherNode) {
        this.m = m;
        this.fatherNode = fatherNode;
        this.rectangle = new rectangle<>(top, bottom, start, end);
        this.rectangleList = new ArrayList<>();
    }

    public boolean overflow() {
        return rectangleList.size() >= m;
    }

    public void setFatherNode(RTreeNode<K> fatherNode) {
        this.fatherNode = fatherNode;
    }

    public void initChild() {
        this.childList = new ArrayList<>();
    }

    public boolean isLeaf() {
        return false;
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

    public void split() {
        // TODO
    }

    public abstract externalTree getTree(int index);

    public abstract void add(metadataServer.rectangleTree.rectangle<K> rectangle, externalTree tree);
}
