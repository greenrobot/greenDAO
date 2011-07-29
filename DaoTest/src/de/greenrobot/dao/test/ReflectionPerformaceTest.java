package de.greenrobot.dao.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;
import android.util.Log;

public class ReflectionPerformaceTest extends TestCase {

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
        Log.d("Perf", "set int: normal=" + time + "ms, reflected=" + time2 + "ms, " + 1000 * count / time2
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
        Log.d("Perf", "get int: normal=" + time + "ms, reflected=" + time2 + "ms, " + 1000 * count / time2
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
        Log.d("Perf", "set String: normal=" + time + "ms, reflected=" + time2 + "ms, " + 1000 * count / time2
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
        Log.d("Perf", "get String: normal=" + time + "ms, reflected=" + time2 + "ms, " + 1000 * count / time2
                + " refelected ops/s, slower=" + ((float) time2) / time);
    }
}
