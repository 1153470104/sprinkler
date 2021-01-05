package BPlusTree;

import java.util.ArrayList;

public class BPTNonLeaf extends BPTNodeCommon{

    public BPTNonLeaf(int m, BPTNode fatherNode) {
        super(m, fatherNode);
        this.childernList = new ArrayList<>();
        this.isLeaf = false;
    }

    public void insertChild( int index, BPTNode childNode){
        this.childernList.add(index, childNode);
        this.childLength += 1;
    }

    public BPTNode getChild(int index){
        return this.childernList.get(index);
    }
}
