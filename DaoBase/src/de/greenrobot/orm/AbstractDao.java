package de.greenrobot.orm;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * Base class for all DAOs. Implements some operations.
 * 
 * @author Markus
 * 
 * @param <T>
 *            Entity type
 * @param <K>
 *            Primary key type; use Void if entity does not have one
 */
public abstract class AbstractDao<T, K> {
    protected final SQLiteDatabase db;

    protected SQLiteStatement insertStatement;
    protected SQLiteStatement insertOrReplaceStatement;
    protected SQLiteStatement updateStatement;
    protected SQLiteStatement deleteStatement;

    private volatile String selectAll;
    private volatile String selectByKey;
    private volatile String selectByRowId;

    private final String[] allColumns;
    private final String[] pkColumns;
    private final String[] nonPkColumns;

    public AbstractDao(SQLiteDatabase db) {
        this.db = db;
        Column[] columns = getColumnModel();
        allColumns = new String[columns.length];

        List<String> pkColumnList = new ArrayList<String>();
        List<String> nonPkColumnList = new ArrayList<String>();
        for (int i = 0; i < columns.length; i++) {
            Column column = columns[i];
            String name = column.name;
            allColumns[i] = name;
            if (column.primaryKey) {
                pkColumnList.add(name);
            } else {
                nonPkColumnList.add(name);
            }
        }
        String[] nonPkColumnsArray = new String[nonPkColumnList.size()];
        nonPkColumns = nonPkColumnList.toArray(nonPkColumnsArray);
        String[] pkColumnsArray = new String[pkColumnList.size()];
        pkColumns = pkColumnList.toArray(pkColumnsArray);
    }

    protected void apppendCommaSeparated(StringBuilder builder, String[] values) {
        int length = values.length;
        for (int i = 0; i < length; i++) {
            builder.append(values[i]);
            if (i < length - 1) {
                builder.append(',');
            }
        }
    }

    protected void apppendPlaceholders(StringBuilder builder, int count) {
        for (int i = 0; i < count; i++) {
            if (i < count - 1) {
                builder.append("?,");
            } else {
                builder.append('?');
            }
        }
    }

    protected SQLiteStatement getInsertStatement() {
        if (insertStatement == null) {
            String sql = createSqlForInsert("INSERT INTO ");
            insertStatement = db.compileStatement(sql);
        }
        return insertStatement;
    }

    protected SQLiteStatement getInsertOrReplaceStatement() {
        if (insertOrReplaceStatement == null) {
            String sql = createSqlForInsert("INSERT OR REPLACE INTO ");
            insertOrReplaceStatement = db.compileStatement(sql);
        }
        return insertOrReplaceStatement;
    }

    protected String createSqlForInsert(String insertInto) {
        StringBuilder builder = new StringBuilder(insertInto);
        builder.append(getTablename()).append(" (");
        apppendCommaSeparated(builder, allColumns);
        builder.append(") VALUES (");
        apppendPlaceholders(builder, allColumns.length);
        builder.append(')');
        return builder.toString();
    }

    /** ends with an space to simplify appending to this string. */
    protected String getSelectAll() {
        if (selectAll == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            apppendCommaSeparated(builder, allColumns);
            builder.append(" FROM ").append(getTablename()).append(' ');
            selectAll = builder.toString();
        }
        return selectAll;
    }

    protected String getSelectByKey() {
        if (selectByKey == null) {
            StringBuilder builder = new StringBuilder(getSelectAll());
            builder.append("WHERE ");
            apppendCommaSeparated(builder, pkColumns);
            builder.append('=');
            apppendPlaceholders(builder, pkColumns.length);
            selectByKey=builder.toString();
        }
        return selectByKey;
    }

    public String[] getAllColumns() {
        return allColumns;
    }

    public String[] getPkColumns() {
        return pkColumns;
    }

    public String[] getNonPkColumns() {
        return nonPkColumns;
    }

    public T load(K key) {
        assertSinglePk();
        String sql = getSelectByKey();
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        return readUniqueAndCloseCursor(cursor);
    }

