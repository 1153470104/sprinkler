package BPlusTree.keyType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MortonCodeTest {

    @Test
    public void zOrderTest() {
        assertEquals(23, MortonCode.zOrder(7, 1));
        assertEquals(60, MortonCode.zOrder(6, 6));
    }

}
