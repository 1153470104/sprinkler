package metadataServer.rectangleTree;

import BPlusTree.externalTree;

/**
 * the leaf node of R tree
 * @param <K> the type of up-down coordinate data type
 * @param <V> the type of horizontal coordinate data type
 */
public class RTreeLeaf<K, V> extends RTreeNode<K, V>{
    private externalTree tree;

    public RTreeLeaf(K top, K bottom, V left, V right, externalTree tree) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.childList = null;
        this.tree = tree;
    }

    @Override
    public externalTree getExternalTree() {
        return this.tree;
    }
}
