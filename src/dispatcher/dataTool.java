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

    public dataTool(String dataPath) {
        this.dataPath = dataPath;
    }

    public static BPTKey<MortonCode> getMortonCode(String line) {
        StringTokenizer st = new StringTokenizer(line, "|");
        String timestamp = st.nextToken();
        String coordTxt = st.nextToken();
        String otherData = st.nextToken();

        //wrangle data
        MortonCode key = new MortonCode(coordTxt);
        String value = timestamp + "," + otherData;
        BPTKey<MortonCode> bptKey = new BPTValueKey<>(key, value);
        return bptKey;
    }

    public List<BPTKey> entryList() throws IOException {
        List<BPTKey> list = new ArrayList<>();
        BufferedReader in = new BufferedReader(new FileReader(dataPath));
        String line;
        while ((line = in.readLine()) != null) {
            BPTKey<MortonCode> bptKey = getMortonCode(line);
//            System.out.println(bptKey);
            list.add(bptKey);
        }
        return list;
    }

    //temporary simple test of dataTool
//    public static void main(String[] args) throws IOException {
//        dataTool dt = new dataTool("resource/data/200.txt");
//        dt.entryList();
//    }
}

