package metadataServer.rectangleTree;

public class rectangle<K extends Comparable> {
     protected K top;
     protected K bottom;
     protected int timeStart;
     protected int timeEnd;

    public rectangle(K top, K bottom, int timeStart, int timeEnd) {
        this.top = top;
        this.bottom = bottom;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public boolean accross(rectangle<K> other) {
        return (bottom.compareTo(other.top) == 1 && other.bottom.compareTo(top) == 1)
                 && (timeEnd > other.timeStart && other.timeEnd > timeStart);
    }
}
