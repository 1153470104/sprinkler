package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.BPTNode.BPTNode;
import BPlusTree.keyType.MortonCode;
import dispatcher.dataTool;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * implement of template B+ tree
 * the leaf node maybe is empty, and don't have key-value pair
 *
 * 基于模板的B+树有一个比较重要的特点就是它的非叶节点不再出现在子节点中
 * 所以估计以后要用的时候得注意这一点, 目前还不知道会有什么后果
 */
public class BPlusTreeTemplated<K extends Comparable, V> extends BPlusTree<K, V> {
    private double skewness = 0.2; // the limit of skewness
    private int leafNum = 0; //the total number of leafNode

    /**
     * init of template tree
     * @param tree the scratched B+ tree
     */
    public BPlusTreeTemplated(BPlusTree<K, V> tree) {
        super(tree.conf);
        this.onlyRoot = false;
        this.templateBased = true;
        this.root = tree.rootCopy();
        this.entryNum = 0;

        this.timeStart = ((BPlusTree<K, V>)tree).getTimeStart();
        this.timeEnd = ((BPlusTree<K, V>)tree).getTimeEnd();
        this.keyStart = ((BPlusTree<K, V>)tree).getKeyStart();
        this.keyEnd = ((BPlusTree<K, V>)tree).getKeyEnd();
    }

    /**
     * change the skewness of this template tree
     *
     * @param skewnessValue
     */
    public void setSkewness(double skewnessValue) {
        this.skewness = skewnessValue;
    }

