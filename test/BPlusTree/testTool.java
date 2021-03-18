package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;

import java.util.ArrayList;
import java.util.List;

public class testTool {
    private List<BPTKey<Integer>> keyList;
    private BPlusTree<Integer> bpt;

    public testTool(){
    }

    public static BPTValueKey<Integer, String> IntegerKey(int i) {
        return new BPTValueKey<Integer, String>(i, Integer.toString(i));
    }

    public BPlusTree<Integer> bpt() {
        return bpt;
    }

    public void initBPT(int m) {
        bpt = new BPlusTreeScratched<Integer>(m);
    }

    public void initKeyList(int number) {
        keyList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            keyList.add(new BPTValueKey<Integer, String>(i, Integer.toString(i)));
        }
    }

    public void addList() {
        for(BPTKey<Integer> key: keyList){
            bpt.addKey(key);
        }
    }

    public void addList(List<Integer> list, String valueType) {
        for(Integer i: list) {
            if(valueType.equals("String")) {
                StringBuilder s = new StringBuilder(Integer.toString(i));
                int len = s.length();
                if(s.length() < 3) {
                    for(int j = 0; j < 3-len; j++){
                        s.insert(0, " ");
                    }
                }
                bpt.addKey(new BPTValueKey<Integer, String>(i, s.toString()));
            } else if(valueType.equals("Integer"))  {
                bpt.addKey(new BPTValueKey<Integer, Integer>(i, i));
            }
        }
    }

    public void makeBPT(int m, int length) {
        initBPT(m);
        initKeyList(length);
        addList();
    }

    public void makeBPT(int m, List<Integer> list) {
        initBPT(m);
        addList(list, "String");
    }

    public void makeIntBPT(int m, List<Integer> list) {
        initBPT(m);
        addList(list, "Integer");
    }

    public String keyListString(List<BPTKey<Integer>> kList) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        for(BPTKey<Integer> n: kList) {
            sb.append(" ").append(n.key()).append(" |");
        }
        return sb.toString();
    }

}
