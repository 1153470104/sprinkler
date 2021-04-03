package metadataServer.rectangleTree;

public class rectangle<K> {
    private K top;
    private K bottom;
    private int timeStart;
    private int timeEnd;

    public rectangle(K top, K bottom, int timeStart, int timeEnd) {
        this.top = top;
        this.bottom = bottom;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public void setTop(K top) {
        this.top = top;
    }

    public void setBottom(K bottom) {
        this.bottom = bottom;
    }

    public void setTimeStart(int timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(int timeEnd) {
        this.timeEnd = timeEnd;
    }

    public K getTop() {
        return top;
    }

    public K getBottom() {
        return bottom;
    }

    public int getTimeStart() {
        return timeStart;
    }

    public int getTimeEnd() {
        return timeEnd;
    }
}
