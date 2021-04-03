package metadataServer.rectangleTree;

import BPlusTree.externalTree;

import java.util.LinkedList;
import java.util.List;

/**
 * the leaf node of R tree
 * @param <K> the type of up-down coordinate data type
 */
public class RTreeLeaf<K extends Comparable> extends RTreeNode<K>{
    private List<externalTree> treeList;

    public RTreeLeaf(int m, K top, K bottom, int left, int right, RTreeNode<K> father) {
        super(m, top, bottom, left, right, father);
        treeList = new LinkedList<>();
    }

    public void add(rectangle<K> rectangle, externalTree tree) {
        rectangleList.add(rectangle);
        if(this.selfRectangle.top == null) {  //即如果本来没有被发掘过
            this.selfRectangle.top = rectangle.top;
            this.selfRectangle.bottom = rectangle.bottom;
            this.selfRectangle.timeStart = rectangle.timeStart;
        }
        // define the boundary's update rule
        if(this.selfRectangle.top.compareTo(rectangle.top) == 1)  this.selfRectangle.top = rectangle.top;
        if(this.selfRectangle.bottom.compareTo(rectangle.bottom) == -1)  this.selfRectangle.bottom = rectangle.bottom;
        if(this.selfRectangle.timeStart > rectangle.timeStart)  this.selfRectangle.timeStart = rectangle.timeStart;
        if(this.selfRectangle.timeEnd == -1 || this.selfRectangle.timeEnd < rectangle.timeEnd)
            this.selfRectangle.timeEnd = rectangle.timeEnd;

        treeList.add(tree);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public List<externalTree> searchChunk(rectangle<K> rec) {
        List<externalTree> list = new LinkedList<>();
        for(int i = 0; i < rectangleList.size(); i++) {
            if(rectangleList.get(i).accross(rec)) {
                list.add(treeList.get(i));
            }
        }
        return list;
    }

    @Override
    public void split() {
        if(!overflow())  return;  //再检查一下
        RTreeNode<K> father = null;
        if(this.fatherNode == null) {
            father = new RTreeNode<K>( this.m, selfRectangle.top, selfRectangle.bottom, selfRectangle.timeStart, selfRectangle.timeEnd, null);
        } else {
            father = this.fatherNode;
        }
        //TODO ...

    }

    @Override
    public externalTree getTree(int index) {
        return this.treeList.get(index);
    }
}
