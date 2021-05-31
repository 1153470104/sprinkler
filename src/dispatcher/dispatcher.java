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
    private List<Long> schema;  // the schema store the boundary of the schema
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
    private Deque<hashCounter> schemaCounter;
    private int timeGap;  // the time gap to change another hash counter
    private int cacheSpan;  // the length of past time interval which determines the dispatch schema
    private int counterTime;
    private int schemaCodeGap;

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
    public dispatcher(String dataPath, int indexNum) throws IOException {
        this.currentNum = 0;
        this.dataArea = null;
        this.statusArea = null;
        this.dataPath = dataPath;
        this.indexNum = indexNum;
        this.tempEntryId = -1;
//        this.getDomain();  // TODO with big data, you can not get a clear view of data
        buffer = new BufferedReader(new FileReader(dataPath + "s" + Integer.toString(currentNum)+".txt"));
//        this.cacheLimit = cacheLimit;
//        this.cacheQueue = new LinkedList<>();
        this.treeList = new BPlusTree[indexNum];
        schema = new ArrayList<>();
        initSchema();  // set the schema while initiating
//        this.schema = new LinkedList<>();  // first initiate
        this.schemaCounter = new LinkedList<>();
        this.counterTime = -1;
        this.timeGap = 60;  // default time gap
        this.cacheSpan = 600;  // default cache span
        this.schemaCodeGap = 500000000;
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
        for(long code: schema) {
            ss.append(code).append("---");
        }
        return ss.toString();
    }

    /**
     * print the current schema
     */
    public void printSchema() {
        System.out.print("---");
        for(long code: schema) {
            System.out.print(code + "---");
        }
        System.out.println();
    }

    /**
     * add current code into cache queue
     * remove the one exceeding the limit boundary
     * @param code the current MortonCode need to be cached
     */
    public void updateQueue0(MortonCode code) {
        this.cacheQueue.add(code);
        if(this.cacheQueue.size() > cacheLimit) {
            cacheQueue.remove();
        }
    }

    public void updateQueue(long code, int time) {
        if (this.counterTime < 0 || this.counterTime + timeGap < time) {
            this.counterTime = time;
            schemaCounter.add(new hashCounter(schemaCodeGap, 203));
            this.balanceSchema(true);
            // remove the most previous hash counter
            if (schemaCounter.size() > this.cacheSpan / this.timeGap) {
                schemaCounter.removeFirst();
            }
        }
        this.schemaCounter.getLast().add(code);
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
                    /*下面因为key bound曾经出现一个bug，因为本该赋值MortonCode却赋值了long
                       其实keybound没有太大用？暴露了冗余的问题*/
                    treeList[i].setKeyBound(null, new MortonCode(schema.get(i)));
                } else {
                    treeList[i].setKeyBound(new MortonCode(schema.get(i-1)), new MortonCode(schema.get(i)));
                }
            }
        }
        if(treeList[schema.size()] != null) {
            treeList[schema.size()].setKeyBound(new MortonCode(schema.get(schema.size()-1)), null);
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
        while(limit < 1000) {
            StringTokenizer st = new StringTokenizer(line, "|");
            st.nextToken(); /*有必要的，因为nextToken是一个个读的，coordTxt要基于前面的读取*/
            String coordTxt = st.nextToken();
            MortonCode mc = new MortonCode(coordTxt);
            if (limit == 0) {
                maxKey = mc;
                minKey = mc;
            } else {
                if(mc.compareTo(maxKey) == 1) {
                    maxKey = mc;
                } else {
                    minKey = mc;
                }
            }
            line = bf.readLine();
            limit++;
        }
        /*乱七八糟的bug，为什么我会把下面几行代码放在while里面？？*/
        long schemaGap = (maxKey.getCode() - minKey.getCode()) / this.indexNum;
        for(int i = 1; i < indexNum; i++) {
            schema.add(minKey.getCode() + i * schemaGap);
        }
        bf.close();
}

    public boolean loadBalance() throws IOException {
        if(schema.size() == 0) {
            initSchema();
            return true;
        }
        int[] countArray = new int[this.indexNum];
        long codeStart = minKey.getCode();
        long codeEnd = schema.get(0);
        int i = -1;
        do {
            i = i+1;
            int count = 0;
            for(long j = codeStart; j < codeEnd; j += schemaCodeGap) {
                for(hashCounter counter: schemaCounter) {
                    count += counter.count(j);
                }
            }
            countArray[i] = count;
            if(i != schema.size()-1) {
                codeStart = schema.get(i);
                codeEnd = schema.get(i+1);
            } else {
                codeStart = schema.get(i);
                codeEnd = maxKey.getCode();
            }
        } while(i < schema.size()-1);

        int max = countArray[0];
        int min = countArray[0];
        for(i = 1; i < indexNum; i++) {
            if(countArray[i]<min) min = countArray[i];
            if(countArray[i]>max) max = countArray[i];
        }
        return !((double) (max - min) / (double) min > loadGapLimit);
    }

    public void balanceSchema(boolean force) {
        int sum = 0;
        List<Integer> countList = new ArrayList<>();
        for(long i = minKey.getCode(); i < maxKey.getCode(); i+=schemaCodeGap) {
            int count = 0;
            for(hashCounter counter: schemaCounter) {
                count += counter.count(i);
            }
            countList.add(count);
            sum += count;
        }
        List<Long> new_schema = new ArrayList<>();  // the schema store the boundary of the schema
        int average = sum / this.indexNum;
        int temp = 0;
        for(int i = 0; i < (maxKey.getCode()-minKey.getCode()) / schemaCodeGap; i++) {
            temp += countList.get(i);
            if(temp > average) {
                temp = 0;
                new_schema.add(minKey.getCode()+i*schemaCodeGap);
                if(new_schema.size() == indexNum-1){
                    break;
                }
            }
        }
        schema = new_schema;
    }

    /**
     *                   previous bad realization!
     *
     * calculate if the current schema is balanced according to the schema
     * @return a boolean of if current load is balanced
     */
    public boolean loadBalance0() {
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
     *                   previous bad realization!
     *
     * use to alternate schema with the frequency of schema
     * TODO need to implement some new data structure to store the frequency info
     * TODO 具体的边界是不要，还是固定的，先不确定，未来做！
     * 目前是只分分界点，没有指定边界点！
     * @param force the boolean to show if need to force renew the schema
     */
    public void balanceSchema0(boolean force) {
        // 以下的实现，是以schema 没有边界，只有分点来实现的
        //如果数量不够，不更新边界
        if (cacheQueue.size() < cacheLimit) return;
        List<Long> newSchema = new LinkedList<>();
        //如果现阶段的 work load 差距不大，也不更新
        //如果work load 相差很大，更新！
        if(!force && loadBalance0())  return;

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
            newSchema.add(maxHeap.get(0).getCode());
            newQueue = tempQueue;
            tempQueue = new LinkedList<>();
        }
        this.schema = newSchema;
        // 先摒除一些gui的东西，至于之后还要不要就再说
//        if(statusArea == null){
//            System.out.print("current schema: ");
//            printSchema();
//        } else {
//            statusArea.append("current schema: ");
//            statusArea.append(getSchema());
//            statusArea.append("\n");
//        }
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
            if(code.getCode() < schema.get(i)) {
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
            if(dataArea != null){
                dataArea.insert(line+"\n", 0);
            }
            if(line != null) {
                tempEntry = new entry(getMortonCode(line), time);
                MortonCode tempkey = tempEntry.key.key();
                tempEntryId = searchId(tempkey);
                if(tempkey.compareTo(maxKey) == 1)
                    maxKey = tempkey;
                else if(tempkey.compareTo(minKey) == -1)
                    minKey = tempkey;
                updateQueue(tempkey.getCode(), time);  // update queue
//                System.out.println();
                return getEntry(id);  //做完了所有准备则
            } else {
                if(currentNum < 57) {  //这里是一个特殊处理！！！我知道只有0-57这些文件，如果原始数据组织发生变化，一定需要改。
                    // if current file is meeting end, open the exceeding file
                    this.currentNum += 1;
                    buffer = new BufferedReader(new FileReader(dataPath + "s" + Integer.toString(currentNum)+".txt"));
                    line = buffer.readLine();
                    tempEntry = new entry(getMortonCode(line), time);
                    MortonCode tempkey = tempEntry.key.key();
                    tempEntryId = searchId(tempkey);
                    if(tempkey.compareTo(maxKey) == 1)
                        maxKey = tempkey;
                    else if(tempkey.compareTo(minKey) == -1)
                        minKey = tempkey;
                    updateQueue(tempkey.getCode(), time);  // update queue
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
        this.time = Integer.parseInt(timestamp);
        String coordTxt = st.nextToken();
        String otherData = st.nextToken();

        //wrangle data
        MortonCode key = new MortonCode(coordTxt);
        String value = timestamp + "," + otherData;
        return new BPTValueKey<>(key, value);
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
        return timestamp >= timeStart && timestamp <= timeEnd;
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
