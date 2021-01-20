package keyType;

import java.util.StringTokenizer;

public class MortonCode {
    private int code;

    public MortonCode(){
    }

    public MortonCode(double x, double y){
        int ix = (int)x*1000000;
        int iy = (int)y*1000000;
        this.code = zOrder(ix, iy);
    }

    public MortonCode(String coordText){
        StringTokenizer st = new StringTokenizer(coordText, ",");
        int ix = (int)(Double.parseDouble(st.nextToken())*1000000);
        int iy = (int)(Double.parseDouble(st.nextToken())*1000000);
        this.code = zOrder(ix, iy);
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
}
