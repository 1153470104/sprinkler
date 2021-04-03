package metadataServer.rectangleTree;

import BPlusTree.externalTree;

import java.util.List;

public class RTree<K> {
    private int m;
    private RTreeNode<K> root;
    private int num;

    public RTree(int m) {
        this.num = 0;
        this.m = m;
        this.root = new RTreeLeaf<K>(m, null, null, -1, -1, null);
    }

    public void add(K top, K bottom, int timeStart, int timeEnd, externalTree tree) {
        // todo
        RTreeNode<K> temp = root;
        while(!temp.isLeaf()) {
            temp = temp.searchNode(top, bottom, timeStart, timeEnd);
        }
        temp.add(new rectangle<K>(top, bottom, timeStart, timeEnd), tree);
        while(temp.overflow()) {
            temp.split();
            temp = temp.fatherNode;
        }

        num++;
    }

    /**
     * return the number of total entries' number
     * @return the number of entries
     */
    public int size() {
        return this.num;
    }
//    public void split() {
//        // todo
//    }

    public List<externalTree> searchTree(K top, K bottom, int left, int right) {
        // todo
        return null;
    }
}
