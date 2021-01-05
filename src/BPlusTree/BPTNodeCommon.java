package BPlusTree;

import javax.xml.stream.FactoryConfigurationError;
import java.util.ArrayList;
import java.util.List;

public class BPTNodeCommon implements BPTNode{
    protected int m;
    private int maxNumber;
    private int minNumber;
    private int keyLength;
    protected int childLength;
    protected List<BPTKey<Integer>> keyList;
    protected List<BPTNode> childernList;
    private BPTNode fatherNode;
    protected boolean isLeaf;

    public BPTNodeCommon(int m, BPTNode fatherNode){
        this.m = m;
        this.maxNumber = m-1;
        this.minNumber = (int) (Math.ceil(m / 2.0) -1);
        this.keyLength = 0;
        this.childLength = 0;
        this.keyList = new ArrayList<>();
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
    public BPTNode getFather() {
        return this.fatherNode;
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
    public BPTNode getChild(int index) {
        return null;
    }
}
