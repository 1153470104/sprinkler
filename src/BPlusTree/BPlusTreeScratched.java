package BPlusTree;

import BPTException.fullTreeException;
import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.BPTNode.*;
import BPlusTree.configuration.configuration;
import BPlusTree.keyType.MortonCode;
import dispatcher.dataTool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * the implement of B+ tree which is built from scratched
 * so that the split function is more intact
 *
 * @param <K> the key's type
 */
public class BPlusTreeScratched<K extends Comparable, V> extends BPlusTree<K, V> {

    public BPlusTreeScratched(configuration conf){
        super(conf);
        this.onlyRoot = true;
        this.root = new BPTLeaf<K>(m, null);
        this.templateBased = false;
    }

    public BPlusTreeScratched(int m){
        super(m);
        this.onlyRoot = true;
        this.root = new BPTLeaf<K>(m, null);
        this.templateBased = false;
    }

    /**
     * @param key the key to be inserted
     */
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
                this.blockNum += 1; //每分裂一次block num这里+1
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
                if (blockLimit - blockNum < depth) {
                    this.blockFull = true;
                    node.deleteKey(index);
                    this.scratchNumLimit = this.entryNum;
                    break;
                }
                this.blockNum += 1; //每分裂一次block num这里+1
                if (this.split(node) != -1) {
                    node = node.getFather();
                    checkNum = node.checkout();
                } else {
                    this.root = node.getFather();
                    break;
                }
            }
        }
    }

    /**
     * @param key1 the start key of the searching domain
     * @param key2 the end key of the searching domain
     * @return  a list of all keys between key1 & key2
     */
    @Override
    public List<BPTKey<K>> search(K key1, K key2) {
        List<BPTKey<K>> nodeList = new ArrayList<>();
        if(key1.compareTo(key2) == 1) {
            System.out.println("wrong input, key1 should less than key2");
            return nodeList;
        }
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

        return nodeList;
    }

    @Override
    public List<BPTKey<K>> search(int timeStart, int timeEnd, K key1, K key2) {
        List<BPTKey<K>> domainKeys = new LinkedList<>();
        // this part is using bloom filter to test if there's entry in that time gap
        if (!hasTime(timeStart, timeEnd)) {
            return domainKeys;
        }
        if (!hasTime(timeStart, timeEnd)) {
            return domainKeys;
        }
        //TODO this is not a good way to
        List<BPTKey<K>> rawKeys = this.search(key1, key2);
        for(BPTKey k: rawKeys) {
            //TODO  this place is not generic any more,
            //TODO  the timestamp should have a better way to be brought in
            //TODO  maybe the better way is to let every type realize it in dataTool,
            //TODO  but I think there must be a better way
            if(dataTool.inTimeDomain((BPTValueKey<MortonCode, String>) k, timeStart, timeEnd)) {
                domainKeys.add(k);
            }
        }
//        System.out.println("scratch!!: " + domainKeys.size());
        return domainKeys;
    }


    /**
     * full implementation of split
     * @param node the node to be split
     * @return a int shows if it reaches top and set up a new root
     *         -1 means a new root, 0 means not reach root yet
     */
//    @Override
    public int split(BPTNode<K> node) {
        int returnNum;
        int siblingIsLeaf = 0;
        if(node.getFather() == null) {
            /* 需要新建父节点的情况 */
            this.blockNum += 1; // 因为新建父节点，所以需要在这里多加blockNum一次
            this.depth += 1;
            BPTNonLeaf<K> father = new BPTNonLeaf<K>(this.m, null);
            father.insertKey(0, node.getKey(minNumber));
            BPTNode<K> siblingNode = new BPTNode<K>(this.m, father);
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

            returnNum = -1;
            this.root = father;
            this.onlyRoot = false;
        } else {
            /* 父节点已经存储在的情况 */
            BPTNode<K> father = node.getFather();
            int fatherIndex = father.searchKey(node.getKey(0));
            father.insertKey(fatherIndex, new BPTKey<K>(node.getKey(minNumber).key()));
            BPTNode<K> siblingNode = new BPTNode<K>(this.m, father);
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
