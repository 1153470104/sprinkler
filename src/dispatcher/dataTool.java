package dispatcher;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.keyType.MortonCode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class dataTool {
    private String dataPath;
    private BufferedReader buffer;
    private MortonCode maxKey;
    private MortonCode minKey;

    public int getTime() {
        return time;
    }

    private int time;

    public dataTool(String dataPath) throws IOException {
        this.dataPath = dataPath;
        this.getDomain();
        buffer = new BufferedReader(new FileReader(dataPath));
    }

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

