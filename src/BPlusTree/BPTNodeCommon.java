package BPlusTree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BPTNodeCommon<K extends Comparable> implements BPTNode<K>{
    protected int m;
    protected int maxNumber;
    protected int minNumber;
    protected int keyLength;
    protected int childLength;
    protected List<BPTKey<K>> keyList;
    protected List<BPTNode<K>> childernList;
    protected BPTNode<K> fatherNode;
    protected boolean isLeaf;
    private BPTNode<K> leafPrev = null;
    private BPTNode<K> leafnext = null;

    public BPTNodeCommon(int m, BPTNode<K> fatherNode){
        this.m = m;
        this.maxNumber = m-1;
        this.minNumber = (int) (Math.ceil(m / 2.0) -1);
        this.keyLength = 0;
        this.childLength = 0;
        this.keyList = new ArrayList<>();
        this.childernList = new ArrayList<>();
        this.fatherNode = fatherNode;
        this.isLeaf = true;
    }

    @Override
    public int insertKey(int index, BPTKey<K> key) {
        this.keyList.add(index, key);
        this.keyLength += 1;
        return this.checkout();
    }

    @Override
    public void addChild(BPTNode<K> child) {
        this.childernList.add(child);
        this.childLength += 1;
    }

    public void insertChild( int index, BPTNode<K> childNode){
        this.childernList.add(index, childNode);
        this.childLength += 1;
    }

    @Override
    public int checkout() {
        if (keyLength < this.minNumber){
            return -1;
        } else if(keyLength > maxNumber) {
            return 1;
        }
        return 0;
    }

    public void checkLeafLink() {
        if(!isLeaf){
            BPTNode<K> LeafPrev = null;
            BPTNode<K> Leafnext = null;
        }
    }

    @Override
    public int keyLength() {
        return this.keyLength;
    }

    @Override
    public int childLength() {
        return this.childLength;
    }

    @Override
    public BPTNode<K> getFather() {
        return this.fatherNode;
    }

    @Override
    public void setIsLeaf(boolean bool) {
        this.isLeaf = bool;
    }

    @Override
    public BPTNode<K> getLeafPrev() {
        return leafPrev;
    }

    @Override
    public BPTNode<K> getLeafNext() {
        return leafnext;
    }

    @Override
    public void setLeafNext(BPTNode<K> next) {
        if(isLeaf) {
            leafnext = next;
        }
    }

    @Override
    public void setLeafPrev(BPTNode<K> prev) {
        if(isLeaf) {
            leafPrev = prev;
        }
    }


    @Override
    public int searchKey(BPTKey<K> key) {
        for(int i = 0; i < this.keyLength; i++){
            K listKey = this.keyList.get(i).key;
            K inputKey = key.key;
            if (inputKey.compareTo(listKey) == -1) {
                return i;
            } else if (inputKey == listKey){
                return i+1;
            }
        }
        return this.keyLength;
    }

    @Override
    public boolean isLeaf() {
        return this.isLeaf;
    }

    @Override
    public BPTKey<K> getKey(int index) {
        return this.keyList.get(index);
    }

    @Override
    public void deleteKey(int index) {
        this.keyList.remove(index);
        this.keyLength -= 1;
    }

    @Override
    public BPTNode<K> getChild(int index) {
        if (index > this.childLength-1) {
            return null;
        }
        return this.childernList.get(index);
    }

    @Override
    public BPTNode<K> deleteChild(int index) {
        BPTNode<K> node = null;
        if (index < this.childLength) {
            node = this.childernList.remove(index);
            this.childLength -= 1;
        }
        return node;
    }

    @Override
    public void setFather(BPTNode<K> father) {
        this.fatherNode = father;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node Key: ");
        for (BPTKey<K> key: this.keyList) {
            sb.append(key.key.toString()).append(" ");
        }
        return sb.toString();
    }
}
