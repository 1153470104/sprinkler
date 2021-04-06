package metadataServer.rectangleTree;

import BPlusTree.externalTree;

import java.util.*;

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
        // 添加之后边界会改变，而父节点的边界如何改变将在外面的add function中去实现
        // 这里仅仅调用父节点的upgrade
        rectangleList.add(rectangle);
        if(this.selfRectangle.top == null) {  //即如果本来没有被发掘过
            this.selfRectangle.top = rectangle.top;
            this.selfRectangle.bottom = rectangle.bottom;
            this.selfRectangle.timeStart = rectangle.timeStart;
        }
        treeList.add(tree);
        // define the boundary's update rule
        updateAllBounds(); //use the update function in node implementation

//        if(this.selfRectangle.top.compareTo(rectangle.top) == 1)  this.selfRectangle.top = rectangle.top;
//        if(this.selfRectangle.bottom.compareTo(rectangle.bottom) == -1)  this.selfRectangle.bottom = rectangle.bottom;
//        if(this.selfRectangle.timeStart > rectangle.timeStart)  this.selfRectangle.timeStart = rectangle.timeStart;
//        if(this.selfRectangle.timeEnd == -1 || this.selfRectangle.timeEnd < rectangle.timeEnd)
//            this.selfRectangle.timeEnd = rectangle.timeEnd;

    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public List<externalTree> searchChunk(rectangle<K> rec) {
        List<externalTree> list = new LinkedList<>();
        for(int i = 0; i < rectangleList.size(); i++) {
            if(rectangleList.get(i).crossStatus(rec) == rectangle.ACROSS) {
                list.add(treeList.get(i));
            }
        }
        return list;
    }

    @Override
    public void split() {
        if(!overflow())  return;  //再检查一下
        RTreeNode<K> father;
        boolean newFather = false;
        if(this.fatherNode == null) {
//            System.out.println("null");
            father = new RTreeNode<K>(
                    this.m, selfRectangle.top, selfRectangle.bottom, selfRectangle.timeStart
                    , selfRectangle.timeEnd, null);
            father.initChild();
            newFather = true;
        } else {
//            System.out.println("not null");
            father = this.fatherNode;
        }
        //the next split function is based on the specific condition
        //which in this program is: the K's boundary will not change too much
        // but the time will continuously grow
        // so, we will split out the time later half value-pair
        // then form a new leaf nodes

        // TODO 未来，或许可以用堆排序来判断前多少个什么的，这里就直接遍历吧。。。
        // TODO 因为现在预计也就是16个max？比较8个而已。。。
        int len = rectangleList.size();
        List<Integer> splitNum = new LinkedList<>();
        for(int i = 0; i < len; i++) {  //通过这个for循环，得到一个reserveNum，就是哪些数值是
            boolean added = false;
            for(int j = 0; j < splitNum.size(); j++) {
                if(rectangleList.get(i).timeStart > rectangleList.get(splitNum.get(j)).timeStart) {
                    splitNum.add(j, i);
                    added = true;
                    break; /* 又忘记break，导致它永不停止*/
                }
            }
            /* 这玩意居然本来被我放进for循环里面了 */
            if(splitNum.size() < len/2 && !added) {
                splitNum.add(i);
            }
            if(splitNum.size() > len/2) {
                splitNum.remove(splitNum.size()-1);
            }
//            System.out.println(i);
        }
        // build the new node
        Set<Integer> splitSet = new HashSet<>(splitNum);
//        System.out.println(reserveSet);
        RTreeLeaf<K> newLeaf = new RTreeLeaf<>(m, null, null, -1, -1, father);
        for(int i = 0; i < rectangleList.size(); i++) {
            if(splitSet.contains(i)) {
                newLeaf.add(rectangleList.get(i), treeList.get(i));
            }
        }
        // another origin node should be cut
        Collections.sort(splitNum);
        for(int i = splitNum.size()-1; i >= 0; i--) {
            int removeIndex = splitNum.get(i);
            rectangleList.remove(removeIndex);
            treeList.remove(removeIndex);
        }
        this.fatherNode = father;
        // add to father

        this.updateAllBounds();
        if(newFather)  father.add(this);
        father.add(newLeaf);
        // update the bounds of this shrink node
    }

    @Override
    public externalTree getTree(int index) {
        return this.treeList.get(index);
    }
}
