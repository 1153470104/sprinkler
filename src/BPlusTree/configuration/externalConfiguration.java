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

        this.pageSize = 1024; // default pageSize, 1024

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
     * read key according to its type
     * @param bbuffer the buffer to read data
     * @return the key read from byte buffer
     */
    public Object readKey(ByteBuffer bbuffer) {
        if(keyType == Integer.class) {
            return bbuffer.getInt();
        } else if (keyType == Long.class) {
            return bbuffer.getLong();
        }
        return null;
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

    /**
     * read value according to its type
     * @param bbuffer the buffer to read data
     * @return the value read from byte buffer
     */
    public Object readValue(ByteBuffer bbuffer) {
        if(keyType == String.class) {
            // use get() to get string bytes
            byte[] buffer = new byte[valueSize];
            bbuffer.get(buffer, 0, valueSize);
            return new String(buffer, StandardCharsets.UTF_8);
        }else if(keyType == Integer.class) {
            return bbuffer.getInt();
        }
        return null;
    }
}
