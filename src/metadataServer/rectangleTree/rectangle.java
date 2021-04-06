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
        if(top == null) {
            if(other.bottom!= null && bottom.compareTo(other.bottom) <= 0 && timeEnd >= other.timeEnd && timeStart <= other.timeStart) {
                return CONTAIN;
            }
            if(other.bottom == null || other.bottom.compareTo(top) > 0 && (timeEnd > other.timeStart && other.timeEnd > timeStart)) {
                return ACROSS;
            }
            return IRRELEVANT;
        } else if(bottom == null) {
            if(other.top!= null && top.compareTo(other.top) <= 0 && timeEnd >= other.timeEnd && timeStart <= other.timeStart) {
                return CONTAIN;
            }
            if(other.bottom == null || other.bottom.compareTo(top) > 0 && (timeEnd > other.timeStart && other.timeEnd > timeStart)) {
                return ACROSS;
            }
            return IRRELEVANT;

        }

        // the first time, I forgot to compare the time stamp
        // also change the  == 1 / == -1 to >0 <0
        //这里面的很多比较关系还简化了toCompare 传入null也传出负值的情况!!!
        if((bottom.compareTo(other.bottom) < 0) && (top.compareTo(other.top) > 0 || other.top == null)
               && timeEnd < other.timeEnd && timeStart > other.timeStart)
            return PERTAIN;
        if(bottom.compareTo(other.bottom) >= 0 && other.top != null && other.top.compareTo(top) >= 0
                && timeEnd >= other.timeEnd && timeStart <= other.timeStart)
            return CONTAIN;
        if ((other.top == null || bottom.compareTo(other.top) > 0) && top.compareTo(other.bottom) < 0
                 && (timeEnd > other.timeStart && other.timeEnd > timeStart))
            return ACROSS;
        return IRRELEVANT;
    }

    public rectangle<K> copy() {
        return new rectangle<>(top, bottom, timeStart, timeEnd);
    }

    public String toString() {
        String topS, bottomS;
        if(top == null)  topS = "null";
        else topS = top.toString();
        if(bottom == null)  bottomS = "null";
        else bottomS = bottom.toString();
        return topS + "," + bottomS + "," + timeStart + "," + timeEnd;
    }
}
