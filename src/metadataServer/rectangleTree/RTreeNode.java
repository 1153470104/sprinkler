package metadataServer.rectangleTree;

import BPlusTree.externalTree;

import java.util.*;

/**
 * the non-leaf node of R tree
 * @param <K> the type of up-down coordinate data type
 */
public class RTreeNode<K extends Comparable> {
    protected int m;
    protected RTreeNode<K> fatherNode;
    protected rectangle<K> selfRectangle;
    protected List<rectangle<K>> rectangleList;
    private List<RTreeNode<K>> childList;

    public RTreeNode(int m, K top, K bottom, int start, int end, RTreeNode fatherNode) {
        this.m = m;
        this.fatherNode = fatherNode;
        this.selfRectangle = new rectangle<>(top, bottom, start, end);
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

    public void updateBounds(rectangle<K> newRectangle) {
        if(this.selfRectangle.top.compareTo(newRectangle.top) == 1)  this.selfRectangle.top = newRectangle.top;
        if(this.selfRectangle.bottom.compareTo(newRectangle.bottom) == -1)  this.selfRectangle.bottom = newRectangle.bottom;
        if(this.selfRectangle.timeStart > newRectangle.timeStart)  this.selfRectangle.timeStart = newRectangle.timeStart;
        if(this.selfRectangle.timeEnd == -1 || this.selfRectangle.timeEnd < newRectangle.timeEnd)
            this.selfRectangle.timeEnd = newRectangle.timeEnd;

        if (fatherNode!=null)  fatherNode.updateBounds(this.selfRectangle);
    }

    public void updateAllBounds() {
        selfRectangle = rectangleList.get(0).copy();
        for(rectangle<K> rec: rectangleList) {
            updateBounds(rec);
        }
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
        rectangle<K> inputRec = new rectangle<>(top, bottom, start, end);
        int[] interStatus = new int[rectangleList.size()];
        for(int i = 0; i < rectangleList.size(); i++) {
            int status = rectangleList.get(i).crossStatus(inputRec);
            if(status == 1) {  // if there's a node which covers it, return
                return childList.get(i);
            }
            interStatus[i] = status;
        }
        // below choose the latest across or be-covered node to return
        int laterNodeIndex = -1;
        int left = 0;
        for(int i = 0; i < rectangleList.size(); i++) {
            if((interStatus[i] == 2 || interStatus[i] == -1) && rectangleList.get(i).timeStart > left) {
                laterNodeIndex = i;
                left = rectangleList.get(i).timeStart;
            }
        }
        if(laterNodeIndex != -1)  return childList.get(laterNodeIndex);
        // if no child node has any intersection with the input rectangle
        // return the most left one
        for(int i = 0; i < rectangleList.size(); i++) {
            if(rectangleList.get(i).timeEnd == -1) {
                return childList.get(i);
            }
        }
        return null; // it is supposed never to be activated
    }

    public List<externalTree> searchChunk(rectangle<K> rec) {
        List<externalTree> leafList = new LinkedList<>();
        for(RTreeNode<K> node: childList) {
            leafList.addAll(node.searchChunk(rec));
        }
        return leafList;
    }

    public int getLength() {
        return childList.size();
    }

    public void add(RTreeNode<K> node) {
        this.rectangleList.add(node.selfRectangle);
        this.childList.add(node);
        updateAllBounds();
    }

    public void split() {
        if(!overflow())  return;  //再检查一下
        RTreeNode<K> father = null;
        if(this.fatherNode == null) {
            father = new RTreeNode<K>(
                    this.m, selfRectangle.top, selfRectangle.bottom, selfRectangle.timeStart
                    , selfRectangle.timeEnd, null);
        } else {
            father = this.fatherNode;
        }

        //the next split function is based on the specific condition
        //which in this program is: the K's boundary will not change too much
        // but the time will continuously grow
        // so, we will split out the time later half value-pair
        // then form a new leaf nodes

        // TODO 未来，或许可以用堆排序来判断前多少个什么的，这里就直接遍历吧。。。
        // TODO 因为现在预计也就是16个max？比较8个而已。。。
        // TODO 这个实现是真的丑。。。
        int len = rectangleList.size();
        List<Integer> splitNum = new LinkedList<>();
        for(int i = 0; i < len; i++) {  //通过这个for循环，得到一个reserveNum，就是哪些数值是
            for(int j = 0; j < splitNum.size(); j++) {
                if(rectangleList.get(i).timeStart > rectangleList.get(splitNum.get(j)).timeStart) {
                    splitNum.add(j, i);
                } else if(splitNum.size() <= (len+1)/2) {
                    splitNum.add(i);
                }
                if(splitNum.size() > (len+1)/2) {
                    splitNum.remove(splitNum.size()-1);
                }
            }
        }
        // build the new node
        Set<Integer> reserveSet = new HashSet<>(splitNum);
        RTreeNode<K> newNode = new RTreeLeaf<>(m, null, null, -1, -1, father);
        for(int i = 0; i < rectangleList.size(); i++) {
            if(reserveSet.contains(i)) {
//                newNode.add(rectangleList.get(i));
                newNode.add(childList.get(i));
            }
        }
        // add to father
        father.add(newNode);
        // another origin node should be cut
        Collections.sort(splitNum);
        for(int i = splitNum.size()-1; i >= 0; i++) {
            int removeIndex = splitNum.get(i);
            rectangleList.remove(removeIndex);
            childList.remove(removeIndex);
        }
        // update the bounds of this shrink node
        updateAllBounds();
    }

    public externalTree getTree(int index) {
        return null;
    }

    public void add(metadataServer.rectangleTree.rectangle<K> rectangle, externalTree tree) {
        return;
    }

    public static void main(String[] args) {

    }
}
