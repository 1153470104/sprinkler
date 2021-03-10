package BPlusTree.keyType;

import java.util.StringTokenizer;

/**
 *
 * unused class of coordinator
 *本来是要用这个坐数据的，但是发现一般来说是用morton码来线性化二维坐标
 *
 */
public class Coord {
    private double x;
    private double y;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    public Coord(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Coord(String coordText){
        StringTokenizer st = new StringTokenizer(coordText, ",");
        this.x = Double.parseDouble(st.nextToken());
        this.y = Double.parseDouble(st.nextToken());
    }

    public boolean xExceed(Coord another){
        return this.getX() > another.getX();
    }

    public boolean yExceed(Coord another){
        return this.getY() > another.getY();
    }


    @Override
    public String toString() {
        return "[" + x + ", " + y + ']';
    }

}

