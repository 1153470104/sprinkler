package dispatcher;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.BPlusTree;
import BPlusTree.keyType.MortonCode;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 *
 * use to simulate the dispatch process of the system
 * the schema of partition is maintained by this
 *
 * the formal edition of dataTool
 */
public class dispatcher {
    private String dataPath;
    private BufferedReader buffer;
    private MortonCode maxKey;
    private MortonCode minKey;
    private entry tempEntry;
    private int tempEntryId;
    private List<MortonCode> schema;  // the schema store the boundary of the schema
    private int indexNum;
    private BPlusTree[] treeList;
    private int currentNum;

    private JTextArea dataArea;
    private JTextArea statusArea;

     //使用最暴力的方式，留存最近的内容，然后直接求partition
    private Queue<MortonCode> cacheQueue;
    private int cacheLimit;
    private double loadGapLimit = 0.2;

    private int time; //time is set while retrieving the morton code

    /**
     * the inner class entry
     * used to pass the content of index data
     */
    public static class entry {
        /* with a big bug
         * first use morton code instead of BPTKey
         * which ignore the truth that it's BPTValueKey instead of blank key!!!
         * which lead to cast failure ... take care of generic
         */
        public BPTKey<MortonCode> key;
        public int time;
        public entry(BPTKey<MortonCode> key, int time) {
            this.key = key;
            this.time = time;
        }
    }

    /**
     * the initiation for gui
     * @param dataPath the data storage path
     * @param indexNum the index number
     * @param cacheLimit the limit of cached entries
     * @param dataArea the gui data area
     * @param statusArea the gui status area
     * @throws IOException thrown when any I/O operation fails
     */
    public dispatcher(String dataPath, int indexNum, int cacheLimit, JTextArea dataArea, JTextArea statusArea) throws IOException {
        this.dataArea = dataArea;
        this.statusArea = statusArea;
        this.dataPath = dataPath;
        this.indexNum = indexNum;
        this.tempEntryId = -1;
//        this.getDomain();
        buffer = new BufferedReader(new FileReader(dataPath));
        this.cacheLimit = cacheLimit;
        this.cacheQueue = new LinkedList<>();
        this.treeList = new BPlusTree[indexNum];
        initSchema();  // set the schema while initiating
    }

    /**
     * normal initiation of dispatcher
     * @param dataPath the data storage path
     * @param indexNum the index number
     * @param cacheLimit the limit of cached entries
     * @throws IOException thrown when any I/O operation fails
     *
     * the data source must be multi file, so
     */
    public dispatcher(String dataPath, int indexNum, int cacheLimit) throws IOException {
        this.currentNum = 0;
        this.dataArea = null;
        this.statusArea = null;
        this.dataPath = dataPath;
        this.indexNum = indexNum;
        this.tempEntryId = -1;
        this.getDomain();  // TODO with big data, you can not get a clear view of data
        buffer = new BufferedReader(new FileReader(dataPath + "s" + Integer.toString(currentNum)+".txt"));
        this.cacheLimit = cacheLimit;
        this.cacheQueue = new LinkedList<>();
        this.treeList = new BPlusTree[indexNum];
        initSchema();  // set the schema while initiating
//        this.schema = new LinkedList<>();  // first initiate
    }

    /**
     * get current schema
     * the mode is:
     *  --- key_1 --- key_2 --- ... --- key_n ---
     * @return a string showing current schema
     */
    public String getSchema() {
        StringBuilder ss = new StringBuilder();
        ss.append("---");
        for(MortonCode code: schema) {
            ss.append(code.toString()).append("---");
        }
        return ss.toString();
    }

    /**
     * print the current schema
     */
    public void printSchema() {
        System.out.print("---");
        for(MortonCode code: schema) {
            System.out.print(code.toString() + "---");
        }
        System.out.println();
    }

    /**
     * add current code into cache queue
     * remove the one exceeding the limit boundary
     * @param code the current MortonCode need to be cached
     */
    public void updateQueue(MortonCode code) {
        this.cacheQueue.add(code);
        if(this.cacheQueue.size() > cacheLimit) {
            cacheQueue.remove();
        }
    }

