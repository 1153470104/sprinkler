package BPlusTree.configuration;

public class externalConfiguration {
    public int headerSize;
    public int pageSize;
    public int nonLeafHeaderSize;
    public int leafHeaderSize;

    public int keySize;
    public int valueSize;

    public externalConfiguration(int keySize, int valueSize) {
        this.headerSize = (Integer.SIZE * 3 + Long.SIZE) / 8;          // header size in bytes
        this.nonLeafHeaderSize = (Short.SIZE + Integer.SIZE) / 8; // 22 bytes
        this.leafHeaderSize = (Short.SIZE + 2 * Long.SIZE + Integer.SIZE) / 8; // 22 bytes

        this.keySize = keySize;
        this.valueSize = valueSize;
    }

}
