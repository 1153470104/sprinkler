package dispatcher;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;
import BPlusTree.keyType.MortonCode;

import java.io.*;
import java.util.*;

/**
 * class with some pre-processing method for simulation data
 */
public class dataProcess {
    private BufferedReader buffer;
    private String dataPath;
    private int fileNum;

    public dataProcess(String dataPath) throws FileNotFoundException {
        this.dataPath = dataPath;
    }

    public void countFile() {
        File root = new File(dataPath);
        File[] files = root.listFiles();
        assert files != null;  //这个是ide提醒我的。。。
        fileNum = files.length;
    }
    /**
     * transform the coordinates in simulation data to z-order
     * @param fileName the file name of simulation data
     * @throws IOException thrown when an input operation fails
     */
    public void transZOrder(String fileName) throws IOException {
        buffer = new BufferedReader(new FileReader(dataPath));
        String line = buffer.readLine();
        BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
        while(line != null) {
            StringTokenizer st = new StringTokenizer(line, "|");
            String timestamp = st.nextToken();
            String coordTxt = st.nextToken();
            String otherData = st.nextToken();
            MortonCode mc = new MortonCode(coordTxt);
            long zOrder = mc.getCode();
            out.write(timestamp + "|" + String.valueOf(zOrder) + "|" + otherData+"\n");
            line = buffer.readLine();
        }
        out.close();
    }

    public void externalSort1() throws IOException {
        countFile();
        // first sort every data part individually
        for(int i = 0; i < fileNum-1; i++) {  /* 放了一个文件夹在里面，结果file数增加了。。。然后会溢出 */
            System.out.print(i);
            sortByTime2(dataPath+"part"+Integer.toString(i)+".txt", dataPath+"sort/s_part"+Integer.toString(i)+".txt");
        }

    }

    public void externalSort2(String fileName) throws IOException {
        countFile();

        // open all buffer and add then into an array
        BufferedReader[] bufferArray = new BufferedReader[this.fileNum];
        for(int i = 0; i < this.fileNum; i++) {
            bufferArray[i] = new BufferedReader(new FileReader(
                    this.dataPath+"s_part"+Integer.toString(i)+".txt"));
        }
        List<groupLine> binaryList = new ArrayList<>();
        for(int j = 0; j < this.fileNum; j++) {
            groupLine temp = new groupLine(j, bufferArray[j].readLine());
            binaryList.add(temp);
        }
        Comparator<groupLine> comp = new glComparator();
        binaryList.sort(comp);

        // init the output buffer
        int fileCount = 0;
        int currentLine = 0;
        BufferedWriter out = new BufferedWriter(new FileWriter(
                fileName+"s"+ fileCount + ".txt"));
        do {
            out.write(binaryList.get(0).line + "\n");
            groupLine temp = binaryList.remove(0);
            int groupIndex = temp.groupNum;
            currentLine += 1;

            // get new line, add new line into sorted list
            String line = bufferArray[groupIndex].readLine();
            if(line != null) {
                // do a binary insert
                groupLine gs = new groupLine(groupIndex, line);
                int start = 0;
                int end = binaryList.size()-1;
                // 2 special cases
                if(gs.compare(binaryList.get(end))) binaryList.add(gs);
                if(!gs.compare(binaryList.get(start))) binaryList.add(0, gs);
                // normal case
                while(end-start > 1) {
                    if(gs.compare(binaryList.get((start+end) / 2))) {
                        start = (start+end) / 2;
                    } else {
                        end = (start+end) / 2;
                    }
                }
                binaryList.add(end, gs);
            } else {
                continue;
            }

            // verify sort-combining process status
            if (currentLine == 1000000) {
                System.out.println("file"+fileCount+" finish!");
                out.close();
                fileCount += 1;
                currentLine = 0;
                out = new BufferedWriter(new FileWriter(fileName + "s" + fileCount + ".txt"));
            }
        } while (binaryList.size() > 0); /*又是一个while-true到 do-while的优化！！!*/

        out.close();

        // close all buffer
        for(int i = 0; i < this.fileNum; i++) {
            bufferArray[i].close();
        }
    }

//    public static void insertSortList(List<groupLine> list, groupLine line) {
//        if(list.size() == 0) {
//            list.add(line);
//        } else {
//            int len = list.size();
//
//        }
//    }

    /**
     * a heap sort realization reorder the entries in simulation data
     * by the order of every entry's timestamp decreasingly
     * @param originFile the file to be sorted
     * @param fileName the file of simulation data
     * @throws IOException thrown when an I/O operation fails
     */
    public void sortByTime2(String originFile, String fileName) throws IOException {
        buffer = new BufferedReader(new FileReader(originFile));
        List<String> sortList = new ArrayList<>();
        sortList.add(""); //为了方便堆排序的父子节点之间查找，加一个空节点就可以直接1/2找了
        String line = buffer.readLine();
        System.out.println("start to get lines");
        while(line != null) {
            sortList.add(line);
            line = buffer.readLine();
        }
        System.out.println("put them into a heap");
        int bound = sortList.size()-1;
        System.out.println(bound);
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
                }
            }
        }
        System.out.println("sort!!!");
        //下面写堆排序取max和维护的过程
        while(bound > 1) {
            String temp = sortList.get(bound);
            sortList.set(bound, sortList.get(1));
            sortList.set(1, temp);
            bound = bound-1;
            int current = 1;
            while (true) {
                /* 重大失误，如果找到更大的就交换如果没找到就结束循环，我tm没结束循环 */
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
            }
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
        int time1 = Integer.parseInt(st1.nextToken());
        int time2 = Integer.parseInt(st2.nextToken());
        return time1 > time2;
    }

    public class groupLine {
        public int groupNum;
        public String line;

        groupLine(int i, String line) {
            this.groupNum = i;
            this.line = line;
        }

        /**
         * is this time is larger than other's time, return true
         * @param other other groupLine
         * @return if its time is larger than other groupLine
         */
        public boolean compare(groupLine other){
            return lineCompare(this.line, other.line);
        }
    }

    public class glComparator implements Comparator<groupLine> {
        @Override
        public int compare(groupLine o1, groupLine o2) {
            /*这里一定要是相反的数！-1 1 这样，不能够 0 1
              从java1.7开始就得这样了，具体涉及到什么timesort 没细看*/
            if (lineCompare(o1.line, o2.line)) return 1;
            else return -1;
        }
    }

    public static void main(String[] args) throws IOException {
        dataProcess ds = new dataProcess("resource/data/externalData/sort/");
        ds.externalSort2("resource/data/data_sort/");
//        ds.transZOrder("resource/data/100000z.txt");
//        ds.sortByTime(ds.dataPath, "resource/data/100000s.txt");
    }
}
