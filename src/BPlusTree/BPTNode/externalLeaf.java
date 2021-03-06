package BPlusTree.BPTNode;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.bloomFilter;
import BPlusTree.configuration.configuration;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
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
        valueList = new LinkedList<>();
        for (BPTKey<K> kbptKey : keyList) {
            Object value = ((BPTValueKey<K, Object>) kbptKey).getValue();
            valueList.add(value);
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
        valueList = new LinkedList<>();
    }

    /**
     * write node of leaf node into the tree file
     */
    public void writeNode(RandomAccessFile r, configuration conf, bloomFilter bf) throws IOException {
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
            try {
                int time = Integer.parseInt((((String) this.valueList.get(i)).substring(0, 10)));
                bf.addMap(time);
            } catch (StringIndexOutOfBoundsException | ClassCastException e) {
//                continue; /*这个continue可害惨了呀，continue不是啥都不做而是直接进下一个循环了*/
            }
            conf.writeValue(bbuffer, this.valueList.get(i));
        }
        r.write(buffer);
    }

    /**
     * write node of leaf node into the tree file
     */
    @Override
    public void writeNode(RandomAccessFile r, configuration conf) throws IOException {
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

    public BPTValueKey<K, Object> getKey(int index) {
        return new BPTValueKey<K, Object>(this.keyList.get(index).key(), this.valueList.get(index));
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
    public void addKey(K key, Object value) {
        // TODO a big mistake !!! generic fails here
        //  if I want to simplify, I have to add all V once I left
        //  the thing is if I need to generic, all the system should change
        this.keyList.add(new BPTKey<>(key));
        this.valueList.add(value);
    }

    /**
     * the leaf node search key
     * @param key the key to search
     * @return -1 if key less than any key in leaf
     *         else return the index of key
     *         or the index of leaf-key which just bigger than key
     *         else the length of keyList when key bigger than any key in leaf
     */
    @Override
    public int searchKey(K key) {
        int len = keyList.size();
        if(key.compareTo(keyList.get(0).key()) == -1) {
            return -1;
        }
        for(int i = 0; i < len; i++) {
            if(key.compareTo(keyList.get(i).key()) == -1 || key.compareTo(keyList.get(i).key()) == 0) {
                return i;
            }
        }
        return len;
    }

    /**
     * getter of prev page index
     * @return the prevLeaf's pageIndex
     */
    public long getPrevLeaf() {
        return prevLeaf;
    }

    /**
     * getter of next page index
     * @return the nextLeaf's pageIndex
     */
    public long getNextLeaf() {
        return nextLeaf;
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
            stringBuilder.append(keyList.get(i).key()).append(":").append(valueList.get(i)).append("|");
        }
        stringBuilder.append(keyList.get(length-1).key()).append(":").append(valueList.get(length-1));
        return common+stringBuilder.toString();
    }
}
