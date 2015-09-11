package de.greenrobot.performance.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Simple entity for performance testing.
 */
public class SimpleEntityNotNull extends RealmObject {

    @PrimaryKey
    private long id;

    private boolean simpleBoolean;
    private byte simpleByte;
    private short simpleShort;
    private int simpleInt;
    private long simpleLong;
    private float simpleFloat;
    private double simpleDouble;
    /** Not-null value. */
    private String simpleString;
    /** Not-null value. */
    private byte[] simpleByteArray;

    // Be aware that the getters and setters will be overridden by the generated proxy class
    // used in the back by RealmObjects, so any custom logic you add to the getters & setters
    // will not actually be executed
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

    public short getSimpleShort() {
        return simpleShort;
    }

    public void setSimpleShort(short simpleShort) {
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

    public byte[] getSimpleByteArray() {
        return simpleByteArray;
    }

    public void setSimpleByteArray(byte[] simpleByteArray) {
        this.simpleByteArray = simpleByteArray;
    }
}
