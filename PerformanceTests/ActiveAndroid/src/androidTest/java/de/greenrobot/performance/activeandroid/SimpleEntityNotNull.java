package de.greenrobot.performance.activeandroid;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Entity mapped to table SIMPLE_ENTITY_NOT_NULL.
 */
@Table(name = "SIMPLE_ENTITY_NOT_NULL")
public class SimpleEntityNotNull extends Model {

    @Column(name = "SIMPLE_BOOLEAN")
    public boolean simpleBoolean;
    @Column(name = "SIMPLE_BYTE")
    public byte simpleByte;
    @Column(name = "SIMPLE_SHORT")
    public short simpleShort;
    @Column(name = "SIMPLE_INT")
    public int simpleInt;
    @Column(name = "SIMPLE_LONG")
    public long simpleLong;
    @Column(name = "SIMPLE_FLOAT")
    public float simpleFloat;
    @Column(name = "SIMPLE_DOUBLE")
    public double simpleDouble;
    @Column(name = "SIMPLE_STRING")
    public String simpleString;
    @Column(name = "SIMPLE_BYTE_ARRAY")
    public byte[] simpleByteArray;

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
