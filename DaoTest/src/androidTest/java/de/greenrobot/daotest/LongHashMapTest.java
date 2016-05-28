/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * This file is part of greenDAO Generator.
 * 
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.greenrobot.daotest;

import java.util.Random;

import junit.framework.TestCase;
import de.greenrobot.dao.internal.LongHashMap;

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
