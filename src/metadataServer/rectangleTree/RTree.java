package metadataServer.rectangleTree;

import BPlusTree.externalTree;

import java.util.LinkedList;
import java.util.List;

/**
 * the rectangle tree realization
 * @param <K> the type of one of the axis, the other's type is int
 */
public class RTree<K extends Comparable> {
    private int m;
    private RTreeNode<K> root;
    private int num;

    public RTree(int m) {
        this.num = 0;
        this.m = m;
        this.root = new RTreeLeaf<K>(m, null, null, -1, -1, null);
    }

    /**
     *
     * add rectanlge-key value pair into the R-tree
     *
     * @param top the small key boundary of value chunk
     * @param bottom the bigger key boundary of value chunk
     * @param timeStart the start time of the value chunk
     * @param timeEnd the end time of the value chunk
     * @param tree the data chunk need to be insert
     */
    public void add(K top, K bottom, int timeStart, int timeEnd, externalTree tree) {
        RTreeNode<K> temp = root;
        while(!temp.isLeaf()) {
            temp = temp.searchNode(top, bottom, timeStart, timeEnd);
        }
//        System.out.println(top + "; " + bottom);
        temp.add(new rectangle<K>(top, bottom, timeStart, timeEnd), tree);
        while(temp.overflow()) {
            temp.split();
            temp = temp.fatherNode;
//            System.out.println("temp: " + temp);
        }
        //next is to make sure the root is the newest father node
        while(temp.fatherNode != null) {
            temp = temp.fatherNode;
        }
        root = temp;
        num++;
    }

    /**
     * return the number of total entries' number
     * @return the number of entries
     */
    public int size() {
        return this.num;
    }

    /**
     * search the external chunk by the 
     */
    public List<externalTree> searchTree(K top, K bottom, int left, int right) {
        return root.searchChunk(new rectangle<>(top, bottom, left, right));
    }

    public String toString() {
        return root.toString();
    }
}
