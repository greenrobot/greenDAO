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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.database.Cursor;
import android.database.DatabaseUtils;
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
    private final DaoConfig config;
    private IdentityScope<K, T> identityScope;
    private TableStatements statements;

    private final AbstractDaoSession session;

    public AbstractDao(DaoConfig config) {
        this(config, null);
    }

    @SuppressWarnings("unchecked")
    public AbstractDao(DaoConfig config, AbstractDaoSession daoSession) {
        this.config = config;
        this.session = daoSession;
        db = config.db;
        identityScope = (IdentityScope<K, T>) config.getIdentityScope();
        statements = config.statements;
    }

    public AbstractDaoSession getSession() {
        return session;
    }

    TableStatements getStatements() {
        return config.statements;
    }

    public String getTablename() {
        return config.tablename;
    }

    public Property[] getProperties() {
        return config.properties;
    }

    public Property getPkProperty() {
        return config.pkProperty;
    }

    public String[] getAllColumns() {
        return config.allColumns;
    }

    public String[] getPkColumns() {
        return config.pkColumns;
    }

    public String[] getNonPkColumns() {
        return config.nonPkColumns;
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
        String sql = statements.getSelectByKey();
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        return loadUniqueAndCloseCursor(cursor);
    }

    public T loadByRowId(long rowId) {
        String[] idArray = new String[] { Long.toString(rowId) };
        Cursor cursor = db.rawQuery(statements.getSelectByRowId(), idArray);
        return loadUniqueAndCloseCursor(cursor);
    }

    protected T loadUniqueAndCloseCursor(Cursor cursor) {
        try {
            return loadUnique(cursor);
        } finally {
            cursor.close();
        }
    }

    protected T loadUnique(Cursor cursor) {
        boolean available = cursor.moveToFirst();
        if (!available) {
            return null;
        } else if (!cursor.isLast()) {
            throw new DaoException("Expected unique result, but count was " + cursor.getCount());
        }
        return loadCurrent(cursor, 0);
    }

    public List<T> loadAll() {
        Cursor cursor = db.rawQuery(statements.getSelectAll(), null);
        return loadAllAndCloseCursor(cursor);
    }

    public boolean detach(T entity) {
        if (identityScope != null) {
            return identityScope.detach(getKey(entity), entity);
        } else {
            return false;
        }
    }

    protected List<T> loadAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllFromCursor(cursor);
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
        SQLiteStatement stmt = statements.getInsertStatement();
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
        SQLiteStatement stmt = statements.getInsertStatement();
        synchronized (stmt) {
            bindValues(stmt, entity);
            long rowId = stmt.executeInsert();
            updateKeyAfterInsertAndTrack(entity, rowId);
            return rowId;
        }
    }

    /** Insert an entity into the table associated with a concrete DAO. */
    public void insertWithoutSettingPk(T entity) {
        SQLiteStatement stmt = statements.getInsertStatement();
        synchronized (stmt) {
            bindValues(stmt, entity);
            stmt.execute();
        }
    }

    /** Insert an entity into the table associated with a concrete DAO. */
    public long insertOrReplace(T entity) {
        SQLiteStatement stmt = statements.getInsertOrReplaceStatement();
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
        attachEntity(key, entity);
    }

    /** Reads all available rows from the given cursor and returns a list of entities. */
    protected List<T> loadAllFromCursor(Cursor cursor) {
        List<T> list = new ArrayList<T>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                list.add(loadCurrent(cursor, 0));
            } while (cursor.moveToNext());
        }
        return list;
    }

    /** Internal use only. Considers identity scope. */
    public T loadCurrent(Cursor cursor, int offset) {
        if (identityScope != null) {
            K key = readKey(cursor, offset);
            if (offset != 0 && key == null) {
                // Occurs with deep loads (left outer joins)
                return null;
            }
            T entity = identityScope.get(key);
            if (entity != null) {
                return entity;
            } else {
                entity = readEntity(cursor, offset);
                attachEntity(key, entity);
                return entity;
            }
        } else {
            // Check offset, assume a value !=0 indicating a potential outer join, so check PK
            if (offset != 0) {
                K key = readKey(cursor, offset);
                if (key == null) {
                    // Occurs with deep loads (left outer joins)
                    return null;
                }
            }
            T entity = readEntity(cursor, offset);
            attachEntity(null, entity);
            return entity;
        }
    }

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<T> queryRaw(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(statements.getSelectAll() + where, selectionArg);
        return loadAllAndCloseCursor(cursor);
    }

    /** Performs a standard Android-style query for entities. */
    public List<T> query(String selection, String[] selectionArgs, String groupBy, String having, String orderby) {
        Cursor cursor = db.query(config.tablename, getAllColumns(), selection, selectionArgs, groupBy, having, orderby);
        return loadAllAndCloseCursor(cursor);
    }

    public void deleteAll() {
        // String sql = SqlUtils.createSqlDelete(config.tablename, null);
        // db.execSQL(sql);

        db.execSQL("DELETE FROM '" + config.tablename + "'");
        if (identityScope != null) {
            identityScope.clear();
        }
    }

    /** Deletes the given entity from the database. Currently, only single value PK entities are supported. */
    public void delete(T entity) {
        assertSinglePk();
        K key = getKey(entity);
        deleteByKey(key);
        if (identityScope != null) {
            identityScope.remove(key);
        }
    }

    /** Deletes an entity with the given PK from the database. Currently, only single value PK entities are supported. */
    public void deleteByKey(K key) {
        assertSinglePk();
        SQLiteStatement stmt = statements.getDeleteStatement();
        synchronized (stmt) {
            if (key instanceof Long) {
                stmt.bindLong(1, (Long) key);
            } else {
                stmt.bindString(1, key.toString());
            }
            stmt.execute();
        }
        if (identityScope != null) {
            identityScope.remove(key);
        }
    }

    /** Resets all locally changed properties of the entity by reloading the values from the database. */
    public void refresh(T entity) {
        assertSinglePk();
        K key = getKey(entity);
        String sql = statements.getSelectByKey();
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
            readEntity(cursor, entity, 0);
            attachEntity(key, entity);
        } finally {
            cursor.close();
        }
    }

    public void update(T entity) {
        assertSinglePk();
        SQLiteStatement stmt = statements.getUpdateStatement();
        synchronized (stmt) {
            updateInsideSynchronized(entity, stmt);
        }
    }
    
    public QueryBuilder<T> queryBuilder() {
        return new QueryBuilder<T>(this);
    }

    protected void updateInsideSynchronized(T entity, SQLiteStatement stmt) {
        // To do? Check if it's worth not to bind PKs here (performance).
        bindValues(stmt, entity);
        K key = getKey(entity);
        int index = config.allColumns.length + 1;
        if (key instanceof Long) {
            stmt.bindLong(index, (Long) key);
        } else {
            stmt.bindString(index, key.toString());
        }
        stmt.execute();
        attachEntity(key, entity);
    }

    /**
     * Attaches the entity to the identity scope. Sub classes with relations additionally set the DaoMaster here.
     * 
     * @param key
     *            Needed only for identity scope, pass null if there's none.
     * @param entity
     *            The entitiy to attach
     * */
    protected void attachEntity(K key, T entity) {
        if (identityScope != null && key != null) {
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
        SQLiteStatement stmt = statements.getUpdateStatement();
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
        if (config.pkColumns.length != 1) {
            throw new DaoException(this + " (" + config.tablename + ") does not have a single-column primary key");
        }
    }

    public long count() {
        return DatabaseUtils.queryNumEntries(db, '\'' + config.tablename + '\'');
    }

    /** Reads the values from the current position of the given cursor and returns a new entity. */
    abstract protected T readEntity(Cursor cursor, int offset);

    /** Reads the key from the current position of the given cursor, or returns null if there's no single-value key. */
    abstract protected K readKey(Cursor cursor, int offset);

    /** Reads the values from the current position of the given cursor into an existing entity. */
    abstract protected void readEntity(Cursor cursor, T entity, int offset);

    /** Binds the entity's values to the statement. Make sure to synchronize the statement outside of the method. */
    abstract protected void bindValues(SQLiteStatement stmt, T entity);

    abstract protected K updateKeyAfterInsert(T entity, long rowId);

    /**
     * Returns the value of the primary key, if the entity has a single primary key, or, if not, null. Returns null if
     * entity is null.
     */
    abstract protected K getKey(T entity);

    /** Returns true if the Entity class can be updated, e.g. for setting the PK after insert. */
    abstract protected boolean isEntityUpdateable();

}
