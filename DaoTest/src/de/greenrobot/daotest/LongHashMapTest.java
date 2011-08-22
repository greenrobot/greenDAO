package de.greenrobot.daotest;

import java.util.Random;

import junit.framework.TestCase;
import de.greenrobot.dao.LongHashMap;

public class LongHashMapTest extends TestCase {

    Random random;
    private String traceName;
    private long start;

    public LongHashMapTest() {
        this.random = new Random();
    }

    public void testLongHashMapSimple() {
        LongHashMap<Object> map = new LongHashMap<Object>();

        map.put(1l << 33, "OK");
        assertNull(map.get(0));
        assertEquals("OK", map.get(1l << 33));

        long keyLong = 0x7fffffffl << 33l + 14;
        assertNull(map.remove(keyLong));
        map.put(keyLong, "OK");
        assertTrue(map.containsKey(keyLong));
        assertEquals("OK", map.remove(keyLong));

        keyLong = Long.MAX_VALUE;
        map.put(keyLong, "OK");
        assertTrue(map.containsKey(keyLong));

        keyLong = 8064216579113853113l;
        map.put(keyLong, "OK");
        assertTrue(map.containsKey(keyLong));

    }

    public void testLongHashMapRandom() {
        LongHashMap<Object> map = new LongHashMap<Object>();
        for (int i = 0; i < 5000; i++) {
            long key = random.nextLong();
            String value = "Value-" + key;
            map.put(key, value);
            assertTrue("" + key, map.containsKey(key));

            int keyInt = (int) key;
            String valueInt = "Value-" + keyInt;
            map.put(keyInt, valueInt);
            assertTrue(map.containsKey(keyInt));

            assertEquals(value, map.get(key));
            assertEquals(valueInt, map.get(keyInt));

            assertEquals(value, map.remove(key));
            assertEquals(valueInt, map.remove(keyInt));

            assertNull(map.get(key));
            assertNull(map.get(keyInt));
        }
    }

}
