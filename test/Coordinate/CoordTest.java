package Coordinate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordTest {
    Coord c1 = new Coord(100, 945.4);
    Coord c2 = new Coord(300, 95.4);
    Coord c3 = new Coord("-8.650395,41");
    Coord c4 = new Coord("-8.604045,41.145417");

    @Test
    void xExceed() {
        assertTrue(c1.xExceed(c3));
        assertFalse(c1.xExceed(c2));
        assertTrue(c4.xExceed(c3));
        assertFalse(c3.xExceed(c2));
    }

    @Test
    void yExceed() {
        assertTrue(c1.yExceed(c3));
        assertTrue(c1.yExceed(c2));
        assertTrue(c4.yExceed(c3));
        assertFalse(c3.yExceed(c2));
    }

    @Test
    void testToString() {
        assertEquals(c1.toString(), "[100.0, 945.4]");
        assertEquals(c2.toString(), "[300.0, 95.4]");
        assertEquals(c3.toString(), "[-8.650395, 41.0]");
        assertEquals(c4.toString(), "[-8.604045, 41.145417]");
    }
}
