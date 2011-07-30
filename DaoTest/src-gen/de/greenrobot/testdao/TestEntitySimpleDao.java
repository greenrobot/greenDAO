package de.greenrobot.testdao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.orm.AbstractDao;

import de.greenrobot.testdao.TestEntitySimple;

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * DAO for table TEST_ENTITY_SIMPLE (schema version 1).
*/
public class TestEntitySimpleDao extends AbstractDao<TestEntitySimple, Long> {

    public static final String TABLENAME = "TEST_ENTITY_SIMPLE";
    
    protected static final String ALL_COLUMNS_SQL = 
        "_id,SIMPLE_INT,SIMPLE_INT_NOT_NULL,SIMPLE_LONG,SIMPLE_LONG_NOT_NULL,SIMPLE_STRING," +
        "SIMPLE_STRING_NOT_NULL";
    
    protected static final String PK_COLUMNS_SQL = 
        "_id";
    
    protected static final String NON_PK_COLUMNS_SQL = 
        "SIMPLE_INT,SIMPLE_INT_NOT_NULL,SIMPLE_LONG,SIMPLE_LONG_NOT_NULL,SIMPLE_STRING,SIMPLE_STRING_NOT_NULL";
    
    protected static final String VALUE_PLACEHOLDERS = "?,?,?,?,?,?,?";

    public enum Columns {
        /** Maps to property id (#0). */
        _id,
        /** Maps to property simpleInt (#1). */
        SIMPLE_INT,
        /** Maps to property simpleIntNotNull (#2). */
        SIMPLE_INT_NOT_NULL,
        /** Maps to property simpleLong (#3). */
        SIMPLE_LONG,
        /** Maps to property simpleLongNotNull (#4). */
        SIMPLE_LONG_NOT_NULL,
        /** Maps to property simpleString (#5). */
        SIMPLE_STRING,
        /** Maps to property simpleStringNotNull (#6). */
        SIMPLE_STRING_NOT_NULL
    }

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
    protected String getTablename() {
        return TABLENAME;
    }

    /** @inheritdoc */
    @Override
    protected String getValuePlaceholders() {
        return VALUE_PLACEHOLDERS;
    }

    /** @inheritdoc */
    @Override
    protected String getAllColumnsSql() {
        return ALL_COLUMNS_SQL;
    }

    /** @inheritdoc */
    @Override
    protected String getPkColumnsSql() {
        return PK_COLUMNS_SQL;
    }

    /** @inheritdoc */
    @Override
    protected String getNonPkColumnsSql() {
        return NON_PK_COLUMNS_SQL;
    }

    /** @inheritdoc */
    protected void bindValues(SQLiteStatement stmt, TestEntitySimple entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindLong(2, entity.getSimpleInt());
        stmt.bindLong(3, entity.getSimpleIntNotNull());
        stmt.bindLong(4, entity.getSimpleLong());
        stmt.bindLong(5, entity.getSimpleLongNotNull());
        stmt.bindString(6, entity.getSimpleString());
        stmt.bindString(7, entity.getSimpleStringNotNull());
    }

    /** @inheritdoc */
    public TestEntitySimple readFrom(Cursor cursor) {
        TestEntitySimple entity = new TestEntitySimple();
        entity.setId(cursor.getInt(0));
        entity.setSimpleInt(cursor.getInt(1));
        entity.setSimpleIntNotNull(cursor.getInt(2));
        entity.setSimpleLong(cursor.getLong(3));
        entity.setSimpleLongNotNull(cursor.getLong(4));
        entity.setSimpleString(cursor.getString(5));
        entity.setSimpleStringNotNull(cursor.getString(6));
        return entity;
    }
    
    protected void updateKeyAfterInsert(TestEntitySimple entity, long rowId) {
        // TODO updateKeyAfterInsert
    }
    
    @Override
    /** @inheritdoc */
    protected String getPkColumn() {
        return null;
    }
}
