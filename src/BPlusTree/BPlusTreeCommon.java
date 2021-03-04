package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTNode.*;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

public class BPlusTreeCommon<K extends Comparable> implements BPlusTree<K>{
    protected boolean onlyRoot;
    protected int m;
    protected int maxNumber;
    protected int minNumber;
    protected BPTNode<K> root;
    protected boolean templateBased = false;
    protected int entryNum = 0;


    //由于这四个元素和对象声明没用绑在一块，所以使用的时候一定要注意，别忘了
    protected int timeStart;
    protected int timeEnd;
    protected K keyStart; //keystart仅仅用于分配，没有在addKey的时候对其进行检验，所以一定要注意
    protected K keyEnd;


    public int getTimeStart() {
        return timeStart;
    }

    public int getTimeEnd() {
        return timeEnd;
    }

    public K getKeyStart() {
        return keyStart;
    }

    public K getKeyEnd() {
        return keyEnd;
    }

    public BPlusTreeCommon(int m){
        this.m = m;
        this.maxNumber = m-1;
        this.minNumber = (int) (Math.ceil(m / 2.0) -1);
    }

    @Override
    public void addKey(BPTKey<K> key) {
        entryNum = entryNum+1;
    }

    @Override
    public List<BPTKey<K>> search(K key1, K key2) {
        return null;
    }

    @Override
    public String printBasic() {
        Queue<BPTNode<K>> nodeQueue = new LinkedList<>();
        nodeQueue.add(root);
        StringBuilder sb = new StringBuilder();
        int levelNum = 1;  // how many keys in this level
        int levelCount = 0;  // how many keys in next level so far we know
        int currentOut = 0;  // how many keys are been printed in this level
        while (!nodeQueue.isEmpty()) {
            BPTNode<K> node = nodeQueue.remove();
            int keyNum = node.keyLength();
//            System.out.println("node key length: " + Integer.toString(keyNum) + " child length" + Integer.toString(node.childLength()));
            sb.append("| ");
            System.out.print("| ");
            for (int i = 0; i < keyNum; i++) {
                sb.append(node.getKey(i).key().toString()).append(" ");
                System.out.print(node.getKey(i).key().toString() + " ");
            }
            int childNum = node.childLength();
            levelCount += childNum;
            for(int i = 0; i < childNum; i++) {
                BPTNode<K> child = node.getChild(i);
                nodeQueue.add(child);
            }
            currentOut += 1;
            if (currentOut == levelNum && levelCount > 0) {
                sb.append("|\n");
                System.out.print("|\n");
                levelNum = levelCount;
                levelCount = 0;
                currentOut = 0;
            }
        }
        sb.append("|");
        System.out.print("|");
        System.out.println();
        return sb.toString();
    }

    public String printData() {
        StringBuilder sb = new StringBuilder();
        BPTNode<K> node = root;
        while(node.childLength() > 0) {
            node = node.getChild(0);
//            System.out.println(node.childLength());
//            System.out.println("here");
        }
        do {
            for(int i = 0; i < node.keyLength(); i++){
                sb.append("| ").append(node.getKey(i).key().toString()).append(" ");
//                System.out.print(node.getKey(i).key.toString());
            }
            node = node.getLeafNext();
        } while(node != null);
        if (sb.length() == 0) {
            sb.append("| |");
        } else {
            sb.append("|");
        }
//        System.out.print("|");
//        System.out.print(sb.toString());
        System.out.println();
        return sb.toString();
    }

    @Override
    public boolean isBlockFull() {
        if (this.entryNum < 1000) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isTemplated() {
        return this.templateBased;
    }

    @Override
    public BPTNode<K> rootCopy() {
        BPTNode<K> newRoot;
        BPTNode<K> tempNewRoot;
        BPTNode<K> tempRoot;

        Queue<BPTNode<K>> nodeQueue = new LinkedList<>();
        Queue<BPTNode<K>> copyQueue = new LinkedList<>();
        tempRoot = root;
        newRoot = tempRoot.valueCopy(null);
        tempNewRoot = newRoot;

        try{
            while(true) {
                for(int i = 0; i < tempRoot.childLength(); i++) {
                    BPTNode<K> theChild = tempRoot.getChild(i);
                    if (theChild.isLeaf()) {
                        tempNewRoot.addChild(new BPTNodeCommon<>(m, tempNewRoot));
                    } else {
                        nodeQueue.add(theChild);
                        BPTNode<K> copyChild = theChild.valueCopy(tempNewRoot);
                        tempNewRoot.addChild(copyChild);
                        copyQueue.add(copyChild);
                    }
                }
                tempNewRoot = copyQueue.remove();
                tempRoot = nodeQueue.remove();
            }
        } catch(NoSuchElementException e) {
            System.out.println("copy finish!");
        }

        // 维护底层的 prev next 结构
        Queue<BPTNode<K>> tempQueue = new LinkedList<>();
        tempQueue.add(newRoot);
        while(tempQueue.size() > 0){
            tempNewRoot = tempQueue.peek();
            if(tempNewRoot.isLeaf()){
                break;
            }else{
                for(int i = 0; i < tempNewRoot.childLength(); i++){
                    tempQueue.add(tempNewRoot.getChild(i));
                }
                tempQueue.remove();
            }
        }
        BPTNode<K> prevNode;
        BPTNode<K> nextNode;
        prevNode = tempQueue.remove();
        while(tempQueue.size() > 0){
            nextNode = tempQueue.remove();
            prevNode.setLeafNext(nextNode);
            nextNode.setLeafPrev(prevNode);
            prevNode = nextNode;
        }

        return newRoot;
    }

    @Override
    public int getM() {
        return m;
    }

    @Override
    public void flushOut() {

    }

    @Override
    public void setStartTime(int start) {
        this.timeStart = start;
    }

    @Override
    public void setEndTime(int end) {
        this.timeEnd = end;
    }

    @Override
    public void setKeyStart(K start) {
        this.keyStart = start;
    }

    @Override
    public void setKeyEnd(K end) {
        this.keyEnd = end;
    }

//    @Override
//    public String writeInDisk() {
//
//    }
}
