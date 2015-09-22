package de.greenrobot.performance.firebase;

import java.util.Arrays;
import java.util.List;

/**
 * Simple entity for performance testing.
 */
public class SimpleEntityNotNull {

    // Only JSON supported equivalent types are available for Firebase as it uses JSON to store data
    // use int instead of short
    // use List<Byte> instead of byte[]

    private long id;

    private boolean simpleBoolean;
    private byte simpleByte;
    private int simpleShort;
    private int simpleInt;
    private long simpleLong;
    private float simpleFloat;
    private double simpleDouble;
    /** Not-null value. */
    private String simpleString;
    /** Not-null value. */
    private List<Byte> simpleByteArray;

    public SimpleEntityNotNull() {
        // empty default constructor, necessary for Firebase to be able to deserialize
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getSimpleBoolean() {
        return simpleBoolean;
    }

    public void setSimpleBoolean(boolean simpleBoolean) {
        this.simpleBoolean = simpleBoolean;
    }

    public byte getSimpleByte() {
        return simpleByte;
    }

    public void setSimpleByte(byte simpleByte) {
        this.simpleByte = simpleByte;
    }

    public int getSimpleShort() {
        return simpleShort;
    }

    public void setSimpleShort(int simpleShort) {
        this.simpleShort = simpleShort;
    }

    public int getSimpleInt() {
        return simpleInt;
    }

    public void setSimpleInt(int simpleInt) {
        this.simpleInt = simpleInt;
    }

    public long getSimpleLong() {
        return simpleLong;
    }

    public void setSimpleLong(long simpleLong) {
        this.simpleLong = simpleLong;
    }

    public float getSimpleFloat() {
        return simpleFloat;
    }

    public void setSimpleFloat(float simpleFloat) {
        this.simpleFloat = simpleFloat;
    }

    public double getSimpleDouble() {
        return simpleDouble;
    }

    public void setSimpleDouble(double simpleDouble) {
        this.simpleDouble = simpleDouble;
    }

    public String getSimpleString() {
        return simpleString;
    }

    public void setSimpleString(String simpleString) {
        this.simpleString = simpleString;
    }

    public Byte[] getSimpleByteArray() {
        return simpleByteArray.toArray(new Byte[simpleByteArray.size()]);
    }

    public void setSimpleByteArray(Byte[] simpleByteArray) {
        this.simpleByteArray = Arrays.asList(simpleByteArray);
    }
}
