package dispatcher;

/**
 * a specific counter for morton code data dispatcher
 */
public class hashCounter {
    private int m;
    private int gap;
    private int[] counter1;
    private int[] counter2;
    private int[] counter3;

    public hashCounter(int gap) {
        //TODO
    }

    public void add(int code) {
        //TODO
    }

    public int count(int code){
        //TODO
        return 0;
    }

    // simple division method
    public int hashMap1(int code) {
        return code % m;
    }

    // multiplication method by 0.618
    public int hashMap2(int code) {
        double goldRatio = (Math.sqrt(5.0)-1) / 2;
        return (int)(((goldRatio * code) - (int)(goldRatio * code)) * m);
    }

    // multiplication method by 0.414
    public int hashMap3(int code) {
        double ratio = (Math.sqrt(2.0) - 1);
        return (int) (((ratio * code) - (int) (ratio * code)) * m);
    }
}
