package de.greenrobot.testdao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.orm.AbstractDao;
import de.greenrobot.orm.Column;

import de.greenrobot.testdao.TestEntitySimple;

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * DAO for table TEST_ENTITY_SIMPLE (schema version 1).
*/
public class TestEntitySimpleDao extends AbstractDao<TestEntitySimple, Integer> {

    public static final String TABLENAME = "TEST_ENTITY_SIMPLE";

    public static Column[] COLUMN_MODEL = {
        new Column("_id", true),
        new Column("SIMPLE_INT", false),
        new Column("SIMPLE_INT_NOT_NULL", false),
        new Column("SIMPLE_LONG", false),
        new Column("SIMPLE_LONG_NOT_NULL", false),
        new Column("SIMPLE_STRING", false),
        new Column("SIMPLE_STRING_NOT_NULL", false)
    };

    public TestEntitySimpleDao(SQLiteDatabase db) {
        super(db);
    }
    
    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "TEST_ENTITY_SIMPLE (" + //
            "_id INTEGER PRIMARY KEY ASC ," + // 0
            "SIMPLE_INT INTEGER," + // 1
            "SIMPLE_INT_NOT_NULL INTEGER NOT NULL ," + // 2
            "SIMPLE_LONG INTEGER," + // 3
            "SIMPLE_LONG_NOT_NULL INTEGER NOT NULL ," + // 4
            "SIMPLE_STRING TEXT," + // 5
            "SIMPLE_STRING_NOT_NULL TEXT NOT NULL )"; // 6
        db.execSQL(sql);
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "TEST_ENTITY_SIMPLE";
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
    protected void bindValues(SQLiteStatement stmt, TestEntitySimple entity) {
        stmt.clearBindings();
        Integer id = entity.getId();
        if(id != null) {
            stmt.bindLong(1, id);
        }
        Integer simpleInt = entity.getSimpleInt();
        if(simpleInt != null) {
            stmt.bindLong(2, simpleInt);
        }
        stmt.bindLong(3, entity.getSimpleIntNotNull());
        Long simpleLong = entity.getSimpleLong();
        if(simpleLong != null) {
            stmt.bindLong(4, simpleLong);
        }
        stmt.bindLong(5, entity.getSimpleLongNotNull());
        String simpleString = entity.getSimpleString();
        if(simpleString != null) {
            stmt.bindString(6, simpleString);
        }
        stmt.bindString(7, entity.getSimpleStringNotNull());
    }

    /** @inheritdoc */
    @Override
    public TestEntitySimple readFrom(Cursor cursor) {
        TestEntitySimple entity = new TestEntitySimple();
        if(!cursor.isNull(0))
        entity.setId(cursor.getInt(0));
        if(!cursor.isNull(1))
        entity.setSimpleInt(cursor.getInt(1));
        entity.setSimpleIntNotNull(cursor.getInt(2));
        if(!cursor.isNull(3))
        entity.setSimpleLong(cursor.getLong(3));
        entity.setSimpleLongNotNull(cursor.getLong(4));
        if(!cursor.isNull(5))
        entity.setSimpleString(cursor.getString(5));
        entity.setSimpleStringNotNull(cursor.getString(6));
        return entity;
    }
    
    @Override
    protected void updateKeyAfterInsert(TestEntitySimple entity, long rowId) {
        // TODO updateKeyAfterInsert
    }
    
    /** @inheritdoc */
    @Override
    public Integer getPrimaryKeyValue(TestEntitySimple entity) {
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