    /**
     * update the corresponding tree by id
     * @param tree the new index tree
     * @param id the id of the tree
     */
    public void updateTree(BPlusTree tree, int id) {
        this.treeList[id]= tree;
        updateTreeSchema();
    }

    /**
     * change the schema of the tree
     */
    public void updateTreeSchema() {
        for(int i = 0; i < schema.size(); i++) {
            if(this.treeList[i] != null) {
                if(i == 0){
                    treeList[i].setKeyBound(null, schema.get(i));
                } else {
                    treeList[i].setKeyBound(schema.get(i-1), schema.get(i));
                }
            }
        }
        if(treeList[schema.size()] != null) {
            treeList[schema.size()].setKeyBound(schema.get(schema.size()-1), null);
        }
    }

    /**
     * initiate the schema using the
     * @throws IOException be thrown when any I/O function fails
     */
    public void initSchema() throws IOException {
        BufferedReader bf = new BufferedReader(
                new FileReader(dataPath + "s" + Integer.toString(currentNum)+".txt"));
        maxKey = null;
        minKey = null;

        int limit = 0;
        String line = bf.readLine();
        while(limit < this.cacheLimit) {
            StringTokenizer st = new StringTokenizer(line, "|");
            st.nextToken(); /*有必要的，因为nextToken是一个个读的，coordTxt要基于前面的读取*/
            String coordTxt = st.nextToken();
            MortonCode mc = new MortonCode(coordTxt);

            this.cacheQueue.add(mc);
            line = bf.readLine();
            limit++;
        }
        balanceSchema(true);
        this.cacheQueue = new LinkedList<>(); // renew the cache queue
        bf.close();
}

