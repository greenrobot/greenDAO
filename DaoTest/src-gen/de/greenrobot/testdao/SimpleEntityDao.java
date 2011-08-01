package de.greenrobot.testdao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.orm.AbstractDao;
import de.greenrobot.orm.Column;

import de.greenrobot.testdao.SimpleEntity;

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * DAO for table SIMPLE_ENTITY (schema version 1).
*/
public class SimpleEntityDao extends AbstractDao<SimpleEntity, Long> {

    public static final String TABLENAME = "SIMPLE_ENTITY";

    public static Column[] COLUMN_MODEL = {
        new Column("_id", true),
        new Column("SIMPLE_BOOLEAN", false),
        new Column("SIMPLE_BYTE", false),
        new Column("SIMPLE_SHORT", false),
        new Column("SIMPLE_INT", false),
        new Column("SIMPLE_LONG", false),
        new Column("SIMPLE_FLOAT", false),
        new Column("SIMPLE_DOUBLE", false),
        new Column("SIMPLE_STRING", false),
        new Column("SIMPLE_BYTE_ARRAY", false)
    };

    public SimpleEntityDao(SQLiteDatabase db) {
        super(db);
    }
    
    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "SIMPLE_ENTITY (" + //
                "_id INTEGER PRIMARY KEY ," + // 0
                "SIMPLE_BOOLEAN INTEGER," + // 1
                "SIMPLE_BYTE INTEGER," + // 2
                "SIMPLE_SHORT INTEGER," + // 3
                "SIMPLE_INT INTEGER," + // 4
                "SIMPLE_LONG INTEGER," + // 5
                "SIMPLE_FLOAT REAL," + // 6
                "SIMPLE_DOUBLE REAL," + // 7
                "SIMPLE_STRING TEXT," + // 8
                "SIMPLE_BYTE_ARRAY BLOB)"; // 9
        db.execSQL(sql);
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "SIMPLE_ENTITY";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    public String getTablename() {
        return TABLENAME;
    }

    /** @inheritdoc */
    @Override
    protected Column[] getColumnModel() {
        return COLUMN_MODEL;
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SimpleEntity entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        Boolean simpleBoolean = entity.getSimpleBoolean();
        if (simpleBoolean != null) {
            stmt.bindLong(2, simpleBoolean ? 1l: 0l);
        }
        Byte simpleByte = entity.getSimpleByte();
        if (simpleByte != null) {
            stmt.bindLong(3, simpleByte);
        }
        Short simpleShort = entity.getSimpleShort();
        if (simpleShort != null) {
            stmt.bindLong(4, simpleShort);
        }
        Integer simpleInt = entity.getSimpleInt();
        if (simpleInt != null) {
            stmt.bindLong(5, simpleInt);
        }
        Long simpleLong = entity.getSimpleLong();
        if (simpleLong != null) {
            stmt.bindLong(6, simpleLong);
        }
        Float simpleFloat = entity.getSimpleFloat();
        if (simpleFloat != null) {
            stmt.bindDouble(7, simpleFloat);
        }
        Double simpleDouble = entity.getSimpleDouble();
        if (simpleDouble != null) {
            stmt.bindDouble(8, simpleDouble);
        }
        String simpleString = entity.getSimpleString();
        if (simpleString != null) {
            stmt.bindString(9, simpleString);
        }
        byte[] simpleByteArray = entity.getSimpleByteArray();
        if (simpleByteArray != null) {
            stmt.bindBlob(10, simpleByteArray);
        }
    }

    /** @inheritdoc */
    @Override
    public SimpleEntity readFrom(Cursor cursor) {
        SimpleEntity entity = new SimpleEntity();
        if (!cursor.isNull(0)) {
            entity.setId(cursor.getLong(0));
        }
        if (!cursor.isNull(1)) {
            entity.setSimpleBoolean(cursor.getShort(1) != 0);
        }
        if (!cursor.isNull(2)) {
            entity.setSimpleByte((byte) cursor.getShort(2));
        }
        if (!cursor.isNull(3)) {
            entity.setSimpleShort(cursor.getShort(3));
        }
        if (!cursor.isNull(4)) {
            entity.setSimpleInt(cursor.getInt(4));
        }
        if (!cursor.isNull(5)) {
            entity.setSimpleLong(cursor.getLong(5));
        }
        if (!cursor.isNull(6)) {
            entity.setSimpleFloat(cursor.getFloat(6));
        }
        if (!cursor.isNull(7)) {
            entity.setSimpleDouble(cursor.getDouble(7));
        }
        if (!cursor.isNull(8)) {
            entity.setSimpleString(cursor.getString(8));
        }
        if (!cursor.isNull(9)) {
            entity.setSimpleByteArray(cursor.getBlob(9));
        }
        return entity;
    }
    
    @Override
    protected void updateKeyAfterInsert(SimpleEntity entity, long rowId) {
        entity.setId(rowId);
    }
    
    /** @inheritdoc */
    @Override
    public Long getPrimaryKeyValue(SimpleEntity entity) {
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