    /**
     * addKey function of template tree
     * without global split only on leaf and leaf-1 level
     *
     * @param key the key to be inserted
     */
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
        // because the insert of templated tree could only happen in the bottom
        // so, increasing layers is not considered
        if (checkNum == 1) {
            // 判断是否要超出数量限制，超出就split
            // 有可能成功，有可能不成功
            this.split(node);
        }
    }

    /**
     *
     * @param key1 the start key of the searching domain
     * @param key2 the end key of the searching domain
     * @return a list of key in this domain
     */
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

    @Override
    public List<BPTKey<K>> search(int timeStart, int timeEnd, K key1, K key2) {
        List<BPTKey<K>> domainKeys = new LinkedList<>();
        // this part is using bloom filter to test if there's entry in that time gap
        if (!hasTime(timeStart, timeEnd)) {
            return domainKeys;
        }
        List<BPTKey<K>> rawKeys = this.search(key1, key2);
        for(BPTKey k: rawKeys) {
            //TODO this place is not generic any more,
            //TODO the timestamp should have a better way to be brought in
            if(dataTool.inTimeDomain((BPTValueKey<MortonCode, String>) k, timeStart, timeEnd)) {
                domainKeys.add(k);
            }
        }
        return domainKeys;
    }

    /**
     * simple mode of split function
     * if the father node is full, quit split
     *
     * @param node the node to be split
     * @return int shows if split success
     *         which -1 means fail, 1 means success
     */
    public int split(BPTNode<K> node) {
        int siblingIsLeaf = 0;

        // 不存在没有父节点的情况，所以套用从头建立B+树中的一种情况
        BPTNode<K> father = node.getFather();
        int fatherIndex = father.searchKey(node.getKey(0));

        // 判断父节点还能不能分，不能分就直接返回-1
        if(father.keyLength() == this.maxNumber) {
            return -1;
        } else {
            father.insertKey(fatherIndex, new BPTKey<K>(node.getKey(minNumber).key()));
            BPTNode<K> siblingNode = new BPTNode<K>(this.m, father);
            siblingNode.setIsLeaf(node.isLeaf());
            if (!siblingNode.isLeaf()) {
                siblingIsLeaf = 1;
            }
            for (int i = minNumber + siblingIsLeaf; i < m; i++) {
                siblingNode.insertKey(i - (minNumber + siblingIsLeaf), node.getKey(i));
            }
            for (int i = m - 1; i > minNumber - 1; i--) {
                node.deleteKey(i);
                BPTNode<K> childNode = node.deleteChild(i + 1);
                if (childNode != null) {
                    siblingNode.insertChild(0, childNode);
                    childNode.setFather(siblingNode);
                }
//                System.out.print("root: ");
//                System.out.println(node.childLength());
            }
            father.insertChild(fatherIndex + 1, siblingNode);

            // 接下来的部分直接对node和sibling进行处理，
            // 并不讨论是否为叶节点，因为在node内部方法中已经规避掉非叶问题了
            siblingNode.setLeafPrev(node);
            siblingNode.setLeafNext(node.getLeafNext());
            node.setLeafNext(siblingNode);
            return 1;
        }
    }

    /**
     * judgement function of if the tree is balanced
     * list the formula as below:
     * params:
     *   average_num: the average key number of leaf node
     *   max_leaf_num: the max leaf key number
     *   skewness: this.skewness
     *
     * if (max_leaf_num - average_num) / average_num > skewness
     *   return false
     * else
     *   return true
     *
     * @return the boolean value of if balanced
     */
    public boolean isBalanced() {
        double averageNum;
//        int minNodeNum = m;
        int maxNodeNum = 0;
        int nodeNum = 0;

        BPTNode<K> node = root;
        while(node.childLength() > 0) {
            node = node.getChild(0);
        }

        while(node != null){
            int keyNum = node.keyLength();
//            if(keyNum < minNodeNum) {
//                minNodeNum = keyNum;
//            }
            if(keyNum > maxNodeNum) {
                maxNodeNum = keyNum;
            }
            nodeNum += 1;
            node = node.getLeafNext();
        }
        this.leafNum = nodeNum;
        averageNum = (double)this.entryNum / (double)nodeNum;

        // 这里是只检测在超出最大范围的时候，是不是平衡的，用最大节点的数量来计算
        if((maxNodeNum - averageNum) / averageNum > this.skewness) {
            return false;
        }
        return true;
    }

    /**
     * balance function of template tree
     * averagely assign keys onto every leaf node
     * then rewrite the non-leaf nodes' keys to make sure the correctness of the frame
     *
     * if tree is balance or not, the function would be execute
     * the isBalance function is set in balance function
     */
    public void balance(){
        // !TODO 突然想到，对于每个不同的 m 可能都有无法balance的情况，balance操作完了，还不balance
        //居然是因为测试方便把 is balance放到balance功能中进行检测
        //这就要求使用template的人手动balance 而非 add的同时balance
        /* 叶节点和entrynum数目的比较顺序都能写反我也是菜 */
        // 有点私心，写成一定比叶节点数要大两倍
//        System.out.println((this.leafNum > this.entryNum) || this.isBalanced());
        /* 本来出现一个离奇bug，后来才发现是因为leafNum的存在依赖于isBalance的计算 */
        boolean balanceOrNot = this.isBalanced();
        if (this.leafNum*10 >= this.entryNum || balanceOrNot) {
//            System.out.println("shit");
            return;
        }

//        System.out.println("why");
//        System.out.println("leafNum: " + leafNum + ", entryNum: " + entryNum);
//        System.out.println();

        BPTNode<K> searchNode = this.root;
        List<BPTNode<K>> nodeDeque = new LinkedList<>();
        nodeDeque.add(searchNode);
        while(!searchNode.getChild(0).isLeaf()) {
            for(int i = 0; i < searchNode.childLength(); i++) {
                nodeDeque.add(searchNode.getChild(i));
            }
            nodeDeque.remove(0);
            searchNode = nodeDeque.get(0);
        }
//        System.out.print("nodeDeque length: ");
//        System.out.println(nodeDeque.size());

        // 对具体的要把leaf接到哪个node上进行跟踪
        BPTNode<K> readNode = searchNode.getChild(0);
//        int readNum = 0;
//        BPTNode<K> readNode = nodeDeque.get(readNum);
        int writeNum = 0;
        BPTNode<K> writeNode = nodeDeque.get(writeNum);
//        int readNodeNum = readNode.childLength();
//        int readNodePos = 0;
        int writeNodeNum = writeNode.childLength();
        int writeNodePos = 0;

        // average是平均数，complement是计算每隔几个要补一个，避免余数的部分在后面溢出
        int averageNum = this.entryNum / this.leafNum;
        //加入一个leftNum用于避免complement是1的时候, 避免为了补complement导致后面不够
        int leftNum = entryNum - averageNum * leafNum;
        int complement;
        if(entryNum - averageNum * leafNum == 0) {
            complement = 0;
        } else {
            complement = leafNum / (entryNum - averageNum * leafNum);
        }
        int plus = 0; //用于complement的辅助变量，为 1或0

        Deque<BPTKey<K>> keyDeque = new LinkedList<>();
        BPTNode<K> prevLeaf = null;
        int readCount = 1 ; /* readCount 是用来判断读有没有读到边界*/
        int writeCount = 1 ; /* writeCount是用来计数，判断要不要complement */
        //下面一大个for循环是为了把底层的顺序做对，然后再接着去写
        for(int i = 0; i < this.leafNum; i++) {
            //判断要不要加 1
            if(complement == 0) {
                plus = 0;
            } else if(writeCount % complement == 0 && leftNum>0) {
                plus = 1;
                leftNum -= 1;
            } else {
                plus = 0;
            }
            // TODO 这里要考虑边界情况，避免死循环
            while(keyDeque.size() < averageNum+plus && readCount<=this.leafNum) { /*居然边界都能写错*/
                for(int j = 0; j < readNode.keyLength(); j++) {
                    keyDeque.add(readNode.getKey(j));
                }
//                readNodePos += 1;
//                if(readNodePos >= readNodeNum) {
//                    readNum += 1;
//                    readNode = nodeDeque.get(readNum);
//                }
                readNode = readNode.getLeafNext();
                readCount += 1;
            }

            writeCount += 1;

            // build new leaf node and replace old-one
            BPTNode<K> newLeaf = new BPTNode<>(m, writeNode);
            newLeaf.setLeafPrev(prevLeaf);
            if(prevLeaf != null) {
                prevLeaf.setLeafNext(newLeaf);
            }

//            System.out.print("averageNum+plus: ");
//            System.out.println(averageNum+plus);

            for(int k = 0; k < averageNum+plus; k++) {
//                System.out.print(keyDeque.getFirst().getKey());
//                System.out.print(" ");
                // TODO 避免对空 keyDeque作remove
                if(keyDeque.size() == 0) {
                    break;
                }
                //
                newLeaf.insertKey(k, keyDeque.remove());
            }
            prevLeaf = newLeaf;

            /*不能只insert不remove，原有的不会自己扔掉*/
            writeNode.deleteChild(writeNodePos);
            writeNode.insertChild(writeNodePos, newLeaf);
            writeNodePos += 1;
            /*没考虑到循环结束的边界问题*/
            if(writeNodePos >= writeNodeNum && writeNum < nodeDeque.size()-1) {
                // nodedeque中跟踪的序号+1 并读取新弄得
                // node中child位置归零， 重读node child数目
                writeNum += 1;
                writeNode = nodeDeque.get(writeNum);
                writeNodeNum = writeNode.childLength();
                writeNodePos = 0;
            }
        }

        //接下来自下而上，把整个树的key调整过来
        BPTNode<K> orderNode = this.root;
        List<BPTNode<K>> orderList = new LinkedList<>();
        orderList.add(orderNode);
        int num = 0;
        while(!orderNode.getChild(0).isLeaf()) {
            for(int i = 0; i < orderNode.childLength(); i++) {
                orderList.add(orderNode.getChild(i));
            }
            orderNode = orderList.get(num++);
        }

        int sum = orderList.size()-1;
        BPTNode<K> tempNode;
        BPTNode<K> iterateNode;
        do {
            tempNode = orderList.get(sum);
            for(int i = 0; i < tempNode.keyLength(); i++) {
                tempNode.deleteKey(i);
                // TODO 未考虑如果tempnode的子节点没有key的情况
                // 可以在balance的时候检测一下，entryNum 是否大于 nodeNum
                iterateNode = tempNode.getChild(i+1);
                // 这边要注意不是直接取子节点的第一个key，
                // 而是子节点往下直到leafnode 然后再找最小的子节点的第一个key
                while(!iterateNode.isLeaf()) {
                    iterateNode = iterateNode.getChild(0);
                }
                tempNode.insertKey(i, new BPTKey<K>(iterateNode.getKey(0).key()));
            }
            sum = sum-1;
        } while(tempNode.getFather() != null);
        System.out.println("balanced!");
        this.printInfo();
    }

    @Override
    public void printInfo() {
        StringBuilder ss = new StringBuilder();
        System.out.println("time domain: "+String.valueOf(timeStart) + " to "+ String.valueOf(timeEnd));
        System.out.print("tree's m: " + String.valueOf(m) + "; nodeNum: " + String.valueOf(leafNum) + "; entryNum: " + String.valueOf(entryNum));
        if(this.isTemplate())  {
            System.out.println(" is templated");
        } else {
            System.out.println();
        }
        printBasic();
    }
}
