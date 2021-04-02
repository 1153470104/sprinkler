package dispatcher;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.keyType.MortonCode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
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
    private List<MortonCode> schema;
    private int indexNum;

    private int time;

    public dispatcher(String dataPath, int indexNum) throws IOException {
        this.dataPath = dataPath;
        this.indexNum = indexNum;
        this.tempEntryId = -1;
        this.getDomain();
        buffer = new BufferedReader(new FileReader(dataPath));
    }

    /**
     * use to alternate schema with the frequency of schema
     * TODO need to implement some new data structure to store the frequency info
     * TODO 具体的边界是不要，还是固定的，先不确定，未来做！
     */
    public void balanceSchema() {
        // TODO
    }

    /**
     * use to decide which index server should get tempEntry
     * according to the schema of partition
     * @param code the morton code BPTKey which need to be located
     * @return the id of corresponding index server's id
     *         which is start from 0 !!!
     */
    private int searchId(BPTKey<MortonCode> code) {
        // TODO
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