    /**
     * calculate if the current schema is balanced according to the schema
     * @return a boolean of if current load is balanced
     */
    public boolean loadBalance() {
        int[] freq = new int[indexNum];
        List<MortonCode> curList = new LinkedList<>(cacheQueue);
        for(MortonCode code: curList) {
            for(int i = 0; i < schema.size(); i++) {
                if(code.compareTo(schema.get(i)) == -1) {
                    freq[i] += 1;
                    break;
                }
            }
            freq[schema.size()] += 1;
        }
        int max = freq[0];
        int min = freq[0];
        for(int i = 1; i < indexNum; i++) {
            if(freq[i]<min) min = freq[i];
            if(freq[i]>max) max = freq[i];
        }
        if((double)(max-min)/(double)min > loadGapLimit) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * use to alternate schema with the frequency of schema
     * TODO need to implement some new data structure to store the frequency info
     * TODO 具体的边界是不要，还是固定的，先不确定，未来做！
     * 目前是只分分界点，没有指定边界点！
     * @param force the boolean to show if need to force renew the schema
     */
    public void balanceSchema(boolean force) {
        // 以下的实现，是以schema 没有边界，只有分点来实现的
        //如果数量不够，不更新边界
        if (cacheQueue.size() < cacheLimit) return;
        List<MortonCode> newSchema = new LinkedList<>();
        //如果现阶段的 work load 差距不大，也不更新
        //如果work load 相差很大，更新！
        if(!force && loadBalance())  return;

        // 用indexNum-1个最大堆，找出所有分界值。。。
        // get a copy of cache queue
        /*下面的以cacheQueue给newQueue赋值的时候有时候会出现溢出的问题，就离谱
         * 尚未解决！！！TODO，只能说未来可以直接把这个计算schema的方式替换掉 */
        Queue<MortonCode> newQueue = null;
        try {
             newQueue = new LinkedList<>(cacheQueue);
        } catch(ArrayIndexOutOfBoundsException e) {
            System.out.println(cacheQueue.size());
            System.exit(-1);
        }
        Queue<MortonCode> tempQueue = new LinkedList<>();
        int heapLength = cacheLimit / indexNum;
        for(int i = 0; i < indexNum-1; i++) {
            List<MortonCode> maxHeap = new LinkedList<>();
            while(newQueue.size() > 0) {
                MortonCode temp = newQueue.poll();
//                System.out.println(temp);
                /* a big bug! size is not serial num! it shows the real length
                * mis-write to '...< heapLength-1' at first */
                if(maxHeap.size() < heapLength) {
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
        if(statusArea == null){
            System.out.print("current schema: ");
            printSchema();
        } else {
            statusArea.append("current schema: ");
            statusArea.append(getSchema());
            statusArea.append("\n");
        }
        updateTreeSchema(); // after change schema, change the tree schema as well
    }

    public void addHeap(List<MortonCode> heap, MortonCode code) {
        heap.add(code);
        int index = heap.size()-1;
        while(index > 0) {
//            System.out.println("index: " + index);
            int father = (index-1) / 2;
            if(heap.get(index).compareTo(heap.get(father)) == 1) {
                MortonCode temp = heap.get(index);
                MortonCode tempBig = heap.get(father);
                /* the most silly error...
                 * forgot to write add(index, content), only write add(content)
                 * make the max heap result strange...*/
                heap.remove(index); heap.add(index, tempBig);
                heap.remove(father); heap.add(father, temp);
                index = father;
            } else {
                break;
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
                heap.remove(index); heap.add(index, tempBig);
                heap.remove(bigIndex); heap.add(bigIndex, temp);
                index = bigIndex;
            } else {
                break;
            }
        }
    }

    /**
     * use to decide which index server should get tempEntry
     * according to the schema of partition
     * @param code the morton code which need to be located
     * @return the id of corresponding index server's id
     *         which is start from 0 !!!
     */
    private int searchId(MortonCode code) {
        for(int i = 0; i < schema.size(); i++) {
            if(code.compareTo(schema.get(i)) == -1) {
                return i;
            }
        }
        return schema.size();
    }

    /**
     * the function use to return a entry according to the id
     * if there's no cache, read one from file, then call self
     * if cache id suit, return cache entry; if not, return null
     * @param id the index id
     * @return corresponding entry or null
     * @throws IOException thrown when an I/O operation fails
     */
    public synchronized entry getEntry(int id) throws IOException, InterruptedException {
//        System.out.println("current id " + tempEntryId + " input id " + id);
        Thread.sleep(0);
        if(tempEntryId == id) {
            tempEntryId = -1;
//            System.out.println(tempEntry.key.key());
//            System.out.println("temp id: "+ tempEntryId);
            return tempEntry;
        } else if(tempEntryId == -1) {
//            System.out.println("-1 temp id: "+ tempEntryId);
            String line = buffer.readLine();
            if(dataArea == null){
//                System.out.println(line);
            } else {
                dataArea.insert(line+"\n", 0);
            }
            if(line != null) {
                tempEntry = new entry(getMortonCode(line), time);
                tempEntryId = searchId(tempEntry.key.key());
                updateQueue(tempEntry.key.key());  // update queue
//                System.out.println();
                return getEntry(id);  //做完了所有准备则
            } else {
                if(currentNum < 57) {  //这里是一个特殊处理！！！我知道只有0-57这些文件，如果原始数据组织发生变化，一定需要改。
                    // if current file is meeting end, open the exceeding file
                    this.currentNum += 1;
                    buffer = new BufferedReader(new FileReader(dataPath + "s" + Integer.toString(currentNum)+".txt"));
                    line = buffer.readLine();
                    tempEntry = new entry(getMortonCode(line), time);
                    tempEntryId = searchId(tempEntry.key.key());
                    updateQueue(tempEntry.key.key());  // update queue
                    return getEntry(id);  //做完了所有准备则
                } else {
                    System.out.println("input over!!!");
                    System.exit(0);
                }
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
        BufferedReader bf = new BufferedReader(
                new FileReader(dataPath + "s" + Integer.toString(currentNum)+".txt"));
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
        if(statusArea == null){
            System.out.println("domain from: " + minKey.toString() + " - " + maxKey.toString());
        } else {
            statusArea.append("domain from: " + minKey.toString() + " - " + maxKey.toString() + "\n");
        }
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
