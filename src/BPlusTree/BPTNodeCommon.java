package BPlusTree;

import javax.xml.stream.FactoryConfigurationError;
import java.util.ArrayList;
import java.util.List;

public class BPTNodeCommon implements BPTNode{
    protected int m;
    protected int maxNumber;
    protected int minNumber;
    protected int keyLength;
    protected int childLength;
    protected List<BPTKey<Integer>> keyList;
    protected List<BPTNode> childernList;
    private BPTNonLeaf fatherNode;
    protected boolean isLeaf;

    public BPTNodeCommon(int m, BPTNonLeaf fatherNode){
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
    public int insertKey(int index, BPTKey<Integer> key) {
        this.keyList.add(index, key);
        this.keyLength += 1;
        return this.checkout();
    }

    @Override
    public void addChild(BPTNode child) {
        this.childernList.add(child);
        this.childLength += 1;
    }

    public void insertChild( int index, BPTNode childNode){
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

    @Override
    public int keyLength() {
        return this.keyLength;
    }

    @Override
    public int childLength() {
        return this.childLength;
    }

    @Override
    public BPTNonLeaf getFather() {
        return this.fatherNode;
    }

    @Override
    public void setIsLeaf(boolean bool) {
        this.isLeaf = bool;
    }

    @Override
    public int searchKey(BPTKey<Integer> key) {
        for(int i = 0; i < this.keyLength; i++){
            int listKey = this.keyList.get(i).key;
            int inputKey = key.key;
            if (inputKey < listKey) {
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
    public BPTKey<Integer> getKey(int index) {
        return this.keyList.get(index);
    }

    @Override
    public void deleteKey(int index) {
        this.keyList.remove(index);
        this.keyLength -= 1;
    }

    @Override
    public BPTNode getChild(int index) {
        return this.childernList.get(index);
    }

    @Override
    public BPTNode deleteChild(int index) {
        BPTNode node = null;
        if (index < this.childLength) {
            node = this.childernList.remove(index);
            this.childLength -= 1;
        }
        return node;
    }

    @Override
    public void setFather(BPTNonLeaf father) {
        this.fatherNode = father;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node Key: ");
        for (BPTKey<Integer> key: this.keyList) {
            sb.append(key.key.toString()).append(" ");
        }
        return sb.toString();
    }
}
