package BPlusTree;

import BPTKey.BPTKey;
import BPTNode.BPTLeaf;

import java.util.List;

public class BPlusTreeTemplated<K extends Comparable> extends BPlusTreeCommon<K> {


    public BPlusTreeTemplated(BPlusTreeScratched<K> tree) {
        super(tree.m);
        this.onlyRoot = false;
        this.templateBased = true;
    }

    @Override
    public void addKey(BPTKey<K> key) {
        //TODO
    }

    @Override
    public List<BPTKey<K>> search(K key1, K key2) {
        //TODO

        return null;
    }
}
