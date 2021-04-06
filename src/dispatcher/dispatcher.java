package dispatcher;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.keyType.MortonCode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

/**
 *
 * use to
 * the formal edition of dataTool
 */
public class dispatcher {
    private String dataPath;
    private BufferedReader buffer;
    private MortonCode maxKey;
    private MortonCode minKey;
    private BPTKey<MortonCode> tempEntry;
    private int tempEntryId;
    private List<MortonCode> schema;  // the schema store the boundary of the schema
    private int indexNum;

     //使用最暴力的方式，留存最近的内容，然后直接求partition
    private Queue<MortonCode> cacheQueue;
    private int cacheLimit;
    private double loadGapLimit = 0.2;

    private int time;

    public dispatcher(String dataPath, int indexNum, int cacheLimit) throws IOException {
        this.dataPath = dataPath;
        this.indexNum = indexNum;
        this.tempEntryId = -1;
        this.getDomain();
        buffer = new BufferedReader(new FileReader(dataPath));
        this.cacheLimit = cacheLimit;
    }

    public void updateQueue(MortonCode code) {
        this.cacheQueue.add(code);
        if(this.cacheQueue.size() > cacheLimit) {
            cacheQueue.remove();
        }
    }

    public void setSchema(List<MortonCode> schema) {
        this.schema = schema;
    }

    /**
     * use to alternate schema with the frequency of schema
     * TODO need to implement some new data structure to store the frequency info
     * TODO 具体的边界是不要，还是固定的，先不确定，未来做！
     */
    public void balanceSchema() {
        // 以下的实现，是以schema 没有边界，只有分点来实现的
        //如果数量不够，不更新边界
        if (cacheQueue.size() < cacheLimit) return;
        List<MortonCode> newSchema = new LinkedList<>();
        //如果现阶段的 work load 差距不大，也不更新
        //TODO
        //如果work load 相差很大，更新！
        // TODO 用indexNum-1个最大堆，找出所有分界值。。。
        // get a copy of cache queue
        Queue<MortonCode> newQueue = new LinkedList<>(cacheQueue);
        Queue<MortonCode> tempQueue = new LinkedList<>();
        int heapLength = cacheLimit / indexNum;
        for(int i = 0; i < indexNum-1; i++) {
            List<MortonCode> maxHeap = new LinkedList<>();
            while(newQueue.size() > 0) {
                MortonCode temp = newQueue.poll();
                if(maxHeap.size() < heapLength-1) {
                    addHeap(maxHeap, temp);
                } else if(temp.compareTo(maxHeap.get(0)) == -1) {
                    tempQueue.add(maxHeap.remove(0));
                    maxHeap.add(0, temp);
                    updateHeap(maxHeap, heapLength);
                } else {
                    tempQueue.add(temp);
                }
            }
            newSchema.add(maxHeap.get(0));
            newQueue = tempQueue;
            tempQueue = new LinkedList<>();
        }
        this.schema = newSchema;
    }

    public void addHeap(List<MortonCode> heap, MortonCode code) {
        int index = heap.size()-1;
        while(index > 0) {
            int father = (index-1) / 2;
            if(heap.get(index).compareTo(heap.get(father)) == 1) {
                MortonCode temp = heap.get(index);
                MortonCode tempBig = heap.get(father);
                heap.remove(index); heap.add(tempBig);
                heap.remove(father); heap.add(temp);
                index = father;
            }
        }
    }

    public void updateHeap(List<MortonCode> heap, int maxLength) {
        int index = 0;
        while(2*index+1 < maxLength) { // 以是否到了叶节点层为判断，来终止循环
            int bigIndex;
            if(2*index+2 >= maxLength) {
                bigIndex = 2*index+1;
            } else {
                if (heap.get(2 * index + 2).compareTo(heap.get(2 * index + 1)) == -1) {
                    bigIndex = 2 * index + 1;
                } else {
                    bigIndex = 2 * index + 2;
                }
            }
            if(heap.get(index).compareTo(heap.get(bigIndex)) == -1) {
                // swap the MortonCode
                MortonCode temp = heap.get(index);
                MortonCode tempBig = heap.get(bigIndex);
                heap.remove(index); heap.add(tempBig);
                heap.remove(bigIndex); heap.add(temp);
                index = bigIndex;
            } else {
                break;
            }
        }
    }

