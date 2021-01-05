package BPlusTree;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.ToDoubleBiFunction;

public class BPlusTreeCommon implements BPlusTree{
    private boolean onlyRoot;
    private int m;
    private int maxNumber;
    private int minNumber;
    private BPTNode root;

    public BPlusTreeCommon(int m){
        this.m = m;
        this.maxNumber = m-1;
        this.minNumber = (int) (Math.ceil(m / 2.0) -1);
        this.onlyRoot = true;
    }

    @Override
    public void addKey(BPTKey<Integer> key) {
        int checkNum = 0;
        int index = 0;
        BPTNode node = root;
        if(onlyRoot) {
            index = node.searchKey(key);
            checkNum = node.insertKey(index, key);
            if (checkNum == 1) {
                split(node);
                this.root = node.getFather();
            }
        } else {
            while (node.isLeaf()) {
                index = node.searchKey(key);
                node = node.getChild(index);
            }
            index = node.searchKey((key));
            checkNum = node.insertKey(index, key);
            while (checkNum == 1) {
                if (this.split(node) != -1) {
                    node = node.getFather();
                    checkNum = node.checkout();
                } else {
                    this.root = node.getFather();
                }
            }
            this.root = node.getFather();
        }
    }

    @Override
    public void balance(int checkNum, BPTNode node) {
    }

    @Override
    public String search(String key) {
        return null;
    }

    @Override
    public void printbasic() {
        Queue<BPTNode> nodeQueue = new LinkedList<>();
        nodeQueue.add(root);
        int levelNum = 1;
        int levelCount = 0;
        int currentOut = 0;
        while (!nodeQueue.isEmpty()) {
            BPTNode node = nodeQueue.remove();
            int childNum = node.childLength();
            for(int i = 0; i < childNum; i++) {
                if (i < childNum-1) {
                    System.out.print(node.getKey(i).key.toString() + " ");
                }
                nodeQueue.add(node.getChild(i));
                levelCount += 1;
            }
            currentOut += 1;
            if (currentOut == levelNum) {
                System.out.println();
                levelNum = levelCount;
            }
        }
    }

    @Override
    public int split(BPTNode node) {
        int returnNum;
        if(node.getFather() == null) {
            BPTNonLeaf father = new BPTNonLeaf(this.m, null);
            BPTNode siblingNode = new BPTNodeCommon(this.m, father);
            for(int i = minNumber; i <= m; i++) {
                siblingNode.insertKey(i-minNumber, node.getKey(i));
            }
            father.insertChild(0, node);
            father.insertChild(1, siblingNode);
            returnNum = -1;
        } else {
            BPTNonLeaf father = node.getFather();
            int fatherIndex = father.searchKey(node.getKey(0));
            father.insertKey(fatherIndex, new BPTKey<Integer>(node.getKey(minNumber).key));
            BPTNode siblingNode = new BPTNodeCommon(this.m, father);
            for(int i = minNumber; i <= m; i++) {
                siblingNode.insertKey(i-minNumber, node.getKey(i));
            }
            father.insertChild(fatherIndex+1, siblingNode);
            returnNum = 0;
        }

        return returnNum;
    }

    @Override
    public void Combine(BPTNode childNode1, BPTNode childNode2) {
    }
}
