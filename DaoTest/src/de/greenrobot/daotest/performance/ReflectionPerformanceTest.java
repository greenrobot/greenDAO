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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.greenrobot.dao.DaoLog;

public class ReflectionPerformanceTest // extends TestCase
{

    int intValue;
    String stringValue;

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int inValue) {
        this.intValue = inValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public void testSetIntPerf() throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        int count = 100000;

        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            setIntValue(i);
        }
        long time = System.currentTimeMillis() - start;

        Method method = getClass().getMethod("setIntValue", int.class);
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            method.invoke(this, i);
        }
        long time2 = System.currentTimeMillis() - start2;
        DaoLog.d("set int: normal=" + time + "ms, reflected=" + time2 + "ms, " + 1000 * count / time2
                + " refelected ops/s, slower=" + ((float) time2) / time);

    }

    public void testGetIntPerf() throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        int count = 100000;

        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            int x = getIntValue();
        }
        long time = System.currentTimeMillis() - start;

        Method method = getClass().getMethod("getIntValue");
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            int x = (Integer) method.invoke(this);
        }
        long time2 = System.currentTimeMillis() - start2;
        DaoLog.d("get int: normal=" + time + "ms, reflected=" + time2 + "ms, " + 1000 * count / time2
                + " refelected ops/s, slower=" + ((float) time2) / time);
    }

    public void testSetStringPerf() throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        int count = 100000;

        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            setStringValue("the quick brown fox");
        }
        long time = System.currentTimeMillis() - start;

        Method method = getClass().getMethod("setStringValue", String.class);
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            method.invoke(this, "the quick brown fox");
        }
        long time2 = System.currentTimeMillis() - start2;
        DaoLog.d("set String: normal=" + time + "ms, reflected=" + time2 + "ms, " + 1000 * count / time2
                + " refelected ops/s, slower=" + ((float) time2) / time);

    }

    public void testGetStringPerf() throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        int count = 100000;

        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            String x = getStringValue();
        }
        long time = System.currentTimeMillis() - start;

        Method method = getClass().getMethod("getStringValue");
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            String x = (String) method.invoke(this);
        }
        long time2 = System.currentTimeMillis() - start2;
        DaoLog.d("get String: normal=" + time + "ms, reflected=" + time2 + "ms, " + 1000 * count / time2
                + " refelected ops/s, slower=" + ((float) time2) / time);
    }
}
