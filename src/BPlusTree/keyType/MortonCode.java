package BPlusTree.keyType;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.BPTKey.BPTValueKey;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * class for 2D coordinator's storage & operation
 *
 * Morton Code (z-order) data structure implementation
 * which map multi-dimensional data to one dimension
 * so that those space data could be indexed by B+ tree
 *
 * the x-y corresponding morton code sheet:
 *       x=  0    1    2    3    4    5    6    7
 *
 * y=  0     0 -- 1    4 -- 5   16 --17   20 --21
 *              /    /    /   |    /    /    /
 * y=  1     2 -- 3    6 -- 7 | 18 --19   22 --23
 *             ,-----------'  |           -----'
 * y=  2     8 -- 9   12 --13 |      ...
 *              /   /    /    |      ...
 * y=  3    10 --11   14 --15        ...
 *
 * Morton Code form a new sequence by interleaving x & y sequence on binary level
 *     e.g. x=2 0b10, y=3 0b11 form code ob1110=14 as the sheet shows
 *
 */
public class MortonCode implements Comparable{

    private long code;
    private int x;
    private int y;

    /**
     * use x,y coordinates to build a morton code
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public MortonCode(double x, double y){
        int ix = Math.abs((int)x*1000000);
        int iy = Math.abs((int)y*1000000);
        this.x = ix;
        this.y = iy;
        this.code = zOrder(ix, iy);
    }

    /**
     * use a coord text to build a morton code
     * @param coordText the plain string text of coordinate
     *                  which should be 'x.xxxxxx,x.xxxxxx'
     */
    public MortonCode(String coordText){
        StringTokenizer st = new StringTokenizer(coordText, ",");
        int ix = Math.abs((int)(Double.parseDouble(st.nextToken())*1000000));
        int iy = Math.abs((int)(Double.parseDouble(st.nextToken())*1000000));
        this.x = ix;
        this.y = iy;
        this.code = zOrder(ix, iy);
    }


    /**
     * init with a long of code
     * @param code the zOrder code
     */
    public MortonCode(long code) {
        // assign code value
        this.code = code;
        // reversely calculate the value of x & y
        XYCalculate();
    }

    /**
     * calculate x & y according to the code
     */
    public void XYCalculate() {
        x = 0;
        y = 0;
        long cur = code;
        int round = 0;
        while(cur > 0) { // this should be '>0' because x & y is not symmetry
            x = (int) (x + (cur%2) * Math.pow(2, round));
            cur = cur / 2;
            y = (int) (y + (cur%2) * Math.pow(2, round));
            cur = cur / 2;
            round++;
        }
    }

    /**
     * getter of x coordinate
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * getter of y coordinate
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }


    /**
     * transform two dimension coordinate into one dimension z-code
     * @param x the x coord
     * @param y the y coord
     * @return a long of zOrder code
     */
    public static long zOrder(int x, int y) {
        /* 居然没有考虑到溢出的问题，直接导致所有的zorder都变成了2^31-1 */
        int round = 0;
        long zCode = 0;
        while(x>0 || y>0) {
            int lastx = x % 2;
            x = x/2;
            int lasty = y % 2;
            y = y/2;
            zCode += lastx*(Math.pow(2, round));
            zCode += lasty*(Math.pow(2, round+1));
            round = round + 2;
        }
        return zCode;
    }

    @Override
    public int compareTo(Object o) {
        // this part is to extend the compareTo() function   //seems useless....
        // to make it fit the null input
        if(o == null) {
            return -2;
        }
        //这里idea叫我替换，那就替换了，原来并不知道还能这样子
        return Long.compare(this.code, ((MortonCode) o).code);
    }

    /**
     * 用于从一个单纯的按照morton码大小截取的list中，提取出实际在空间范围内的记录
     *
     * if you get a list of Morton Code in B+ tree
     * by the division of most up-left & down-right point
     * the list's first element is the up-left point code
     * the list's last element is the down-right point code
     * which could be used to know the origin domain of data you want
     *
     * @param mList the raw morton code get from B+ tree
     * @return the list of morton code in assigned region
     */
    public static List<BPTKey<MortonCode>> regionCut(List<BPTKey<MortonCode>> mList, MortonCode startKey, MortonCode endKey) {
        // get the real boundary of the search region
        /* 幼稚！幼稚！你以为你得到的顺序就是最后的顺序吗
           原来的一个问题，即没有输入起始和结束的key。。。
           以为list里第一个就是开始而最后一个就是结束。。 */
        int xMin = startKey.getX();
        int yMin = startKey.getY();
        int xMax = endKey.getX();
        int yMax = endKey.getY();

        // this function just use inner params which maybe not ok after
        // because the storage on disk is morton code only
        // TODO a better choice is to aim just at z-code list, which is more simple
        List<BPTKey<MortonCode>> rList = new ArrayList<>();
        for(BPTKey<MortonCode> key: mList) {
            int xx = key.key().getX();
            int yy = key.key().getY();
            if (xx <= xMax && xx >= xMin && yy <= yMax && yy >= yMin) {
                rList.add(key);
            }
        }
        return rList;
    }

    /**
     * the getter of z-code
     * @return the z-code of this Morton Code
     */
    public long getCode() {
        return code;
    }

    @Override
    public String toString() {
        return Long.toString(code);
    }
}