    public T loadByRowId(long rowId) {
        if (selectByRowId == null) {
            selectByRowId = getSelectAll() + "WHERE ROWID=?";
        }
        String[] idArray = new String[] { Long.toString(rowId) };
        Cursor cursor = db.rawQuery(selectByRowId, idArray);
        return readUniqueAndCloseCursor(cursor);
    }

    protected T readUniqueAndCloseCursor(Cursor cursor) {
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return readFrom(cursor);
        } finally {
            cursor.close();
        }
    }

    public List<T> loadAll() {
        Cursor cursor = db.rawQuery(getSelectAll(), null);
        return readAllAndCloseCursor(cursor);
    }

    protected List<T> readAllAndCloseCursor(Cursor cursor) {
        try {
            return readAllFrom(cursor);
        } finally {
            cursor.close();
        }
    }

    public void insertInTx(Iterable<T> entities) {
        insertInTx(entities, isEntityUpdateable());
    }

    public void insertInTx(Iterable<T> entities, boolean setPrimaryKey) {
        SQLiteStatement stmt = getInsertStatement();
        synchronized (stmt) {
            db.beginTransaction();
            try {
                for (T entity : entities) {
                    insert(entity, setPrimaryKey);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    /** Insert an entity into the table associated with a concrete DAO. */
    public long insert(T entity) {
        return insert(entity, isEntityUpdateable());
    }

    /** Insert an entity into the table associated with a concrete DAO. */
    public long insert(T entity, boolean setPrimaryKey) {
        SQLiteStatement stmt = getInsertStatement();
        synchronized (stmt) {
            bindValues(stmt, entity);
            if (setPrimaryKey) {
                long rowId = stmt.executeInsert();
                updateKeyAfterInsert(entity, rowId);
                return rowId;
            } else {
                stmt.execute();
                return 0;
            }
        }
    }

    /** Insert an entity into the table associated with a concrete DAO. */
    public void insertWithoutSettingPk(T entity) {
        SQLiteStatement stmt = getInsertStatement();
        synchronized (stmt) {
            bindValues(stmt, entity);
            stmt.execute();
        }
    }

    /** Insert an entity into the table associated with a concrete DAO. */
    public long insertOrReplace(T entity) {
        SQLiteStatement stmt = getInsertOrReplaceStatement();
        synchronized (stmt) {
            bindValues(stmt, entity);
            long rowId = stmt.executeInsert();
            updateKeyAfterInsert(entity, rowId);
            return rowId;
        }
    }

    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<T> readAllFrom(Cursor cursor) {
        List<T> list = new ArrayList<T>();
        if (cursor.moveToFirst()) {
            do {
                list.add(readFrom(cursor));
            } while (cursor.moveToNext());
        }
        return list;
    }

    /** SUBJECT TO CHANGE: A raw-style query where you can pass any WHERE clause and arguments. */
    public List<T> query(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectAll() + where, selectionArg);
        return readAllAndCloseCursor(cursor);
    }

    public boolean delete(T entity) {
        assertSinglePk();
        return deleteByKey(getPrimaryKeyValue(entity));
    }

    public boolean deleteByKey(K key) {
        assertSinglePk();
        String[] idArray = new String[] { key.toString() };
        int affectedRows = db.delete(getTablename(), pkColumns[0] + "==?", idArray);
        return affectedRows >= 1;
    }

    protected void assertSinglePk() {
        if (pkColumns.length != 1) {
            throw new SQLException(this + " (" + getTablename() + ") does not have a single-column primary key");
        }
    }

    public long count() {
        return DatabaseUtils.queryNumEntries(db, getTablename());
    }

    /** Reads the values from the current position of the given cursor and returns a new ImageTO object. */
    abstract public T readFrom(Cursor cursor);

    /** Binds the entity's values to the statement. Make sure to synchronize the statement outside of the method. */
    abstract protected void bindValues(SQLiteStatement stmt, T entity);

    abstract public String getTablename();

    abstract protected Column[] getColumnModel();

    abstract protected void updateKeyAfterInsert(T entity, long rowId);

    /**
     * Returns the value of the primary key, if the entity has a single primary key, or, if not, null. Returns null if
     * entity is null.
     */
    abstract protected K getPrimaryKeyValue(T entity);

    /** Returns true if the Entity class can be updated, e.g. for setting the PK after insert. */
    abstract protected boolean isEntityUpdateable();

}
