package BPlusTree.keyType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MortonCodeTest {

    @Test
    public void zOrderTest() {
        assertEquals(23, MortonCode.zOrder(7, 1));
        assertEquals(60, MortonCode.zOrder(6, 6));
    }

    @Test
    void XYCalculate() {
        long n0 = 60;
        MortonCode m0 = new MortonCode(n0);
        assertEquals(6, m0.getX());
        assertEquals(6, m0.getY());

        long n1 = 23;
        MortonCode m1 = new MortonCode(n1);
        assertEquals(7, m1.getX());
        assertEquals(1, m1.getY());

        long n2 = 2368416231227397L;
        MortonCode m2 = new MortonCode(n2);
        assertEquals(8618499, m2.getX());
        assertEquals(41141376, m2.getY());


    }
}
