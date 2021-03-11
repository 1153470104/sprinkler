package BPlusTree.BPTNode;

import BPlusTree.configuration.externalConfiguration;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * class of non leaf external node
 *
 */
public class externalNonLeaf<K extends Comparable> extends externalNode<K>{
    private List<Long> pointerList;

    public externalNonLeaf(BPTNode<K> node) {
        super(node);
        pointerList = new ArrayList<>();
    }

     /**
     * write node of non leaf node into the tree file
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
        for(int i = 0; i < this.length; i++) {
            bbuffer.putLong(this.pointerList.get(i));   // Pointer
            bbuffer.put(this.keyList.get(i)); //不知道对不对
        }
        bbuffer.putLong(this.pointerList.get(this.length));   // Pointer
        r.write(buffer);
    }

    /**
     * append a pointer to point list of this external node
     * @param point
     */
    public void addPointer(long point) {
        pointerList.add(point);
    }
}
