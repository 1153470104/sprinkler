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
        if(bottom.compareTo(other.bottom) == -1 && top.compareTo(other.top) == 1)
            return PERTAIN;
        if(other.bottom.compareTo(bottom) == -1 && other.top.compareTo(top) == 1)
            return CONTAIN;
        if ((bottom.compareTo(other.top) == 1 && other.bottom.compareTo(top) == 1)
                 && (timeEnd > other.timeStart && other.timeEnd > timeStart))
            return ACROSS;
        return IRRELEVANT;
    }

    public rectangle<K> copy() {
        return new rectangle<>(top, bottom, timeStart, timeEnd);
    }
}
