package metadataServer.rectangleTree;

import BPlusTree.externalTree;

import java.util.List;

public class RTree<K> {
    private int m;
    private RTreeNode<K> root;

    public RTree(int m) {
        this.m = m;
    }

    public void add(RTreeLeaf<K> leaf) {
        // todo
    }

    public void split() {
        // todo
    }

    public List<externalTree> searchTree(K top, K bottom, int left, int right) {
        // todo
        return null;
    }
}
