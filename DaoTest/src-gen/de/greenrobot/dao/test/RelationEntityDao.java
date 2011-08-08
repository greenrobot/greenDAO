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
    public RelationEntity readFrom(Cursor cursor, int offset) {
        RelationEntity entity = new RelationEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // parentId
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2) // testId
        );
        entity.__setDaoMaster(daoMaster);
        return entity;
    }
    
    /** @inheritdoc */
    @Override
    public void readFrom(Cursor cursor, RelationEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setParentId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setTestId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
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
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            appendCommaSeparated(builder, "T.", getAllColumns());
            builder.append(',');
            appendCommaSeparated(builder, "T0.", daoMaster.getRelationEntityDao().getAllColumns());
            builder.append(',');
            appendCommaSeparated(builder, "T1.", daoMaster.getTestEntityDao().getAllColumns());
            builder.append(" FROM RELATION_ENTITY T");
            builder.append(" LEFT JOIN RELATION_ENTITY T0 ON T.PARENT_ID=T0._id");
            builder.append(" LEFT JOIN TEST_ENTITY T1 ON T.TEST_ID=T1._id");
            builder.append(" WHERE ");
            appendCommaSeparatedEqPlaceholder(builder, "T.", getPkColumns());

            selectDeep = builder.toString();
        }
        return selectDeep;
    }

    public RelationEntity loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        String sql = getSelectDeep();
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);

        boolean available = cursor.moveToFirst();
        if (!available) {
            return null;
        } else if (!cursor.isLast()) {
            throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
        }
        RelationEntity entity = readFrom(cursor, 0);
        int offset = getAllColumns().length;
        entity.setRelationEntity(daoMaster.getRelationEntityDao().readFrom(cursor, offset));
        offset += daoMaster.getRelationEntityDao().getAllColumns().length;
        entity.setTestEntity(daoMaster.getTestEntityDao().readFrom(cursor, offset));
        return entity;
    }
}
