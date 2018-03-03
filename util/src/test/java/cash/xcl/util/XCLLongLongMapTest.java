package cash.xcl.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class XCLLongLongMapTest {
    XCLLongLongMap map = XCLLongLongMap.withExpectedSize(16);

    @Test
    public void put() {
        assertEquals(0, map.size());
        map.put(1, 1);
        assertEquals(1, map.size());
        assertEquals(1, map.get(1));
        map.put(1, 11);
        assertEquals(1, map.size());
        assertEquals(11, map.get(1));

        assertEquals("!cash.xcl.util.KolobokeXCLLongLongMap {\n" +
                "  ? 1: 11\n" +
                "}\n", map.toString());
    }

    @Test
    public void get() {
        map.put(1, 1);
        assertEquals(1, map.get(1));
        assertEquals(0, map.get(2));
        assertEquals(-1, map.getOrDefault(2, -1));
    }

    @Test
    public void containsKey() {
        assertFalse(map.containsKey(1));
        map.put(1, 1);
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(0));
    }

    @Test
    public void forEach() {
    }

    @Test
    public void putAll() {
        map.put(1, 1);
        map.put(2, 2);
        XCLLongLongMap map2 = XCLLongLongMap.withExpectedSize(16);
        map2.put(0, 0);
        map2.put(1, 11);
        map2.putAll(map);
        assertEquals("!cash.xcl.util.KolobokeXCLLongLongMap {\n" +
                "  ? 2: 2,\n" +
                "  ? 1: 1,\n" +
                "  ? 0: 0\n" +
                "}\n", map2.toString());

    }
}