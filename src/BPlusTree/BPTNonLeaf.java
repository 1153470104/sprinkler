package BPlusTree;

import java.util.ArrayList;

public class BPTNonLeaf extends BPTNodeCommon{

    public BPTNonLeaf(int m, BPTNode fatherNode) {
        super(m, fatherNode);
        this.childernList = new ArrayList<>();
    }

    public void addChild(BPTNode childNode){
        this.childernList.add(childNode);
        this.childLength += 1;
    }
}
