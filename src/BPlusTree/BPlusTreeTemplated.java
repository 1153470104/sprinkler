package BPlusTree;

import BPlusTree.BPTKey.BPTKey;

import java.util.List;

public class BPlusTreeTemplated<K extends Comparable> extends BPlusTreeCommon<K> {


    public BPlusTreeTemplated(BPlusTreeCommon<K> tree) {
        super(tree.m);
        this.onlyRoot = false;
        this.templateBased = true;
        this.root = tree.rootCopy();
    }


    @Override
    public void addKey(BPTKey<K> key) {
        super.addKey(key);
        //TODO
    }

    @Override
    public List<BPTKey<K>> search(K key1, K key2) {
        //TODO

        return null;
    }

    public void balance(){

    }
}
