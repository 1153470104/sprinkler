package BPlusTree.configuration;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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

    public Type keyType;
    public Type valueType;
    public int keySize;                 // key's size
    public int valueSize;               // value's size

    public externalConfiguration(int keySize, int valueSize, Type keyType, Type valueType) {
        this.headerSize = (Integer.SIZE * 3 + Long.SIZE) / 8;          // header size in bytes
        this.nonLeafHeaderSize = (Short.SIZE + Integer.SIZE) / 8; // 22 bytes
        this.leafHeaderSize = (Short.SIZE + 2 * Long.SIZE + Integer.SIZE) / 8; // 22 bytes

        //assign key-value pair's size
        this.keySize = keySize;
        this.valueSize = valueSize;
        this.keyType = keyType;
        this.valueType = valueType;
    }

    /**
     * write key in byte buffer according to the type of the key
     * @param bbuffer byte buffer to write in
     * @param key the key to be written in buffer
     */
    public void writeKey(ByteBuffer bbuffer, Object key){
        if(keyType == Integer.class) {
            bbuffer.putInt((int)key);
        } else if (keyType == Long.class) {
            bbuffer.putLong((long)key);
        }
    }

    /**
     * write key in byte buffer according to the type of the key
     * @param bbuffer byte buffer to write in
     * @param value the value to be written in buffer
     */
    public void writeValue(ByteBuffer bbuffer, Object value){
        if(keyType == String.class) {
            bbuffer.put(((String) value).getBytes(StandardCharsets.UTF_8));
        }else if(keyType == Integer.class) {
            bbuffer.putInt((int)value);
        }
    }
}
