package BPlusTree.configuration;

import BPlusTree.keyType.MortonCode;

import java.lang.reflect.Type;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 *
 * class that store all configuration parameters for external B+ tree
 *
 * all the size are in bytes
 *
 */
public class configuration {
    public int headerSize;              // header size
    public int pageSize;                // page size
    public int nonLeafHeaderSize;       // non leaf node header size
    public int leafHeaderSize;          // leaf node header size

    public int m;
    public double skewness;
    public double loadGaplimit;
    public int chunkSize;

    public Type keyType;
    public Type valueType;
    public int keySize;                 // key's size
    public int valueSize;               // value's size

    public int gap;                     // bloom filter's time gap
    public int slot;                    // slots' number

    public configuration(int keySize, int valueSize, Type keyType, Type valueType) {
        this.headerSize = (Integer.SIZE * 3 + Long.SIZE) / 8;          // header size in bytes
        this.nonLeafHeaderSize = (Short.SIZE + Integer.SIZE) / 8; // 22 bytes
        this.leafHeaderSize = (Short.SIZE + 2 * Long.SIZE + Integer.SIZE) / 8; // 22 bytes

        this.pageSize = 4096; // default pageSize, 1024 B
        this.m = 20; // tree's m
        this.skewness = 0.2; // template tree's skewness limit
        this.loadGaplimit = 0.2; // dispatcher's index server load gap limit
        this.chunkSize = 4 * 1024 * 1024; // default chunk size 4 MB

        this.gap = 15; // default time gap 15s, means entries in 15s map into same bit
        this.slot = 203; // the default slot num 203

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
            // TODO a stupid realization, use some specific manipulation
            // TODO to make up the previews design fault, just like no design
            long realKey =((MortonCode)key).getCode();
            try {
                bbuffer.putLong(realKey);
            }catch (BufferOverflowException e) {
                System.out.println("key exception: " + key);
            }
        }
    }

    /**
     * read key according to its type
     * @param bbuffer the buffer to read data
     * @return the key read from byte buffer
     */
    public Object readKey(ByteBuffer bbuffer) {
//        System.out.println("start conf readkey");
        if(keyType == Integer.class) {
            return bbuffer.getInt();
        } else if (keyType == Long.class) {
            // TODO same stupid adaption as writeKey before
            long temp = bbuffer.getLong();
//            System.out.println("the read key part"+temp);
            return new MortonCode(temp);
        }
//        System.out.println("end conf readkey");
        return null;
    }

    /**
     * write key in byte buffer according to the type of the key
     * @param bbuffer byte buffer to write in
     * @param value the value to be written in buffer
     */
    public void writeValue(ByteBuffer bbuffer, Object value){
        if(valueType == String.class) {
            try {
                bbuffer.put(((String) value).getBytes(StandardCharsets.UTF_8));
            } catch (BufferOverflowException e) {
                System.out.println("value exception: " + value);
            }
        }else if(valueType == Integer.class) {
            bbuffer.putInt((int)value);
        }
    }

    /**
     * read value according to its type
     * @param bbuffer the buffer to read data
     * @return the value read from byte buffer
     */
    public Object readValue(ByteBuffer bbuffer) {
        if(valueType == String.class) {
            // use get() to get string bytes
            byte[] buffer = new byte[valueSize];
            bbuffer.get(buffer, 0, valueSize);
            return new String(buffer, StandardCharsets.UTF_8);
        }else if(valueType == Integer.class) {
            return bbuffer.getInt();
        }
        return null;
    }
}
