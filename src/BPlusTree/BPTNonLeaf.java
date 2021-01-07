package BPlusTree;

import java.util.ArrayList;

public class BPTNonLeaf extends BPTNodeCommon{

    public BPTNonLeaf(int m, BPTNonLeaf fatherNode) {
        super(m, fatherNode);
        this.isLeaf = false;
    }


}
