package de.greenrobot.daoexample;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Column;

import de.greenrobot.daoexample.Note;

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * DAO for table NOTE (schema version 1).
*/
public class NoteDao extends AbstractDao<Note, Long> {

    public static final String TABLENAME = "NOTE";

    public static Column[] COLUMN_MODEL = {
        new Column("_id", true),
        new Column("TEXT", false),
        new Column("DATE", false)
    };

    public NoteDao(SQLiteDatabase db) {
        super(db);
    }
    
    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "NOTE (" + //
                "_id INTEGER PRIMARY KEY ," + // 0
                "TEXT TEXT NOT NULL ," + // 1
                "DATE TEXT NOT NULL );"; // 2
        db.execSQL(sql);
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "NOTE";
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
    protected void bindValues(SQLiteStatement stmt, Note entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getText());
        stmt.bindString(3, entity.getDate());
    }

    /** @inheritdoc */
    @Override
    public Note readFrom(Cursor cursor) {
        return new Note( //
            cursor.isNull(0) ? null : cursor.getLong(0), // id
            cursor.getString(1), // text
            cursor.getString(2) // date
        );
    }
    
    @Override
    protected void updateKeyAfterInsert(Note entity, long rowId) {
        entity.setId(rowId);
    }
    
    /** @inheritdoc */
    @Override
    public Long getPrimaryKeyValue(Note entity) {
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
