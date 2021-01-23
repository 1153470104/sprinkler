package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTNode.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BPlusTreeCommon<K extends Comparable> implements BPlusTree<K>{
    protected boolean onlyRoot;
    protected int m;
    protected int maxNumber;
    protected int minNumber;
    protected BPTNode<K> root;
    protected boolean templateBased;

    public BPlusTreeCommon(int m){
        this.m = m;
        this.maxNumber = m-1;
        this.minNumber = (int) (Math.ceil(m / 2.0) -1);
    }

    @Override
    public void addKey(BPTKey<K> key) {
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
                sb.append(node.getKey(i).getKey().toString()).append(" ");
                System.out.print(node.getKey(i).getKey().toString() + " ");
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
                sb.append("| ").append(node.getKey(i).getKey().toString()).append(" ");
//                System.out.print(node.getKey(i).key.toString());
            }
            node = node.getLeafNext();
        } while(node != null);
        sb.append("|");
//        System.out.print("|");
//        System.out.print(sb.toString());
        return sb.toString();
    }

}
