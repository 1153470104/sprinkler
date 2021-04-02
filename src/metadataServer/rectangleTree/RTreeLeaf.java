package metadataServer.rectangleTree;

import BPlusTree.externalTree;

import java.util.List;

/**
 * the leaf node of R tree
 * @param <K> the type of up-down coordinate data type
 */
public class RTreeLeaf<K> extends RTreeNode<K>{
    private List<externalTree> treeList;

    public RTreeLeaf(K top, K bottom, int left, int right) {
        this.top = top;
        this.bottom = bottom;
        this.timeStart = left;
        this.timeEnd = right;
        this.childList = null;
    }

    @Override
    public List<RTreeLeaf<K>> searchChunk(K top, K bottom, int left, int right) {
        return super.searchChunk(top, bottom, left, right);
    }

    @Override
    public RTreeNode<K> split() {
        // TODO
        return null;
    }

    @Override
    public externalTree getTree(int index) {
        return this.treeList.get(index);
    }
}
