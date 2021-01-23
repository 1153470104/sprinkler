package dispatcher;

import BPlusTree.BPTKey.BPTKey;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class dataTool {
    private String dataPath;

    public dataTool(String dataPath) {
        this.dataPath = dataPath;
    }

    public List<BPTKey> entryList() throws IOException {
        List<BPTKey> list= new ArrayList<>();
        BufferedReader in = new BufferedReader(new FileReader(dataPath));
        String line;
        while ((line = in.readLine()) != null) {
//            System.out.println(line);
        }
//        System.out.println(line);
        return list;
    }

    public static void main(String[] args) throws IOException {
        dataTool dt = new dataTool("resource/data/200.txt");
        dt.entryList();
    }
}

