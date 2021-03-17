package BPlusTree.BPTNode;

import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.configuration.externalConfiguration;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * class of external leaf
 * used to store temporary external leaf node for writing & reading
 */
public class externalLeaf<K extends Comparable> extends externalNode<K>{
    private List<Object> valueList;

    private long prevLeaf;
    private long nextLeaf;

    public externalLeaf(BPTNode<K> node) {
        super(node);
        valueList = new ArrayList<>();
        for(int i = 0; i < keyList.size(); i++) {
            valueList.add(((BPTValueKey<K, Object>)keyList.get(i)).getValue());
        }
    }

    /**
     * init of a external leaf node
     * @param nodeType the type short num of the external node
     * @param length the capacity of the external node
     * @param pageIndex the page index of the node
     * @param prevLeaf the prev leaf of the node
     * @param nextLeaf the next leaf of the node
     */
    public externalLeaf(short nodeType, int length, long pageIndex, long prevLeaf, long nextLeaf) {
        super(nodeType, length, pageIndex);
        this.prevLeaf = prevLeaf;
        this.nextLeaf = nextLeaf;
    }

    /**
     * write node of leaf node into the tree file
     */
    @Override
    public void writeNode(RandomAccessFile r, externalConfiguration conf) throws IOException {
        super.writeNode(r, conf);
        r.seek(this.pageIndex);
        byte[] buffer = new byte[conf.pageSize];
        ByteBuffer bbuffer = ByteBuffer.wrap(buffer); bbuffer.order(ByteOrder.BIG_ENDIAN);
        // write header info
        bbuffer.putShort(this.nodeType);
        bbuffer.putInt(this.length);
        bbuffer.putLong(this.prevLeaf);
        bbuffer.putLong(this.nextLeaf);
        for(int i = 0; i < this.length; i++) {
            // use conf to write in data
            conf.writeKey(bbuffer, this.keyList.get(i).key());
            conf.writeValue(bbuffer, this.valueList.get(i));
        }
        r.write(buffer);
    }


    /**
     * setter of prev leaf node index
     * prev & next node's setting rely on the outer function
     * @param prevLeaf the prevLeaf index
     */
    public void setPrevLeaf(long prevLeaf) {
        this.prevLeaf = prevLeaf;
    }

    /**
     * setter of next leaf node index
     * @param nextLeaf the next leaf index
     */
    public void setNextLeaf(long nextLeaf) {
        this.nextLeaf = nextLeaf;
    }

    /**
     * leaf node add key-value pair
     * @param key the key of BPTKey
     * @param value the value of BPTKey
     */
    public void addKey(K key, Integer value) {
        // TODO a big mistake !!! generic fails here
        //  if i want to simplify, i have to add all V once I left
        this.keyList.add(new BPTValueKey<>(key, value));
    }

    /**
     * leaf node version of toString
     * @return string of leaf node
     */
    @Override
    public String toString() {
        String common = super.toString();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < length-1; i++) {
            stringBuilder.append(keyList.get(i)).append(":").append(valueList.get(i)).append("|");
        }
        stringBuilder.append(keyList.get(length-1)).append(":").append(valueList.get(length-1));
        return common+stringBuilder.toString();
    }
}
