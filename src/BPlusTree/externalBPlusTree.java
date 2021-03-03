package BPlusTree;

public class externalBPlusTree<K extends Comparable> {
    private String timeStart;
    private String timeEnd;
    private K keyStart;
    private K keyEnd;

    public externalBPlusTree(BPlusTree<K> tree) {
        this.timeStart = ((BPlusTreeCommon<K>)tree).getTimeStart();
        this.timeEnd = ((BPlusTreeCommon<K>)tree).getTimeEnd();
        this.keyStart = ((BPlusTreeCommon<K>)tree).getKeyStart();
        this.keyEnd = ((BPlusTreeCommon<K>)tree).getKeyEnd();
    }
}
