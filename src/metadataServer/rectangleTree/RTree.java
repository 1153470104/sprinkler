package metadataServer.rectangleTree;

import BPlusTree.externalTree;

import java.util.List;

public class RTree<K, V> {
    private int m;
    private RTreeNode<K, V> root;

    public RTree(int m) {
        this.m = m;
    }

    public void add(RTreeLeaf<K, V> leaf) {
        // todo
    }

    public void split() {
        // todo
    }

    public List<externalTree> searchTree(K top, K bottom, V left, V right) {
        // todo
        return null;
    }
}
