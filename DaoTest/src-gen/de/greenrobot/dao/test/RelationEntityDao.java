package de.greenrobot.dao.test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;

import de.greenrobot.dao.test.RelationEntity;

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * DAO for table RELATION_ENTITY (schema version 1).
*/
public class RelationEntityDao extends AbstractDao<RelationEntity, Long> {

    public static final String TABLENAME = "RELATION_ENTITY";

    public static class Properties {
        public final static Property Id = new Property(0, "id", true, "_id");
        public final static Property ParentId = new Property(1, "parentId", false, "PARENT_ID");
        public final static Property TestId = new Property(2, "testId", false, "TEST_ID");
    };

    private DaoMaster daoMaster;

    public RelationEntityDao(SQLiteDatabase db, DaoMaster daoMaster) {
        super(db);
        this.daoMaster = daoMaster;
    }

    public RelationEntityDao(SQLiteDatabase db) {
        super(db);
    }
    
    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "RELATION_ENTITY (" + //
                "_id INTEGER PRIMARY KEY ," + // 0
                "PARENT_ID INTEGER," + // 1
                "TEST_ID INTEGER);"; // 2
        db.execSQL(sql);
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "RELATION_ENTITY";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    public String getTablename() {
        return TABLENAME;
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, RelationEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long parentId = entity.getParentId();
        if (parentId != null) {
            stmt.bindLong(2, parentId);
        }
 
        Long testId = entity.getTestId();
        if (testId != null) {
            stmt.bindLong(3, testId);
        }
    }

    /** @inheritdoc */
    @Override
    public RelationEntity readFrom(Cursor cursor) {
        RelationEntity entity = new RelationEntity( //
            cursor.isNull(0) ? null : cursor.getLong(0), // id
            cursor.isNull(1) ? null : cursor.getLong(1), // parentId
            cursor.isNull(2) ? null : cursor.getLong(2) // testId
        );
        entity.__setDaoMaster(daoMaster);
        return entity;
    }
    
    /** @inheritdoc */
    @Override
    public void readFrom(Cursor cursor, RelationEntity entity) {
        entity.setId(cursor.isNull(0) ? null : cursor.getLong(0));
        entity.setParentId(cursor.isNull(1) ? null : cursor.getLong(1));
        entity.setTestId(cursor.isNull(2) ? null : cursor.getLong(2));
     }
    
    @Override
    protected void updateKeyAfterInsert(RelationEntity entity, long rowId) {
        entity.setId(rowId);
    }
    
    /** @inheritdoc */
    @Override
    public Long getPrimaryKeyValue(RelationEntity entity) {
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
