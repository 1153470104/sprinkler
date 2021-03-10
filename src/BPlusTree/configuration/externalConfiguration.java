package BPlusTree.configuration;

/**
 *
 * class that store all configuration parameters for external B+ tree
 *
 * all the size are in bytes
 *
 */
public class externalConfiguration {
    public int headerSize;              // header size
    public int pageSize;                // page size
    public int nonLeafHeaderSize;       // non leaf node header size
    public int leafHeaderSize;          // leaf node header size

    public int keySize;                 // key's size
    public int valueSize;               // value's size

    public externalConfiguration(int keySize, int valueSize) {
        this.headerSize = (Integer.SIZE * 3 + Long.SIZE) / 8;          // header size in bytes
        this.nonLeafHeaderSize = (Short.SIZE + Integer.SIZE) / 8; // 22 bytes
        this.leafHeaderSize = (Short.SIZE + 2 * Long.SIZE + Integer.SIZE) / 8; // 22 bytes

        //assign key-value pair's size
        this.keySize = keySize;
        this.valueSize = valueSize;
    }

}