    /**
     * use to decide which index server should get tempEntry
     * according to the schema of partition
     * @param code the morton code BPTKey which need to be located
     * @return the id of corresponding index server's id
     *         which is start from 0 !!!
     */
    private int searchId(BPTKey<MortonCode> code) {
        return -1;
    }

    /**
     * the function use to return a entry according to the id
     * if there's no cache, read one from file, then call self
     * if cache id suit, return cache entry; if not, return null
     * @param id the index id
     * @return corresponding entry or null
     * @throws IOException thrown when an I/O operation fails
     */
    public BPTKey<MortonCode> getEntry(int id) throws IOException {
        if(tempEntryId == id) {
            tempEntryId = -1;
            return tempEntry;
        } else if(tempEntryId == -1) {
            String line = buffer.readLine();
            if(line != null) {
                tempEntry = getMortonCode(line);
                updateQueue(tempEntry.key());  // update queue
                tempEntryId = searchId(tempEntry);
                return getEntry(id);  //做完了所有准备则
            }
        } else {
            return null;
        }
        return null;
    }

    /**
     * function to get the domain boundary of simulation data
     * @throws IOException is thrown when an I/O operation fails
     */
    public void getDomain() throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(dataPath));
        maxKey = null;
        minKey = null;

        String line = bf.readLine();
        while(line != null) {
            StringTokenizer st = new StringTokenizer(line, "|");
            st.nextToken(); /*有必要的，因为nextToken是一个个读的，coordTxt要基于前面的读取*/
            String coordTxt = st.nextToken();
            MortonCode mc = new MortonCode(coordTxt);
            if(maxKey == null || mc.compareTo(maxKey) == 1) {
                maxKey = mc;
            }
            if(minKey == null || mc.compareTo(minKey) == -1) {
                minKey = mc;
            }
            line = bf.readLine();
        }
        System.out.println("domain from: " + minKey.toString() + " - " + maxKey.toString());
        bf.close();
    }

    /**
     * function to transform a line of text into a Morton Code
     * @param line a plain line of data
     *             e.g. '1372636935|-8.62065,41.148513|20000233,C'
     * @return the morton code built with the line's data
     */
    public BPTKey<MortonCode> getMortonCode(String line) {
        StringTokenizer st = new StringTokenizer(line, "|");
        String timestamp = st.nextToken();
        this.time = Integer.valueOf(timestamp);
        String coordTxt = st.nextToken();
        String otherData = st.nextToken();

        //wrangle data
        MortonCode key = new MortonCode(coordTxt);
        String value = timestamp + "," + otherData;
        BPTKey<MortonCode> bptKey = new BPTValueKey<>(key, value);
        return bptKey;
    }

    /**
     * a function to judge if key is in the time domain
     * @param key the key to be judged
     * @param timeStart the start time
     * @param timeEnd the end time
     * @return a boolean of if it's in the time domain
     */
    public static boolean inTimeDomain(BPTValueKey<MortonCode, String> key, int timeStart, int timeEnd) {
        StringTokenizer st = new StringTokenizer(key.getValue(), ",");
        int timestamp = Integer.parseInt(st.nextToken());
        if(timestamp >= timeStart && timestamp <= timeEnd) {
            return true;
        } else {
            return false;
        }
    }


    public static String listToString(List<BPTKey<MortonCode>> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for(BPTKey<MortonCode> k: list) {
            stringBuilder.append(k.key()).append(":");
            stringBuilder.append(((BPTValueKey<MortonCode, String>)k).getValue()).append("\n");
        }
        stringBuilder.append("Total Entry Num: ").append(list.size());
        return stringBuilder.toString();
    }

//    public List<BPTKey> entryList() throws IOException {
//        List<BPTKey> list = new ArrayList<>();
//        BufferedReader in = new BufferedReader(new FileReader(dataPath));
//        String line;
//        while ((line = in.readLine()) != null) {
//            BPTKey<MortonCode> bptKey = getMortonCode(line);
////            System.out.println(bptKey);
//            list.add(bptKey);
//        }
//        return list;
//    }

    /**
     * getter of current time
     * @return current time
     */
    public int getTime() {
        return time;
    }

    //temporary simple test of dataTool
    public static void main(String[] args) throws IOException {
        dataTool dt = new dataTool("resource/data/100000s.txt");
        while(true){
            BPTValueKey<MortonCode, String> k = (BPTValueKey<MortonCode, String>)dt.getEntry();
            System.out.print(k.getValue() + ", ");
            System.out.println(k.key());
        }
    }
}
