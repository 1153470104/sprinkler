package BPlusTree.keyType;

import BPlusTree.BPTKey.BPTKey;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MortonCode implements Comparable{

    private int code;
    private int x;
    private int y;

    public MortonCode(){
    }

    public MortonCode(double x, double y){
        int ix = (int)x*1000000;
        int iy = (int)y*1000000;
        this.x = ix;
        this.y = iy;
        this.code = zOrder(ix, iy);
    }

    public MortonCode(String coordText){
        StringTokenizer st = new StringTokenizer(coordText, ",");
        int ix = (int)(Double.parseDouble(st.nextToken())*1000000);
        int iy = (int)(Double.parseDouble(st.nextToken())*1000000);
        this.x = ix;
        this.y = iy;
        this.code = zOrder(ix, iy);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static int zOrder(int x, int y) {
        int round = 0;
        int zCode = 0;
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
        //这里idea叫我替换，那就替换了，原来并不知道还能这样子
        return Integer.compare(this.code, ((MortonCode) o).code);
    }

    /**
     * 用于从一个单纯的按照morton码大小截取的list中，提取出实际在空间范围内的记录
     * @param mList
     * @return
     */
    public static List<BPTKey<MortonCode>> regionCut(List<BPTKey<MortonCode>> mList) {
        int xMin = mList.get(0).getKey().getX();
        int yMin = mList.get(0).getKey().getY();
        int xMax = mList.get(mList.size()-1).getKey().getX();
        int yMax = mList.get(mList.size()-1).getKey().getY();
        List<BPTKey<MortonCode>> rList = new ArrayList<>();
        for(BPTKey<MortonCode> key: mList) {
            int xx = key.getKey().getX();
            int yy = key.getKey().getY();
            if (xx <= xMax && xx >= xMin && yy <= yMax && yy >= yMin) {
                rList.add(key);
            }
        }
        return rList;
    }

    @Override
    public String toString() {
        return Integer.toString(code);
    }
}
