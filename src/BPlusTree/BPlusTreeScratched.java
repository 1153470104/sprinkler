package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTNode.*;

import java.util.ArrayList;
import java.util.List;

public class BPlusTreeScratched<K extends Comparable> extends BPlusTreeCommon<K>{

    public BPlusTreeScratched(int m){
        super(m);
        this.onlyRoot = true;
        this.root = new BPTLeaf<K>(m, null);
        this.templateBased = false;
    }

    @Override
    public void addKey(BPTKey<K> key) {
        //super在这里加了一个entryNum
        super.addKey(key);

        int checkNum = 0;
        int index = 0;
        BPTNode<K> node = this.root;
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
    public List<BPTKey<K>> search(K key1, K key2) {
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
                if(node.getKey(i).key().compareTo(key1) != -1) {
                    start = true;
                }
                if(node.getKey(i).key().compareTo(key2) == 1) {
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


//    @Override
    public int split(BPTNode<K> node) {
        int returnNum;
//        System.out.println(node);
        int siblingIsLeaf = 0;
        if(node.getFather() == null) {
            /* 需要新建父节点的情况 */
            BPTNonLeaf<K> father = new BPTNonLeaf<K>(this.m, null);
            father.insertKey(0, node.getKey(minNumber));
            BPTNode<K> siblingNode = new BPTNodeCommon<K>(this.m, father);
            siblingNode.setIsLeaf(node.isLeaf());
            if (!siblingNode.isLeaf()) {
                siblingIsLeaf = 1;
            }
            for(int i = minNumber+siblingIsLeaf; i < m; i++) {
                siblingNode.insertKey(i-(minNumber+siblingIsLeaf), node.getKey(i));
            }
            for(int i = m-1; i > minNumber-1; i--) {
                node.deleteKey(i);
                BPTNode<K> childNode = node.deleteChild(i+1);
                if (childNode != null) {
                    siblingNode.insertChild(0, childNode);
                    childNode.setFather(siblingNode);
                }
            }

            father.insertChild(0, node);
            father.insertChild(1, siblingNode);

            /* 接下来的部分直接对node和sibling进行处理，
             * 并不讨论是否为叶节点，因为在node内部方法中已经规避掉非叶问题了
             * */
            siblingNode.setLeafPrev(node);
            siblingNode.setLeafNext(node.getLeafNext());
            node.setLeafNext(siblingNode);

            node.setFather(father);
//            System.out.println("length of 1 child: " + Integer.toString(father.getChild(0).childLength()));

            returnNum = -1;
            this.root = father;
            this.onlyRoot = false;
        } else {
            /* 父节点已经存储在的情况 */
            BPTNode<K> father = node.getFather();
            int fatherIndex = father.searchKey(node.getKey(0));
            father.insertKey(fatherIndex, new BPTKey<K>(node.getKey(minNumber).key()));
            BPTNode<K> siblingNode = new BPTNodeCommon<K>(this.m, father);
            siblingNode.setIsLeaf(node.isLeaf());
            if (!siblingNode.isLeaf()) {
                siblingIsLeaf = 1;
            }
            for(int i = minNumber+siblingIsLeaf; i < m; i++) {
                siblingNode.insertKey(i-(minNumber+siblingIsLeaf), node.getKey(i));
            }
            for(int i = m-1; i > minNumber-1; i--) {
                node.deleteKey(i);
                BPTNode<K> childNode = node.deleteChild(i+1);
                if (childNode != null) {
                    siblingNode.insertChild(0, childNode);
                    childNode.setFather(siblingNode);
                }
//                System.out.print("root: ");
//                System.out.println(node.childLength());
            }
            father.insertChild(fatherIndex+1, siblingNode);
            returnNum = 0;

            /* 需要说明的内容同上 */
            siblingNode.setLeafPrev(node);
            siblingNode.setLeafNext(node.getLeafNext());
            node.setLeafNext(siblingNode);
        }

        return returnNum;
    }
}
