package BPlusTree;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.configuration.configuration;

import java.lang.module.Configuration;
import java.util.ArrayList;
import java.util.List;

public class testTool {
    private List<BPTKey<Integer>> keyList;
    private BPlusTree<Integer, String> bpt;
    private BPlusTree<Integer, String> confBpt;
    private BPlusTree<Integer, Integer> intBpt;

    public testTool(){
    }

    public static BPTValueKey<Integer, String> IntegerKey(int i) {
        return new BPTValueKey<>(i, Integer.toString(i));
    }

    public BPlusTree<Integer, String> bpt() {
        return bpt;
    }

    public BPlusTree<Integer, Integer> intBpt() {
        return intBpt;
    }

    public BPlusTree<Integer, String> confBpt() {
        return confBpt;
    }

    public void initBPT(int m) {
        configuration conf = new configuration(4, 2, Integer.class, String.class);
        conf.m = m;
        bpt = new BPlusTreeScratched<Integer, String>(conf);
        intBpt = new BPlusTreeScratched<Integer, Integer>(m);
//        confBpt = new BPlusTreeScratched<Integer, String>(conf);
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
//                System.out.print(s + " ");
                bpt.addKey(new BPTValueKey<>(i, s.toString()));
            } else if(valueType.equals("Integer"))  {
                intBpt.addKey(new BPTValueKey<>(i, i));
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
