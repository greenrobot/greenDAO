/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.greenrobot.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * Base class for all DAOs: Implements entity operations like insert, load, delete, and query.
 * 
 * @author Markus
 * 
 * @param <T>
 *            Entity type
 * @param <K>
 *            Primary key (PK) type; use Void if entity does not have exactly one PK
 */
public abstract class AbstractDao<T, K> {
    protected final SQLiteDatabase db;

    protected final String tablename;

    protected SQLiteStatement insertStatement;
    protected SQLiteStatement insertOrReplaceStatement;
    protected SQLiteStatement updateStatement;
    protected SQLiteStatement deleteStatement;

    private volatile String selectAll;
    private volatile String selectByKey;
    private volatile String selectByRowId;

    private final Property[] properties;
    private final String[] allColumns;
    private final String[] pkColumns;
    private final String[] nonPkColumns;

    private final IdentityScope<K, T> identityScope;

    /** Single property PK or null if there's no PK or a multi property PK. */
    protected final Property pkProperty;

    public AbstractDao(SQLiteDatabase db) {
        this(db, null);
    }

    public AbstractDao(SQLiteDatabase db, IdentityScope<K, T> identityScope) {
        this.db = db;
        this.identityScope = identityScope;
        try {
            tablename = (String) getClass().getField("TABLENAME").get(null);
            Class<?> propertiesClass = Class.forName(getClass().getName() + "$Properties");
            Field[] fields = propertiesClass.getDeclaredFields();
            properties = new Property[fields.length];
            for (Field field : fields) {
                Property property = (Property) field.get(null);
                if (properties[property.oridinal] != null) {
                    throw new DaoException("Duplicate property ordinals");
                }
                properties[property.oridinal] = property;
            }
        } catch (Exception e) {
            throw new DaoException("Could not init DAO", e);
        }
        allColumns = new String[properties.length];

        List<String> pkColumnList = new ArrayList<String>();
        List<String> nonPkColumnList = new ArrayList<String>();
        Property lastPkProperty = null;
        for (int i = 0; i < properties.length; i++) {
            Property property = properties[i];
            String name = property.columnName;
            allColumns[i] = name;
            if (property.primaryKey) {
                pkColumnList.add(name);
                lastPkProperty = property;
            } else {
                nonPkColumnList.add(name);
            }
        }
        String[] nonPkColumnsArray = new String[nonPkColumnList.size()];
        nonPkColumns = nonPkColumnList.toArray(nonPkColumnsArray);
        String[] pkColumnsArray = new String[pkColumnList.size()];
        pkColumns = pkColumnList.toArray(pkColumnsArray);

        pkProperty = pkColumns.length == 1 ? lastPkProperty : null;
    }

    protected void appendCommaSeparated(StringBuilder builder, String valuePrefix, String[] values) {
        int length = values.length;
        for (int i = 0; i < length; i++) {
            builder.append(valuePrefix).append(values[i]);
            if (i < length - 1) {
                builder.append(',');
            }
        }
    }

