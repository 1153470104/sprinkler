package dispatcher;

/**
 * a specific counter for morton code data dispatcher
 */
public class hashCounter {
    private int m; // the slot number of every hash slot
    private int gap;
    private int[] counter1;
    private int[] counter2;
    private int[] counter3;

    public hashCounter(int gap, int m) {
        this.gap = gap;
        this.m = m;
        this.counter1 = new int[m];
        this.counter2 = new int[m];
        this.counter3 = new int[m];
    }

    public void add(long code) {
        code = code - code % gap;  // use gap to combine some
        int index1 = hashMap1(code);
        counter1[index1] = counter1[index1] + 1;
        int index2 = hashMap2(code);
        counter2[index2] = counter2[index2] + 1;
        int index3 = hashMap3(code);
        counter3[index3] = counter3[index3] + 1;
    }

    public int count(long code){
        code = code - code % gap;  // use gap to combine some
        int min = counter1[hashMap1(code)];  // the first count become
        int count2 = counter2[hashMap2(code)];
        if(count2 < min)
            min = count2;
        int count3 = counter3[hashMap3(code)];
        if(count3 < min)
            min = count3;
        return min;
    }

    // simple division method
    public int hashMap1(long code) {
        return (int)(code % m);
    }

    // multiplication method by 0.618
    public int hashMap2(long code) {
        double goldRatio = (Math.sqrt(5.0)-1) / 2;

        return (int)(((goldRatio * code) - (long)(goldRatio * code)) * m);
    }

    // multiplication method by 0.414
    public int hashMap3(long code) {
        double ratio = (Math.sqrt(2.0) - 1);
        return (int) (((ratio * code) - (long) (ratio * code)) * m);
    }
}
