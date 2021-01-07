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
        this.root = new BPTLeaf(m, null);
    }

    @Override
    public void addKey(BPTKey<Integer> key) {
        int checkNum = 0;
        int index = 0;
        BPTNode node = this.root;
        if(onlyRoot) {
            index = node.searchKey(key);
            checkNum = node.insertKey(index, key);
            if (checkNum == 1) {
                split(node);
            }
        } else {
            while (!node.isLeaf()) {
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
                    break;
                }
            }
//            this.root = node.getFather();
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
    public String printbasic() {
        Queue<BPTNode> nodeQueue = new LinkedList<>();
        nodeQueue.add(root);
        StringBuilder sb = new StringBuilder();
        int levelNum = 1;  // how many keys in this level
        int levelCount = 0;  // how many keys in next level so far we know
        int currentOut = 0;  // how many keys are been printed in this level
        while (!nodeQueue.isEmpty()) {
            BPTNode node = nodeQueue.remove();
            int keyNum = node.keyLength();
//            System.out.println("node key length: " + Integer.toString(keyNum) + " child length" + Integer.toString(node.childLength()));
            sb.append("| ");
            System.out.print("| ");
            for (int i = 0; i < keyNum; i++) {
                sb.append(node.getKey(i).key.toString()).append(" ");
                System.out.print(node.getKey(i).key.toString() + " ");
            }
            int childNum = node.childLength();
            levelCount += childNum;
            for(int i = 0; i < childNum; i++) {
                BPTNode child = node.getChild(i);
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
        return sb.toString();
    }

    @Override
    public int split(BPTNode node) {
        int returnNum;
//        System.out.println(node);
        if(node.getFather() == null) {
            BPTNonLeaf father = new BPTNonLeaf(this.m, null);
            BPTNode siblingNode = new BPTNodeCommon(this.m, father);
            for(int i = minNumber; i < m; i++) {
                siblingNode.insertKey(i-minNumber, node.getKey(i));
            }
            for(int i = m-1; i > minNumber-1; i--) {
                node.deleteKey(i);
                node.deleteChild(i+1);
//                System.out.print("root: ");
//                System.out.println(node.childLength());
            }
            father.insertKey(0, siblingNode.getKey(0).getKey());
            father.insertChild(0, node);
            father.insertChild(1, siblingNode);

            node.setFather(father);
//            System.out.println("length of 1 child: " + Integer.toString(father.getChild(0).childLength()));

            returnNum = -1;
            this.root = father;
            this.onlyRoot = false;
        } else {
            BPTNonLeaf father = node.getFather();
            int fatherIndex = father.searchKey(node.getKey(0));
            father.insertKey(fatherIndex, new BPTKey<Integer>(node.getKey(minNumber).key));
            BPTNode siblingNode = new BPTNodeCommon(this.m, father);
            for(int i = minNumber; i < m; i++) {
                siblingNode.insertKey(i-minNumber, node.getKey(i));
            }
            for(int i = m-1; i > minNumber-1; i--) {
                node.deleteKey(i);
                node.deleteChild(i+1);
//                System.out.print("root: ");
//                System.out.println(node.childLength());
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
