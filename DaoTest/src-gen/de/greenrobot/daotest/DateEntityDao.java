package de.greenrobot.daotest;

import android.database.Cursor;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.wrapper.SQLiteDatabaseWrapper;
import de.greenrobot.dao.wrapper.SQLiteStatementWrapper;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table DATE_ENTITY.
*/
public class DateEntityDao extends AbstractDao<DateEntity, Long> {

    public static final String TABLENAME = "DATE_ENTITY";

    /**
     * Properties of entity DateEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Date = new Property(1, java.util.Date.class, "date", false, "DATE");
        public final static Property DateNotNull = new Property(2, java.util.Date.class, "dateNotNull", false, "DATE_NOT_NULL");
    };


    public DateEntityDao(DaoConfig config) {
        super(config);
    }
    
    public DateEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabaseWrapper db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DATE_ENTITY' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'DATE' INTEGER," + // 1: date
                "'DATE_NOT_NULL' INTEGER NOT NULL );"); // 2: dateNotNull
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabaseWrapper db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DATE_ENTITY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatementWrapper stmt, DateEntity entity) {
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
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public DateEntity readEntity(Cursor cursor, int offset) {
        DateEntity entity = new DateEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)), // date
            new java.util.Date(cursor.getLong(offset + 2)) // dateNotNull
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DateEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDate(cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)));
        entity.setDateNotNull(new java.util.Date(cursor.getLong(offset + 2)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DateEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(DateEntity entity) {
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
