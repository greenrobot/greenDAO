package de.greenrobot.performance.ormlite;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/** 
 * Entity mapped to table SIMPLE_ENTITY_NOT_NULL (schema version 1).
*/
@DatabaseTable(tableName = "SIMPLE_ENTITY_NOT_NULL")
public class SimpleEntityNotNull {

    @DatabaseField(id = true, columnName="_id")
    private long id;
    
    @DatabaseField(columnName="SIMPLE_BOOLEAN")
    private boolean simpleBoolean;
    @DatabaseField(columnName="SIMPLE_BYTE")
    private byte simpleByte;
    @DatabaseField(columnName="SIMPLE_SHORT")
    private short simpleShort;
    @DatabaseField(columnName="SIMPLE_INT")
    private int simpleInt;
    @DatabaseField(columnName="SIMPLE_LONG")
    private long simpleLong;
    @DatabaseField(columnName="SIMPLE_FLOAT")
    private float simpleFloat;
    @DatabaseField(columnName="SIMPLE_DOUBLE")
    private double simpleDouble;
    @DatabaseField(columnName="SIMPLE_STRING")
    private String simpleString;
    @DatabaseField(dataType=DataType.BYTE_ARRAY,columnName="SIMPLE_BYTE_ARRAY")
    private byte[] simpleByteArray; 
    
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
