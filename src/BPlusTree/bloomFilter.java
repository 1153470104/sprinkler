package BPlusTree;

/**
 * the class of bloom filter
 * used to reduce the computing cost in searching process
 *
 * the type of data which is need to be filter is timestamp which is integer
 * so the default element type is int, and all realization is based on that
 *
 * notes: the time gap of this program is about 100 000 scale
 */
public class bloomFilter {
    private int gap; // the time gap of the minimal time unit
    private int m; // the slot number of the bloom filter
    private int[] bitMap; // the map store the bit mapping information
    //TODO test, maybe need unsigned int or other things ...

    public bloomFilter(int gap, int m) {
        this.gap = gap;
        this.m = m;
        /*jesus! a ridiculous fault! divide'/' was mis-written into mod '%' */
        bitMap = new int[m / 32+1]; // initiate map length according to m's quantity
    }

    // used to recover bloom filter from external tree
    /*there's no need! could just copy bloom filter, because part of external tree is in memory*/
//    public bloomFilter(int gap, int m, int[] bitMap){
//        this.gap = gap;
//        this.m = m;
//        this.bitMap = bitMap;
//    }

    public static bloomFilter bloomFilterFactory() {
        return new bloomFilter(15, 203);
    }

    public void insert(int index) {
        int intNum = index / 32;
        int orderNum = index % 32;
        System.out.print("index: "+intNum);
        System.out.println(" - "+orderNum);
//        int mask = 0b01 << 31;
        int mask = 0b01 << 31 >>> orderNum;
        System.out.println("before: "+Integer.toBinaryString(bitMap[intNum]));
        bitMap[intNum] = bitMap[intNum] | mask;
        System.out.println("after: "+Integer.toBinaryString(bitMap[intNum])+"\n");
    }

    // simple division method
    public int hashMap1(int time) {
        return time % m;
    }

    // multiplication method by 0.618
    public int hashMap2(int time) {
        double goldRatio = (Math.sqrt(5.0)-1) / 2;
        return (int)(((goldRatio * time) - (int)(goldRatio * time)) * m);
    }

    // multiplication method by 0.414
    public int hashMap3(int time) {
        double ratio = (Math.sqrt(2.0)-1);
        return (int)(((ratio * time) - (int)(ratio * time)) * m);
    }

    /**
     * combine 3 different hash mapping to one location
     * @param time the time need to be mapped
     */
    public void addMap(int time) {
        time = time - time % gap; // used to fulfill a block of time
        insert(hashMap1(time));
        insert(hashMap2(time));
        insert(hashMap3(time));
    }

    public boolean isIndexIn(int index) {
        int intNum = index / 32;
        int orderNum = index % 32;
        System.out.print("index: "+intNum);
        System.out.println(" - "+orderNum);
        int mask = 0b01 << 31 >>> orderNum;
        System.out.println("mask: "+Integer.toBinaryString(mask));
        System.out.println("bitmap: "+Integer.toBinaryString(bitMap[intNum])+"\n");
        return (bitMap[intNum] & mask) == mask;
    }

    public boolean isIn(int time) {
        time = time - time % gap; // used to fulfill a block of time
        int index1 = hashMap1(time);
        int index2 = hashMap2(time);
        int index3 = hashMap3(time);
        return isIndexIn(index1) && isIndexIn(index2) && isIndexIn(index3);
    }

//    public boolean isInGap(int timeStart) {
//        int realTime = timeStart - timeStart % this.gap;
//        return isIn(realTime);
//    }

    public boolean isInRegion(int timeStart, int timeEnd) {
        int start = (timeStart / gap) * gap;
        int end = (timeEnd / gap) * gap;
        if(end < timeEnd)  end = end+gap;
        boolean isIn = false;
        // 检测出存在就直接退出循环，开始遍历寻找。。。
        while(start <= end && !isIn) {
            if(isIn(start)) isIn = true;
            start += gap;
        }
        return isIn;
    }
}
