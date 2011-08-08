package de.greenrobot.dao.test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;

import de.greenrobot.dao.test.DateEntity;

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * DAO for table DATE_ENTITY (schema version 1).
*/
public class DateEntityDao extends AbstractDao<DateEntity, Long> {

    public static final String TABLENAME = "DATE_ENTITY";

    public static class Properties {
        public final static Property Id = new Property(0, "id", true, "_id");
        public final static Property Date = new Property(1, "date", false, "DATE");
        public final static Property DateNotNull = new Property(2, "dateNotNull", false, "DATE_NOT_NULL");
    };

    public DateEntityDao(SQLiteDatabase db) {
        super(db);
    }
    
    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "DATE_ENTITY (" + //
                "_id INTEGER PRIMARY KEY ," + // 0
                "DATE INTEGER," + // 1
                "DATE_NOT_NULL INTEGER NOT NULL );"; // 2
        db.execSQL(sql);
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "DATE_ENTITY";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DateEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(2, date.getTime());
        }
        stmt.bindLong(3, entity.getDateNotNull().getTime());
    }

    /** @inheritdoc */
    @Override
    public DateEntity readFrom(Cursor cursor, int offset) {
        DateEntity entity = new DateEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)), // date
            new java.util.Date(cursor.getLong(offset + 2)) // dateNotNull
        );
        return entity;
    }
    
    /** @inheritdoc */
    @Override
    public void readFrom(Cursor cursor, DateEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDate(cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)));
        entity.setDateNotNull(new java.util.Date(cursor.getLong(offset + 2)));
     }
    
    @Override
    protected void updateKeyAfterInsert(DateEntity entity, long rowId) {
        entity.setId(rowId);
    }
    
    /** @inheritdoc */
    @Override
    public Long getPrimaryKeyValue(DateEntity entity) {
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
