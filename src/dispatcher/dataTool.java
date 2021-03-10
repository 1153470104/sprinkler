package dispatcher;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.keyType.MortonCode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * class that pre-process the simulation data
 */
public class dataTool {
    private String dataPath;
    private BufferedReader buffer;
    private MortonCode maxKey;
    private MortonCode minKey;

    private int time;

    public dataTool(String dataPath) throws IOException {
        this.dataPath = dataPath;
        this.getDomain();
        buffer = new BufferedReader(new FileReader(dataPath));
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
     * a function to get next entry through BufferReader buffer
     * @return
     * @throws IOException is thrown when an I/O operation fails
     */
    public BPTKey<MortonCode> getEntry() throws IOException {
        String line = buffer.readLine();
        if(line != null) {
            return getMortonCode(line);
        }
//         else {
//            return null;
//        }
        return null;
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

