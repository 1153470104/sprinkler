package metadataServer.rectangleTree;

public class rectangle<K extends Comparable> {
     protected K top;
     protected K bottom;
     protected int timeStart;
     protected int timeEnd;

     public static int CONTAIN = 1;
     public static int PERTAIN = -1;
     public static int IRRELEVANT = 0;
    public static int ACROSS = 2;

    public rectangle(K top, K bottom, int timeStart, int timeEnd) {
        this.top = top;
        this.bottom = bottom;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }


    /**
     * return corresponding status
     * between this rectangle & input rectangle
     * @param other the input rectangle
     * @return the relative status of input rectangle to this rectangle
     *         with four status: ACROSS CONTAIN PERTAIN IRRELEVANT
     */
    public int crossStatus(rectangle<K> other) {
        // the first time, I forgot to compare the time stamp
        // also change the  == 1 / == -1 to >0 <0
        if(bottom.compareTo(other.bottom) < 0 && top.compareTo(other.top) > 0
               && timeEnd < other.timeEnd && timeStart > other.timeStart)
            return PERTAIN;
        if(other.bottom.compareTo(bottom) <= 0 && other.top.compareTo(top) >= 0
                && timeEnd >= other.timeEnd && timeStart <= other.timeStart)
            return CONTAIN;
        if ((bottom.compareTo(other.top) > 0 && other.bottom.compareTo(top) > 0)
                 && (timeEnd > other.timeStart && other.timeEnd > timeStart))
            return ACROSS;
        return IRRELEVANT;
    }

    public rectangle<K> copy() {
        return new rectangle<>(top, bottom, timeStart, timeEnd);
    }

    public String toString() {
        return top.toString() + "," + bottom.toString() + "," + timeStart + "," + timeEnd;
    }
}
