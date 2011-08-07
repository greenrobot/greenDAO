package de.greenrobot.testdao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;

import de.greenrobot.testdao.SimpleEntityNotNull;

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * DAO for table SIMPLE_ENTITY_NOT_NULL (schema version 1).
*/
public class SimpleEntityNotNullDao extends AbstractDao<SimpleEntityNotNull, Long> {

    public static final String TABLENAME = "SIMPLE_ENTITY_NOT_NULL";

    public static class Properties {
        public final static Property Id = new Property(0, "id", true, "_id");
        public final static Property SimpleBoolean = new Property(1, "simpleBoolean", false, "SIMPLE_BOOLEAN");
        public final static Property SimpleByte = new Property(2, "simpleByte", false, "SIMPLE_BYTE");
        public final static Property SimpleShort = new Property(3, "simpleShort", false, "SIMPLE_SHORT");
        public final static Property SimpleInt = new Property(4, "simpleInt", false, "SIMPLE_INT");
        public final static Property SimpleLong = new Property(5, "simpleLong", false, "SIMPLE_LONG");
        public final static Property SimpleFloat = new Property(6, "simpleFloat", false, "SIMPLE_FLOAT");
        public final static Property SimpleDouble = new Property(7, "simpleDouble", false, "SIMPLE_DOUBLE");
        public final static Property SimpleString = new Property(8, "simpleString", false, "SIMPLE_STRING");
        public final static Property SimpleByteArray = new Property(9, "simpleByteArray", false, "SIMPLE_BYTE_ARRAY");
    };

    public SimpleEntityNotNullDao(SQLiteDatabase db) {
        super(db);
    }
    
    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "SIMPLE_ENTITY_NOT_NULL (" + //
                "_id INTEGER PRIMARY KEY NOT NULL ," + // 0
                "SIMPLE_BOOLEAN INTEGER NOT NULL ," + // 1
                "SIMPLE_BYTE INTEGER NOT NULL ," + // 2
                "SIMPLE_SHORT INTEGER NOT NULL ," + // 3
                "SIMPLE_INT INTEGER NOT NULL ," + // 4
                "SIMPLE_LONG INTEGER NOT NULL ," + // 5
                "SIMPLE_FLOAT REAL NOT NULL ," + // 6
                "SIMPLE_DOUBLE REAL NOT NULL ," + // 7
                "SIMPLE_STRING TEXT NOT NULL ," + // 8
                "SIMPLE_BYTE_ARRAY BLOB NOT NULL );"; // 9
        db.execSQL(sql);
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "SIMPLE_ENTITY_NOT_NULL";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    public String getTablename() {
        return TABLENAME;
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SimpleEntityNotNull entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindLong(2, entity.getSimpleBoolean() ? 1l: 0l);
        stmt.bindLong(3, entity.getSimpleByte());
        stmt.bindLong(4, entity.getSimpleShort());
        stmt.bindLong(5, entity.getSimpleInt());
        stmt.bindLong(6, entity.getSimpleLong());
        stmt.bindDouble(7, entity.getSimpleFloat());
        stmt.bindDouble(8, entity.getSimpleDouble());
        stmt.bindString(9, entity.getSimpleString());
        stmt.bindBlob(10, entity.getSimpleByteArray());
    }

    /** @inheritdoc */
    @Override
    public SimpleEntityNotNull readFrom(Cursor cursor) {
        SimpleEntityNotNull entity = new SimpleEntityNotNull( //
            cursor.getLong(0), // id
            cursor.getShort(1) != 0, // simpleBoolean
            (byte) cursor.getShort(2), // simpleByte
            cursor.getShort(3), // simpleShort
            cursor.getInt(4), // simpleInt
            cursor.getLong(5), // simpleLong
            cursor.getFloat(6), // simpleFloat
            cursor.getDouble(7), // simpleDouble
            cursor.getString(8), // simpleString
            cursor.getBlob(9) // simpleByteArray
        );
        return entity;
    }
    
    /** @inheritdoc */
    @Override
    public void readFrom(Cursor cursor, SimpleEntityNotNull entity) {
        entity.setId(cursor.getLong(0));
        entity.setSimpleBoolean(cursor.getShort(1) != 0);
        entity.setSimpleByte((byte) cursor.getShort(2));
        entity.setSimpleShort(cursor.getShort(3));
        entity.setSimpleInt(cursor.getInt(4));
        entity.setSimpleLong(cursor.getLong(5));
        entity.setSimpleFloat(cursor.getFloat(6));
        entity.setSimpleDouble(cursor.getDouble(7));
        entity.setSimpleString(cursor.getString(8));
        entity.setSimpleByteArray(cursor.getBlob(9));
     }
    
    @Override
    protected void updateKeyAfterInsert(SimpleEntityNotNull entity, long rowId) {
        entity.setId(rowId);
    }
    
    /** @inheritdoc */
    @Override
    public Long getPrimaryKeyValue(SimpleEntityNotNull entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }

}
