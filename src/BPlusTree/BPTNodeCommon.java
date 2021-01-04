package BPlusTree;

import java.util.ArrayList;
import java.util.List;

public class BPTNodeCommon implements BPTNode{
    protected int m;
    private int maxNumber;
    private int minNumber;
    private int keyLength;
    protected int childLength;
    protected List<BPTKey<String>> keyList;
    protected List<BPTNode> childernList;
    private BPTNode fatherNode;

    public BPTNodeCommon(int m, BPTNode fatherNode){
        this.m = m;
        this.maxNumber = m-1;
        this.minNumber = (int) (Math.ceil(m / 2.0) -1);
        this.keyLength = 0;
        this.childLength = 0;
        this.keyList = new ArrayList<>();
        this.fatherNode = fatherNode;
    }

    @Override
    public int addKey(BPTKey<String> key) {
        this.keyList.add(key);
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
}
