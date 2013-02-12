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
package de.greenrobot.daotest.performance;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;
import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.internal.LongHashMap;
import de.greenrobot.daotest.performance.target.LongHashMapAmarena2DZechner;
import de.greenrobot.daotest.performance.target.LongHashMapJDBM;
import de.greenrobot.daotest.performance.target.LongSparseArray;

public class LoockupPerformanceTest extends TestCase {

    Random random;
    private String traceName;
    private long start;

    public LoockupPerformanceTest() {
        this.random = new Random();
    }

    public void testHashMapPerformance() {
        // runTests(100);
        // runTests(1000);
        // runTests(10000);
        // runTests(100000); // hash: 1485/420ms; sparse: 148196/196ms
        DaoLog.d("testHashMapPerformance DONE");
    }

    private void runTests(int count) {
        runTests(count, false);
        runTests(count, true);
    }

    private void runTests(int count, boolean randomKeys) {
        DaoLog.d("-----------------------------------");
        DaoLog.d("Look up " + count + (randomKeys ? " random" : " linear") + " keys on " + new Date());
        DaoLog.d("-----------------------------------");
        long[] keys = new long[count];
        for (int i = 0; i < count; i++) {
            if (randomKeys) {
                keys[i] = random.nextLong();
            } else {
                keys[i] = i;
            }
        }
        for (int i = 0; i < 3; i++) {
            runMapTest(new HashMap<Long, Object>(count), keys, "hashmap");
            // runMapTest(new WeakHashMap<Long, Object>(count), keys, "weakhashmap");
            // runMapTest(new ConcurrentHashMap<Long, Object>(count), keys, "concurrent-hashmap");
            // runLongSparseArrayTest(keys);
            runLongHashMap(keys);
            runLongHashMapAmarena2DZechnerTest(keys);
            // runLongHashMapJDBMTest(keys);
            DaoLog.d("-----------------------------------");
        }
    }

    protected void runMapTest(Map<Long, Object> map, long[] keys, String name) {
        startClock("put-" + name + "-" + keys.length);
        for (long key : keys) {
            map.put(key, this);
        }
        stopClock();

        startClock("get-" + name + "-" + keys.length);
        int lossCount = 0;
        for (long key : keys) {
            Object object = map.get(key);
            if (object != this) {
                lossCount++;
            }
        }
        if (lossCount > 0) {
            stopClock("losses: " + lossCount);
        } else {
            stopClock();
        }
    }

    private void runLongSparseArrayTest(long[] keys) {
        if (keys.length > 10000) {
            DaoLog.d("Skipping runLongSparseArrayTest for " + keys.length);
            return;
        }
        LongSparseArray<Object> array = new LongSparseArray<Object>(keys.length);

        startClock("put-sparsearray-" + keys.length);
        for (long key : keys) {
            array.put(key, this);
        }
        stopClock();

        startClock("get-sparsearray-" + keys.length);
        for (long key : keys) {
            Object object = array.get(key);
            if (object != this) {
                fail("Ups: " + object);
            }
        }
        stopClock();
    }

    private void runLongHashMapJDBMTest(long[] keys) {
        LongHashMapJDBM<Object> map = new LongHashMapJDBM<Object>(keys.length);

        startClock("put-jdbm-" + keys.length);
        for (long key : keys) {
            map.put(key, this);
        }
        stopClock();

        startClock("get-jdbm-" + keys.length);
        for (long key : keys) {
            Object object = map.get(key);
            if (object != this) {
                fail("Ups: " + object);
            }
        }
        stopClock();
    }

    private void runLongHashMap(long[] keys) {
        LongHashMap<Object> map = new LongHashMap<Object>(keys.length);
        map.reserveRoom(keys.length);

        startClock("put-my-" + keys.length);
        for (long key : keys) {
            map.put(key, this);
        }
        stopClock();

        startClock("get-my-" + keys.length);
        for (long key : keys) {
            Object object = map.get(key);
            if (object != this) {
                fail("Ups: " + object);
            }
        }
        stopClock();
        map.logStats();
    }

    private void runLongHashMapAmarena2DZechnerTest(long[] keys) {
        LongHashMapAmarena2DZechner<Object> map = new LongHashMapAmarena2DZechner<Object>(keys.length);

        startClock("put-amarena-" + keys.length);
        for (long key : keys) {
            map.put(key, this);
        }
        stopClock();

        startClock("get-amarena-" + keys.length);
        for (long key : keys) {
            Object object = map.get(key);
            if (object != this) {
                fail("Ups: " + object);
            }
        }
        stopClock();
        map.logStats();
    }

    protected void startClock(String traceName) {
        this.traceName = traceName;
        start = System.currentTimeMillis();
    }

    protected void stopClock() {
        stopClock(null);
    }

    protected void stopClock(String extraInfoOrNull) {
        long time = System.currentTimeMillis() - start;
        String extraLog = extraInfoOrNull != null ? " (" + extraInfoOrNull + ")" : "";
        DaoLog.d(traceName + " completed in " + time + "ms" + extraLog);
    }

}
