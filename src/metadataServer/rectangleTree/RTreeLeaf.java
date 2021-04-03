package metadataServer.rectangleTree;

import BPlusTree.externalTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * the leaf node of R tree
 * @param <K> the type of up-down coordinate data type
 */
public class RTreeLeaf<K> extends RTreeNode<K>{
    private List<externalTree> treeList;

    public RTreeLeaf(int m, K top, K bottom, int left, int right, RTreeNode<K> father) {
        super(m, top, bottom, left, right, father);
        treeList = new LinkedList<>();
    }

    public void add(rectangle<K> rectangle, externalTree tree) {
        rectangleList.add(rectangle);
        treeList.add(tree);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public List<RTreeLeaf<K>> searchChunk(K top, K bottom, int left, int right) {
        return super.searchChunk(top, bottom, left, right);
    }

    @Override
    public void split() {
        // TODO
    }

    @Override
    public externalTree getTree(int index) {
        return this.treeList.get(index);
    }
}
