package BPlusTree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class bloomFilterTest {

    bloomFilter bf = new bloomFilter(15, 203);

    @Test
    void isIn() {
        bf.addMap(20000);
        bf.addMap(20001);
        bf.addMap(20002);
        assertTrue(bf.isIn(20000));
        assertFalse(bf.isIn(20024));
    }

    @Test
    void isInRegion() {
        bf.addMap(20000);
        bf.addMap(20001);
        bf.addMap(20002);

        bf.addMap(33000);
        bf.addMap(33001);
        bf.addMap(33002);

        assertTrue(bf.isInRegion(19999, 20005));
        assertFalse(bf.isInRegion(29999, 31005));
        assertTrue(bf.isInRegion(32890, 33001));
    }
}
