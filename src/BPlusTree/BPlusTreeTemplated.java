package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTNode.BPTNode;

import java.util.ArrayList;
import java.util.List;

public class BPlusTreeTemplated<K extends Comparable> extends BPlusTreeCommon<K> {


    public BPlusTreeTemplated(BPlusTree<K> tree) {
        super(tree.getM());
        this.onlyRoot = false;
        this.templateBased = true;
        this.root = tree.rootCopy();
    }


    @Override
    public void addKey(BPTKey<K> key) {
        super.addKey(key);
        //TODO to modify
        int checkNum = 0;
        int index = 0;
        BPTNode<K> node = this.root;
        while (!node.isLeaf()) {
            index = node.searchKey(key);
            node = node.getChild(index);
        }
        index = node.searchKey((key));
        checkNum = node.insertKey(index, key);
        /* because the insert of templated tree could only happen in the bottom
           so, increasing layers is not considered*/
        if (checkNum == 1) {
            this.split(node);
        }
        if (!this.isBalanced()) {
            this.balance();
        }
    }

    @Override
    public List<BPTKey<K>> search(K key1, K key2) {
        //TODO to modify
        if(key1.compareTo(key2) == 1) {
            System.out.println("wrong input, key1 should less than key2");
            return null;
        }
        List<BPTKey<K>> nodeList = new ArrayList<>();
        BPTNode<K> node = root;
        while(node.childLength() > 0) {
            node = node.getChild(0);
        }
        boolean start = false;
        boolean end = false;
        do {
            for(int i = 0; i < node.keyLength(); i++){
                // 这里上面的start判断需要大于等于，下面的end判断需要大于
                if(node.getKey(i).getKey().compareTo(key1) != -1) {
                    start = true;
                }
                if(node.getKey(i).getKey().compareTo(key2) == 1) {
                    end = true;
                }
                if(start && !end) {
                    nodeList.add(node.getKey(i));
                }
            }
            node = node.getLeafNext();
        } while(node != null);

        if(nodeList.size() == 0) {
            return null;
        }
        return nodeList;
    }

    public void split(BPTNode<K> node) {

    }

    public boolean isBalanced() {
        return true;
    }

    public void balance(){
    }

}
