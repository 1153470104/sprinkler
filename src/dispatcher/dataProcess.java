package dispatcher;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.keyType.MortonCode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * class with some pre-processing method for simulation data
 */
public class dataProcess {
    private BufferedReader buffer;
    private String dataPath;

    public dataProcess(String dataPath) throws FileNotFoundException {
        this.dataPath = dataPath;
    }

    /**
     * transform the coordinates in simulation data to z-order
     * @param fileName the file name of simulation data
     * @throws IOException thrown when an input operation fails
     */
    public void transZOrder(String fileName) throws IOException {
        buffer = new BufferedReader(new FileReader(dataPath));
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

    /**
     * a heap sort realization reorder the entries in simulation data
     * by the order of every entry's timestamp decreasingly
     * @param fileName the file of simulation data
     * @throws IOException thrown when an I/O operation fails
     */
    public void sortByTime(String fileName) throws IOException {
        buffer = new BufferedReader(new FileReader(dataPath));
        List<String> sortList = new ArrayList<>();
        sortList.add(""); //为了方便堆排序的父子节点之间查找，加一个空节点就可以直接1/2找了
        String line = buffer.readLine();
        while(line != null) {
//            System.out.println(line);
            sortList.add(line);
            line = buffer.readLine();
        }
        int bound = sortList.size()-1;
        for(int i = bound; i > 1; i--) { /* 老是在边界上搞错, 把 >1 写成 >0，忘记根节点不需要比较*/
            if(lineCompare(sortList.get(i), sortList.get(i/2))) {
                // compare with father node, if bigger, swap value
                String temp = sortList.get(i);
                sortList.set(i, sortList.get(i/2));
                sortList.set(i/2, temp);
                //compare with child node if smaller, swap downward
                int current = i;
                while (true) {
                    if (current * 2 > bound) {
                        break;
                    } else if (current * 2 + 1 <= bound) {
                        int swapPos;
                        if(lineCompare(sortList.get(current*2), sortList.get(current*2+1))) {
                            swapPos = current*2;
                        } else {
                            swapPos = current*2+1;
                        }
                        String tt = sortList.get(current);
                        sortList.set(current, sortList.get(swapPos));
                        sortList.set(swapPos, tt);
                        current = swapPos;
                    } else {
                        if(lineCompare(sortList.get(current*2), sortList.get(current))) {
                            String tt = sortList.get(current);
                            sortList.set(current, sortList.get(current*2));
                            sortList.set(current*2, tt);
                            current = current*2;
                        }
                    }
                }
            }
        }
        //下面写堆排序取max和维护的过程
        while(bound > 1) {
//            System.out.print(1);
            String temp = sortList.get(bound);
            sortList.set(bound, sortList.get(1));
            sortList.set(1, temp);
            bound = bound-1;
            int current = 1;
            while (true) {
                /* 重大失误，如果找到更大的就交换如果没找到就结束循环，我tm没结束循环 */
//                System.out.print(2);
                if (current * 2 > bound) {
                    break;
                } else if (current * 2 + 1 <= bound) {
                    int swapPos;
                    if(lineCompare(sortList.get(current*2), sortList.get(current*2+1))) {
                        swapPos = current*2;
                    } else {
                        swapPos = current*2+1;
                    }
                    if(lineCompare(sortList.get(swapPos), sortList.get(current))) {
                        String tt = sortList.get(current);
                        sortList.set(current, sortList.get(swapPos));
                        sortList.set(swapPos, tt);
                        current = swapPos;
                    } else {
                        break;
                    }
                } else {
                    if(lineCompare(sortList.get(current*2), sortList.get(current))) {
                        String tt = sortList.get(current);
                        sortList.set(current, sortList.get(current*2));
                        sortList.set(current*2, tt);
                        current = current*2;
                    } else {
                        break;
                    }
                }
//                System.out.print(3);
            }
            System.out.println(bound);
        }
        /* 下面的方式不可取，每次不维护堆结构，那就相当于n^2的计算量了
        while(bound > 1) {
            for(int i = bound; i > 1; i--) {
                if(lineCompare(sortList.get(i), sortList.get(i/2))) {
                    String temp = sortList.get(i);
                    sortList.set(i, sortList.get(i/2));
                    sortList.set(i/2, temp);
                }
            }
            String temp = sortList.get(1);
            sortList.set(1, sortList.get(bound));
            sortList.set(bound, temp);
            System.out.println(bound);
            bound--;
        } */

        BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
        for(int j = 1; j < sortList.size(); j++) {
            out.write(sortList.get(j) + "\n");
        }
        out.close();

    }

    /**
     * compare two lines time order
     * @param line1 raw text of entry 1
     * @param line2 raw text of entry 2
     * @return if entry1's timestamp less than entry2's return false
     *         else return true
     */
    public boolean lineCompare(String line1, String line2) {
        StringTokenizer st1 = new StringTokenizer(line1, "|");
        StringTokenizer st2 = new StringTokenizer(line2, "|");
        int time1 = Integer.valueOf(st1.nextToken());
        int time2 = Integer.valueOf(st2.nextToken());
        if (time1 > time2) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        dataProcess ds = new dataProcess("resource/data/100000.txt");
//        ds.transZOrder("resource/data/100000z.txt");
        ds.sortByTime("resource/data/100000s.txt");
    }
}
