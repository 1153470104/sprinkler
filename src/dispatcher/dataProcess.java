package dispatcher;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.keyType.MortonCode;

import java.io.*;
import java.util.StringTokenizer;

public class dataProcess {
    private BufferedReader buffer;

    public dataProcess(String dataPath) throws FileNotFoundException {
        buffer = new BufferedReader(new FileReader(dataPath));
    }

    public void transZOrder(String fileName) throws IOException {
        String line = buffer.readLine();
//        System.out.println(line);
        BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
        while(line != null) {
            StringTokenizer st = new StringTokenizer(line, "|");
            String timestamp = st.nextToken();
            String coordTxt = st.nextToken();
            String otherData = st.nextToken();
            MortonCode mc = new MortonCode(coordTxt);
            long zOrder = mc.getCode();
//            System.out.println(zOrder);
            out.write(timestamp + "|" + String.valueOf(zOrder) + "|" + otherData+"\n");
            line = buffer.readLine();
        }
        out.close();
    }

    public static void main(String[] args) throws IOException {
        dataProcess ds = new dataProcess("resource/data/100000.txt");
        ds.transZOrder("resource/data/100000z.txt");
    }
}
