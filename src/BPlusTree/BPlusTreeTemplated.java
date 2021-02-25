package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTNode.BPTNode;
import BPlusTree.BPTNode.BPTNodeCommon;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class BPlusTreeTemplated<K extends Comparable> extends BPlusTreeCommon<K> {
    /**
     * templated tree
     * @param tree
     *
     * 基于模板的B+树有一个比较重要的特点就是它的非叶节点不再出现在子节点中
     * 所以估计以后要用的时候得注意这一点, 目前还不知道会有什么后果
     */
    private double skewness = 0.2;
    private int leafNum = 0;


    public BPlusTreeTemplated(BPlusTree<K> tree) {
        super(tree.getM());
        this.onlyRoot = false;
        this.templateBased = true;
        this.root = tree.rootCopy();
    }

    public void setSkewness(double skewnessValue) {
        this.skewness = skewnessValue;
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
            /* 判断是否要超出数量限制，超出就split
             * 有可能成功，有可能不成功
             */
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

    public int split(BPTNode<K> node) {
        int siblingIsLeaf = 0;

        /* 不存在没有父节点的情况，所以套用从头建立B+树中的一种情况 */
        BPTNode<K> father = node.getFather();
        int fatherIndex = father.searchKey(node.getKey(0));

        /* 判断父节点还能不能分，不能分就直接返回-1 */
        if(father.keyLength() == this.maxNumber) {
            return -1;
        } else {
            father.insertKey(fatherIndex, new BPTKey<K>(node.getKey(minNumber).getKey()));
            BPTNode<K> siblingNode = new BPTNodeCommon<K>(this.m, father);
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

            /* 接下来的部分直接对node和sibling进行处理，
             * 并不讨论是否为叶节点，因为在node内部方法中已经规避掉非叶问题了
             * */
            siblingNode.setLeafPrev(node);
            siblingNode.setLeafNext(node.getLeafNext());
            node.setLeafNext(siblingNode);
            return 1;
        }
    }

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

        /* 这里是只检测在超出最大范围的时候，是不是平衡的，用最大节点的数量来计算 */
        if((maxNodeNum - averageNum) / averageNum > this.skewness) {
            return false;
        }
        return true;
    }

    public void balance(){
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

        int leafCount = 0;
        BPTNode<K> readNode = nodeDeque.get(0);
//        int readNum = 0;
//        BPTNode<K> readNode = nodeDeque.get(readNum);
        int writeNum = 0;
        BPTNode<K> writeNode = nodeDeque.get(writeNum);

        /* average是平均数，complement是计算每隔几个要补一个，避免余数的部分在后面溢出 */
        int averageNum = this.entryNum / this.leafNum;
        int complement = leafNum / (entryNum - averageNum * leafNum);

//        int readNodeNum = readNode.childLength();
//        int readNodePos = 0;
        int writeNodeNum = writeNode.childLength();
        int writeNodePos = 0;
        Deque<BPTKey<K>> keyDeque = new LinkedList<>();
        BPTNode<K> prevLeaf = null;
        int plus = 0;
        for(int i = 0; i < this.leafNum; i++) {
            if(leafCount % complement == 0) {
                plus = 1;
            }
            // TODO 这里要考虑边界情况，避免死循环
            while(keyDeque.size() < averageNum+plus) {
                for(int j = 0; j < readNode.keyLength(); j++) {
                    keyDeque.add(readNode.getKey(j));
                }
//                readNodePos += 1;
//                if(readNodePos >= readNodeNum) {
//                    readNum += 1;
//                    readNode = nodeDeque.get(readNum);
//                }
                readNode = readNode.getLeafNext();
            }

            /* build new leaf node and replace old-one */
            BPTNode<K> newLeaf = new BPTNodeCommon<>(m, writeNode);
            newLeaf.setLeafPrev(prevLeaf);
            if(prevLeaf != null) {
                prevLeaf.setLeafNext(newLeaf);
            }
            for(int k = 0; k < averageNum+plus; k++) {
                newLeaf.insertKey(k, keyDeque.remove());
            }

        }

    }

}