    protected void appendCommaSeparatedEqPlaceholder(StringBuilder builder, String valuePrefix, String[] values) {
        int length = values.length;
        for (int i = 0; i < length; i++) {
            builder.append(valuePrefix).append(values[i]).append("=?");
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
        builder.append(tablename).append(" (");
        appendCommaSeparated(builder, "", allColumns);
        builder.append(") VALUES (");
        apppendPlaceholders(builder, allColumns.length);
        builder.append(')');
        return builder.toString();
    }

    protected SQLiteStatement getDeleteStatement() {
        if (deleteStatement == null) {
            StringBuilder builder = new StringBuilder("DELETE FROM ");
            builder.append(tablename).append(" WHERE ");
            appendColumnsEqualPlaceholders(builder, getPkColumns());
            deleteStatement = db.compileStatement(builder.toString());
        }
        return deleteStatement;
    }

    protected SQLiteStatement getUpdateStatement() {
        if (updateStatement == null) {
            StringBuilder builder = new StringBuilder("UPDATE ");
            builder.append(tablename).append(" SET ");
            appendColumnsEqualPlaceholders(builder, getAllColumns()); // TODO Use getNonPkColumns() only (performance)
            builder.append(" WHERE ");
            appendColumnsEqualPlaceholders(builder, getPkColumns());
            updateStatement = db.compileStatement(builder.toString());
        }
        return updateStatement;
    }

    protected void appendColumnsEqualPlaceholders(StringBuilder builder, String[] pks) {
        for (int i = 0; i < pks.length; i++) {
            builder.append(pks[i]).append("=?");
            if (i < pks.length - 1) {
                builder.append(',');
            }
        }
    }

    /** ends with an space to simplify appending to this string. */
    protected String getSelectAll() {
        if (selectAll == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            appendCommaSeparated(builder, "", allColumns);
            builder.append(" FROM ").append(tablename).append(' ');
            selectAll = builder.toString();
        }
        return selectAll;
    }

    // TODO precompile
    protected String getSelectByKey() {
        if (selectByKey == null) {
            StringBuilder builder = new StringBuilder(getSelectAll());
            builder.append("WHERE ");
            appendCommaSeparatedEqPlaceholder(builder, "", pkColumns);
            selectByKey = builder.toString();
        }
        return selectByKey;
    }

    protected Property[] getProperties() {
        return properties;
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

    public String getTablename() {
        return tablename;
    }

    /**
     * Loads and entity for the given PK.
     * 
     * @param key
     *            a PK value or null
     * @return The entity or null, if no entity matched the PK value
     */
    public T load(K key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }
        if (identityScope != null) {
            T entity = identityScope.get(key);
            if (entity != null) {
                return entity;
            }
        }
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
            return readAndTrack(cursor);
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

    /**
     * Inserts the given entities in the database using a transaction.
     * 
     * @param entities
     *            The entities to insert.
     */
    public void insertInTx(Iterable<T> entities) {
        insertInTx(entities, isEntityUpdateable());
    }

    /**
     * Inserts the given entities in the database using a transaction.
     * 
     * @param entities
     *            The entities to insert.
     */
    public void insertInTx(T... entities) {
        insertInTx(Arrays.asList(entities), isEntityUpdateable());
    }

    /**
     * Inserts the given entities in the database using a transaction. The given entities will become tracked if the PK
     * is set.
     * 
     * @param entities
     *            The entities to insert.
     * @param setPrimaryKey
     *            if true, the PKs of the given will be set after the insert; pass false to improve performance.
     */
    public void insertInTx(Iterable<T> entities, boolean setPrimaryKey) {
        SQLiteStatement stmt = getInsertStatement();
        synchronized (stmt) {
            db.beginTransaction();
            try {
                for (T entity : entities) {
                    bindValues(stmt, entity);
                    if (setPrimaryKey) {
                        long rowId = stmt.executeInsert();
                        updateKeyAfterInsertAndTrack(entity, rowId);
                    } else {
                        stmt.execute();
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    /** Insert an entity into the table associated with a concrete DAO. */
    public long insert(T entity) {
        SQLiteStatement stmt = getInsertStatement();
        synchronized (stmt) {
            bindValues(stmt, entity);
            long rowId = stmt.executeInsert();
            updateKeyAfterInsertAndTrack(entity, rowId);
            return rowId;
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
        long rowId;
        synchronized (stmt) {
            bindValues(stmt, entity);
            rowId = stmt.executeInsert();
        }
        updateKeyAfterInsertAndTrack(entity, rowId);
        return rowId;
    }

    protected void updateKeyAfterInsertAndTrack(T entity, long rowId) {
        K key = updateKeyAfterInsert(entity, rowId);
        if (key != null && identityScope != null) {
            identityScope.put(key, entity);
        }
    }

    /** Reads all available rows from the given cursor and returns a list of entities. */
    public List<T> readAllFrom(Cursor cursor) {
        List<T> list = new ArrayList<T>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                list.add(readAndTrack(cursor));
            } while (cursor.moveToNext());
        }
        return list;
    }

    protected T readAndTrack(Cursor cursor) {
        T entity = readFrom(cursor, 0);
        K key = getPrimaryKeyValue(entity);
        if (key != null && identityScope != null) {
            identityScope.put(key, entity);
        }
        return entity;
    }

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<T> query(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectAll() + where, selectionArg);
        return readAllAndCloseCursor(cursor);
    }

    /** Performs a standard Android-style query for entities. */
    public List<T> query(String selection, String[] selectionArgs, String groupBy, String having, String orderby) {
        Cursor cursor = db.query(tablename, getAllColumns(), selection, selectionArgs, groupBy, having, orderby);
        return readAllAndCloseCursor(cursor);
    }

    public void deleteAll() {
        db.execSQL("DELETE FROM " + tablename);
    }

    /** Deletes the given entity from the database. Currently, only single value PK entities are supported. */
    // TODO support multi-value PK: should sub classes overwrite this method?
    public void delete(T entity) {
        assertSinglePk();
        K key = getPrimaryKeyValue(entity);
        deleteByKey(key);
        if (identityScope != null) {
            identityScope.remove(key);
        }
    }

    /** Deletes an entity with the given PK from the database. Currently, only single value PK entities are supported. */
    public void deleteByKey(K key) {
        assertSinglePk();
        SQLiteStatement stmt = getDeleteStatement();
        synchronized (stmt) {
            if (key instanceof Long) {
                stmt.bindLong(1, (Long) key);
            } else {
                stmt.bindString(1, key.toString());
            }
            stmt.execute();
        }
    }

    /** Resets all locally changed properties of the entity by reloading the values from the database. */
    public void reset(T entity) {
        assertSinglePk();
        // TODO support multi-value PK
        K key = getPrimaryKeyValue(entity);
        String sql = getSelectByKey();
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                throw new DaoException("Entity does not exist in the database anymore: " + entity.getClass()
                        + " with key " + key);
            } else if (!cursor.isLast()) {
                throw new DaoException("Expected unique result, but count was " + cursor.getCount());
            }
            readFrom(cursor, entity, 0);
            if (identityScope != null) {
                identityScope.put(key, entity);
            }
        } finally {
            cursor.close();
        }
    }

    public void update(T entity) {
        assertSinglePk();
        SQLiteStatement stmt = getUpdateStatement();
        synchronized (stmt) {
            updateInsideSynchronized(entity, stmt);
        }
    }

    // TODO support multi-value PK
    protected void updateInsideSynchronized(T entity, SQLiteStatement stmt) {
        // To do? Check if it's worth not to bind PKs here (performance).
        bindValues(stmt, entity);
        K key = getPrimaryKeyValue(entity);
        int index = allColumns.length + 1;
        if (key instanceof Long) {
            stmt.bindLong(index, (Long) key);
        } else {
            stmt.bindString(index, key.toString());
        }
        stmt.execute();
        if (identityScope != null) {
            identityScope.put(key, entity);
        }
    }

    /**
     * Inserts the given entities in the database using a transaction.
     * 
     * @param entities
     *            The entities to insert.
     * @param setPrimaryKey
     *            if true, the PKs of the given will be set after the insert; pass false to improve performance.
     */
    public void updateInTx(Iterable<T> entities) {
        SQLiteStatement stmt = getUpdateStatement();
        synchronized (stmt) {
            db.beginTransaction();
            try {
                for (T entity : entities) {
                    updateInsideSynchronized(entity, stmt);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    protected void assertSinglePk() {
        if (pkColumns.length != 1) {
            throw new SQLException(this + " (" + tablename + ") does not have a single-column primary key");
        }
    }

    public long count() {
        return DatabaseUtils.queryNumEntries(db, tablename);
    }

    /** Reads the values from the current position of the given cursor and returns a new entity. */
    abstract public T readFrom(Cursor cursor, int offset);

    /** Reads the key from the current position of the given cursor, or returns null if there's no single-value key. */
    abstract public K readPkFrom(Cursor cursor, int offset);

    /** Reads the values from the current position of the given cursor into an existing entity. */
    abstract public void readFrom(Cursor cursor, T entity, int offset);

    /** Binds the entity's values to the statement. Make sure to synchronize the statement outside of the method. */
    abstract protected void bindValues(SQLiteStatement stmt, T entity);

    abstract protected K updateKeyAfterInsert(T entity, long rowId);

    /**
     * Returns the value of the primary key, if the entity has a single primary key, or, if not, null. Returns null if
     * entity is null.
     */
    abstract protected K getPrimaryKeyValue(T entity);

    /** Returns true if the Entity class can be updated, e.g. for setting the PK after insert. */
    abstract protected boolean isEntityUpdateable();

}
