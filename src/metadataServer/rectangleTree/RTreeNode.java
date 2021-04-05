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

    /**
     * update the boundary due to one use, which is not complete
     * TODO need to be simplified to some assist function
     * @param newRectangle
     */
    public void updateBounds(rectangle<K> newRectangle) {
        if(this.selfRectangle.top == null) this.selfRectangle = newRectangle; //确保在空状态下，update正常
        if(this.selfRectangle.top.compareTo(newRectangle.top) == 1)  this.selfRectangle.top = newRectangle.top;
        if(this.selfRectangle.bottom.compareTo(newRectangle.bottom) == -1)  this.selfRectangle.bottom = newRectangle.bottom;
        if(this.selfRectangle.timeStart > newRectangle.timeStart)  this.selfRectangle.timeStart = newRectangle.timeStart;
        if(this.selfRectangle.timeEnd == -1 || this.selfRectangle.timeEnd < newRectangle.timeEnd)
            this.selfRectangle.timeEnd = newRectangle.timeEnd;
    }

    public void updateAllBounds() {
        if(this.getLength()>0) {
            selfRectangle = rectangleList.get(0).copy();
            for(rectangle<K> rec: rectangleList) {
                updateBounds(rec);
            }
        }
        if(!this.isLeaf()) {
            for(int i = 0; i < rectangleList.size(); i++) {
                rectangleList.remove(i);
                rectangleList.add(i, childList.get(i).selfRectangle);
            }
        }
        if (fatherNode!=null)  fatherNode.updateAllBounds();
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
            if(status == rectangle.CONTAIN) {  // if there's a node which covers it, return
//                System.out.println(rectangleList.get(i).toString() + " status with " + inputRec.toString() + ": contain");
                return childList.get(i);
            }
            interStatus[i] = status;
        }
        // below choose the latest across or be-covered node to return
        int laterNodeIndex = -1;
        int left = 0;
        for(int i = 0; i < rectangleList.size(); i++) {
            if((interStatus[i] == rectangle.PERTAIN || interStatus[i] == rectangle.ACROSS) && rectangleList.get(i).timeStart > left) {
                laterNodeIndex = i;
                left = rectangleList.get(i).timeStart;
            }
        }
        if(laterNodeIndex != -1) {
//            System.out.println(rectangleList.get(laterNodeIndex).toString() + " status with " + inputRec.toString() + ": across");
            return childList.get(laterNodeIndex);
        }
        // if no child node has any intersection with the input rectangle
        // return the most left one
        laterNodeIndex = -1;
        left = 0;
        for(int i = 0; i < rectangleList.size(); i++) {
            if(rectangleList.get(i).timeStart > left) {
                laterNodeIndex = i;
                left = rectangleList.get(i).timeStart;
            }
        }
//        System.out.println(rectangleList.get(laterNodeIndex).toString() + " status with " + inputRec.toString() + ": irrelevant");
        return childList.get(laterNodeIndex);
        // below code is wrong emmm, the -1 sign of unlimited bound is useless...
//        for(int i = 0; i < rectangleList.size(); i++) {
//            if(rectangleList.get(i).timeEnd == -1) {
//                return childList.get(i);
//            }
//        }
    }

    public List<externalTree> searchChunk(rectangle<K> rec) {
        List<externalTree> leafList = new LinkedList<>();
        for(RTreeNode<K> node: childList) {
            leafList.addAll(node.searchChunk(rec));
        }
        return leafList;
    }

    public int getLength() {
        return rectangleList.size();
    }

    public void add(RTreeNode<K> node) {
        this.rectangleList.add(node.selfRectangle);
        this.childList.add(node);
        updateAllBounds();
    }

    public void split() {
        if(!overflow())  return;  //再检查一下
        RTreeNode<K> father;
        boolean newFather = false;
        if(this.fatherNode == null) {
            father = new RTreeNode<K>(
                    this.m, selfRectangle.top, selfRectangle.bottom, selfRectangle.timeStart
                    , selfRectangle.timeEnd, null);
            father.initChild();
            newFather = true;
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
                    break; /* 又忘记break，导致它永不停止*/
                }
            }
            /* 这玩意居然本来被我放进for循环里面了 */
            if(splitNum.size() < (len+1)/2) {
                splitNum.add(i);
            }
            if(splitNum.size() > (len+1)/2) {
                splitNum.remove(splitNum.size()-1);
            }
//            System.out.println(i);
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
        if(newFather)  father.add(this);
        father.add(newNode);
        // another origin node should be cut
        Collections.sort(splitNum);
        for(int i = splitNum.size()-1; i >= 0; i++) {
            int removeIndex = splitNum.get(i);
            rectangleList.remove(removeIndex);
            childList.remove(removeIndex);
        }
        this.fatherNode = father;
        // update the bounds of this shrink node
        updateAllBounds();
    }

    public RTreeNode<K> getChild(int index) {
        return childList.get(index);
    }

    public externalTree getTree(int index) {
        return null;
    }

    public void add(rectangle<K> rectangle, externalTree tree) {
        return;
    }

    public String toString() {
        Deque<RTreeNode<K>> deque = new LinkedList<>();
        Deque<RTreeNode<K>> leafDeque = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        deque.add(this);
        RTreeNode<K> temp;
        while(deque.size()>0) {
            int len = deque.size();
            for(int i = 0; i < len; i++) {
                temp = deque.poll();
                for(int j = 0; j < temp.getLength(); j++) {
                    if(!temp.isLeaf()) {
                        deque.add(temp.getChild(j));
                    } else {
                        leafDeque.add(temp);
                        /*只要加一个就行，别加了好几轮*/
                        break;
                    }
                }
                sb.append(temp.selfRectangle.toString()).append("|");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append("\n");
        }
        while(leafDeque.size()>0) {
            temp = leafDeque.poll();
            for(int i = 0; i < temp.rectangleList.size(); i++) {
//                sb.append("??? "+i);
                sb.append(temp.rectangleList.get(i).toString()).append(";");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append("|");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("\n");

        System.out.println(sb.toString());
        return sb.toString();
    }
}
