/*
 * Copyright (C) 2011-2013 Markus Junginger, greenrobot (http://greenrobot.de)
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
import java.util.Collection;
import java.util.List;

import android.database.CrossProcessCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.identityscope.IdentityScope;
import de.greenrobot.dao.identityscope.IdentityScopeLong;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.internal.FastCursor;
import de.greenrobot.dao.internal.TableStatements;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Base class for all DAOs: Implements entity operations like insert, load, delete, and query.
 * 
 * This class is thread-safe.
 * 
 * @author Markus
 * 
 * @param <T>
 *            Entity type
 * @param <K>
 *            Primary key (PK) type; use Void if entity does not have exactly one PK
 */
/*
 * When operating on TX, statements, or identity scope the following locking order must be met to avoid deadlocks:
 * 
 * 1.) If not inside a TX already, begin a TX to acquire a DB connection (connection is to be handled like a lock)
 * 
 * 2.) The SQLiteStatement
 * 
 * 3.) identityScope
 */
public abstract class AbstractDao<T, K> {
    protected final SQLiteDatabase db;
    protected final DaoConfig config;
    protected IdentityScope<K, T> identityScope;
    protected IdentityScopeLong<T> identityScopeLong;
    protected TableStatements statements;

    protected final AbstractDaoSession session;
    protected final int pkOrdinal;

    public AbstractDao(DaoConfig config) {
        this(config, null);
    }

    @SuppressWarnings("unchecked")
    public AbstractDao(DaoConfig config, AbstractDaoSession daoSession) {
        this.config = config;
        this.session = daoSession;
        db = config.db;
        identityScope = (IdentityScope<K, T>) config.getIdentityScope();
        if (identityScope instanceof IdentityScopeLong) {
            identityScopeLong = (IdentityScopeLong<T>) identityScope;
        }
        statements = config.statements;
        pkOrdinal = config.pkProperty != null ? config.pkProperty.ordinal : -1;
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
        return loadCurrent(cursor, 0, true);
    }

    /** Loads all available entities from the database. */
    public List<T> loadAll() {
        Cursor cursor = db.rawQuery(statements.getSelectAll(), null);
        return loadAllAndCloseCursor(cursor);
    }

    /** Detaches an entity from the identity scope (session). Subsequent query results won't return this object. */
    public boolean detach(T entity) {
        if (identityScope != null) {
            K key = getKeyVerified(entity);
            return identityScope.detach(key, entity);
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
        executeInsertInTx(stmt, entities, setPrimaryKey);
    }

    /**
     * Inserts or replaces the given entities in the database using a transaction. The given entities will become
     * tracked if the PK is set.
     * 
     * @param entities
     *            The entities to insert.
     * @param setPrimaryKey
     *            if true, the PKs of the given will be set after the insert; pass false to improve performance.
     */
    public void insertOrReplaceInTx(Iterable<T> entities, boolean setPrimaryKey) {
        SQLiteStatement stmt = statements.getInsertOrReplaceStatement();
        executeInsertInTx(stmt, entities, setPrimaryKey);
    }

    /**
     * Inserts or replaces the given entities in the database using a transaction.
     * 
     * @param entities
     *            The entities to insert.
     */
    public void insertOrReplaceInTx(Iterable<T> entities) {
        insertOrReplaceInTx(entities, isEntityUpdateable());
    }

    /**
     * Inserts or replaces the given entities in the database using a transaction.
     * 
     * @param entities
     *            The entities to insert.
     */
    public void insertOrReplaceInTx(T... entities) {
        insertOrReplaceInTx(Arrays.asList(entities), isEntityUpdateable());
    }

    private void executeInsertInTx(SQLiteStatement stmt, Iterable<T> entities, boolean setPrimaryKey) {
        db.beginTransaction();
        try {
            synchronized (stmt) {
                if (identityScope != null) {
                    identityScope.lock();
                }
                try {
                    for (T entity : entities) {
                        bindValues(stmt, entity);
                        if (setPrimaryKey) {
                            long rowId = stmt.executeInsert();
                            updateKeyAfterInsertAndAttach(entity, rowId, false);
                        } else {
                            stmt.execute();
                        }
                    }
                } finally {
                    if (identityScope != null) {
                        identityScope.unlock();
                    }
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Insert an entity into the table associated with a concrete DAO.
     * 
     * @return row ID of newly inserted entity
     */
    public long insert(T entity) {
        return executeInsert(entity, statements.getInsertStatement());
    }

    /**
     * Insert an entity into the table associated with a concrete DAO <b>without</b> setting key property. Warning: This
     * may be faster, but the entity should not be used anymore. The entity also won't be attached to identy scope.
     * 
     * @return row ID of newly inserted entity
     */
    public long insertWithoutSettingPk(T entity) {
        SQLiteStatement stmt = statements.getInsertStatement();
        long rowId;
        if (db.isDbLockedByCurrentThread()) {
            synchronized (stmt) {
                bindValues(stmt, entity);
                rowId = stmt.executeInsert();
            }
        } else {
            // Do TX to acquire a connection before locking the stmt to avoid deadlocks
            db.beginTransaction();
            try {
                synchronized (stmt) {
                    bindValues(stmt, entity);
                    rowId = stmt.executeInsert();
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        return rowId;
    }

    /**
     * Insert an entity into the table associated with a concrete DAO.
     * 
     * @return row ID of newly inserted entity
     */
    public long insertOrReplace(T entity) {
        return executeInsert(entity, statements.getInsertOrReplaceStatement());
    }

    private long executeInsert(T entity, SQLiteStatement stmt) {
        long rowId;
        if (db.isDbLockedByCurrentThread()) {
            synchronized (stmt) {
                bindValues(stmt, entity);
                rowId = stmt.executeInsert();
            }
        } else {
            // Do TX to acquire a connection before locking the stmt to avoid deadlocks
            db.beginTransaction();
            try {
                synchronized (stmt) {
                    bindValues(stmt, entity);
                    rowId = stmt.executeInsert();
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        updateKeyAfterInsertAndAttach(entity, rowId, true);
        return rowId;
    }

    protected void updateKeyAfterInsertAndAttach(T entity, long rowId, boolean lock) {
        if (rowId != -1) {
            K key = updateKeyAfterInsert(entity, rowId);
            attachEntity(key, entity, lock);
        } else {
            // TODO When does this actually happen? Should we throw instead?
            DaoLog.w("Could not insert row (executeInsert returned -1)");
        }
    }

    /** Reads all available rows from the given cursor and returns a list of entities. */
    protected List<T> loadAllFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<T> list = new ArrayList<T>(count);
        if (cursor instanceof CrossProcessCursor) {
            CursorWindow window = ((CrossProcessCursor) cursor).getWindow();
            if (window != null) { // E.g. Roboelectric has no Window at this point
                if (window.getNumRows() == count) {
                    cursor = new FastCursor(window);
                } else {
                    DaoLog.d("Window vs. result size: " + window.getNumRows() + "/" + count);
                }
            }
        }

        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrent(cursor, 0, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }

    /** Internal use only. Considers identity scope. */
    final protected T loadCurrent(Cursor cursor, int offset, boolean lock) {
        if (identityScopeLong != null) {
            if (offset != 0) {
                // Occurs with deep loads (left outer joins)
                if (cursor.isNull(pkOrdinal + offset)) {
                    return null;
                }
            }

            long key = cursor.getLong(pkOrdinal + offset);
            T entity = lock ? identityScopeLong.get2(key) : identityScopeLong.get2NoLock(key);
            if (entity != null) {
                return entity;
            } else {
                entity = readEntity(cursor, offset);
                attachEntity(entity);
                if (lock) {
                    identityScopeLong.put2(key, entity);
                } else {
                    identityScopeLong.put2NoLock(key, entity);
                }
                return entity;
            }
        } else if (identityScope != null) {
            K key = readKey(cursor, offset);
            if (offset != 0 && key == null) {
                // Occurs with deep loads (left outer joins)
                return null;
            }
            T entity = lock ? identityScope.get(key) : identityScope.getNoLock(key);
            if (entity != null) {
                return entity;
            } else {
                entity = readEntity(cursor, offset);
                attachEntity(key, entity, lock);
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
            attachEntity(entity);
            return entity;
        }
    }

    /** Internal use only. Considers identity scope. */
    final protected <O> O loadCurrentOther(AbstractDao<O, ?> dao, Cursor cursor, int offset) {
        return dao.loadCurrent(cursor, offset, /* TODO check this */true);
    }

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<T> queryRaw(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(statements.getSelectAll() + where, selectionArg);
        return loadAllAndCloseCursor(cursor);
    }

    /**
     * Creates a repeatable {@link Query} object based on the given raw SQL where you can pass any WHERE clause and
     * arguments.
     */
    public Query<T> queryRawCreate(String where, Object... selectionArg) {
        List<Object> argList = Arrays.asList(selectionArg);
        return queryRawCreateListArgs(where, argList);
    }

    /**
     * Creates a repeatable {@link Query} object based on the given raw SQL where you can pass any WHERE clause and
     * arguments.
     */
    public Query<T> queryRawCreateListArgs(String where, Collection<Object> selectionArg) {
        return Query.internalCreate(this, statements.getSelectAll() + where, selectionArg.toArray());
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
        K key = getKeyVerified(entity);
        deleteByKey(key);
    }

    /** Deletes an entity with the given PK from the database. Currently, only single value PK entities are supported. */
    public void deleteByKey(K key) {
        assertSinglePk();
        SQLiteStatement stmt = statements.getDeleteStatement();
        if (db.isDbLockedByCurrentThread()) {
            synchronized (stmt) {
                deleteByKeyInsideSynchronized(key, stmt);
            }
        } else {
            // Do TX to acquire a connection before locking the stmt to avoid deadlocks
            db.beginTransaction();
            try {
                synchronized (stmt) {
                    deleteByKeyInsideSynchronized(key, stmt);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        if (identityScope != null) {
            identityScope.remove(key);
        }
    }

    private void deleteByKeyInsideSynchronized(K key, SQLiteStatement stmt) {
        if (key instanceof Long) {
            stmt.bindLong(1, (Long) key);
        } else if (key == null) {
            throw new DaoException("Cannot delete entity, key is null");
        } else {
            stmt.bindString(1, key.toString());
        }
        stmt.execute();
    }

    private void deleteInTxInternal(Iterable<T> entities, Iterable<K> keys) {
        assertSinglePk();
        SQLiteStatement stmt = statements.getDeleteStatement();
        List<K> keysToRemoveFromIdentityScope = null;
        db.beginTransaction();
        try {
            synchronized (stmt) {
                if (identityScope != null) {
                    identityScope.lock();
                    keysToRemoveFromIdentityScope = new ArrayList<K>();
                }
                try {
                    if (entities != null) {
                        for (T entity : entities) {
                            K key = getKeyVerified(entity);
                            deleteByKeyInsideSynchronized(key, stmt);
                            if (keysToRemoveFromIdentityScope != null) {
                                keysToRemoveFromIdentityScope.add(key);
                            }
                        }
                    }
                    if (keys != null) {
                        for (K key : keys) {
                            deleteByKeyInsideSynchronized(key, stmt);
                            if (keysToRemoveFromIdentityScope != null) {
                                keysToRemoveFromIdentityScope.add(key);
                            }
                        }
                    }
                } finally {
                    if (identityScope != null) {
                        identityScope.unlock();
                    }
                }
            }
            db.setTransactionSuccessful();
            if (keysToRemoveFromIdentityScope != null && identityScope != null) {
                identityScope.remove(keysToRemoveFromIdentityScope);
            }
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Deletes the given entities in the database using a transaction.
     * 
     * @param entities
     *            The entities to delete.
     */
    public void deleteInTx(Iterable<T> entities) {
        deleteInTxInternal(entities, null);
    }

    /**
     * Deletes the given entities in the database using a transaction.
     * 
     * @param entities
     *            The entities to delete.
     */
    public void deleteInTx(T... entities) {
        deleteInTxInternal(Arrays.asList(entities), null);
    }

    /**
     * Deletes all entities with the given keys in the database using a transaction.
     * 
     * @param keys
     *            Keys of the entities to delete.
     */
    public void deleteByKeyInTx(Iterable<K> keys) {
        deleteInTxInternal(null, keys);
    }

    /**
     * Deletes all entities with the given keys in the database using a transaction.
     * 
     * @param keys
     *            Keys of the entities to delete.
     */
    public void deleteByKeyInTx(K... keys) {
        deleteInTxInternal(null, Arrays.asList(keys));
    }

    /** Resets all locally changed properties of the entity by reloading the values from the database. */
    public void refresh(T entity) {
        assertSinglePk();
        K key = getKeyVerified(entity);
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
            attachEntity(key, entity, true);
        } finally {
            cursor.close();
        }
    }

    public void update(T entity) {
        assertSinglePk();
        SQLiteStatement stmt = statements.getUpdateStatement();
        if (db.isDbLockedByCurrentThread()) {
            synchronized (stmt) {
                updateInsideSynchronized(entity, stmt, true);
            }
        } else {
            // Do TX to acquire a connection before locking the stmt to avoid deadlocks
            db.beginTransaction();
            try {
                synchronized (stmt) {
                    updateInsideSynchronized(entity, stmt, true);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    public QueryBuilder<T> queryBuilder() {
        return QueryBuilder.internalCreate(this);
    }

    protected void updateInsideSynchronized(T entity, SQLiteStatement stmt, boolean lock) {
        // To do? Check if it's worth not to bind PKs here (performance).
        bindValues(stmt, entity);
        int index = config.allColumns.length + 1;
        K key = getKey(entity);
        if (key instanceof Long) {
            stmt.bindLong(index, (Long) key);
        } else if (key == null) {
            throw new DaoException("Cannot update entity without key - was it inserted before?");
        } else {
            stmt.bindString(index, key.toString());
        }
        stmt.execute();
        attachEntity(key, entity, lock);
    }

    /**
     * Attaches the entity to the identity scope. Calls attachEntity(T entity).
     * 
     * @param key
     *            Needed only for identity scope, pass null if there's none.
     * @param entity
     *            The entitiy to attach
     * */
    protected final void attachEntity(K key, T entity, boolean lock) {
        attachEntity(entity);
        if (identityScope != null && key != null) {
            if (lock) {
                identityScope.put(key, entity);
            } else {
                identityScope.putNoLock(key, entity);
            }
        }
    }

    /**
     * Sub classes with relations additionally set the DaoMaster here. Must be called before the entity is attached to
     * the identity scope.
     * 
     * @param entity
     *            The entitiy to attach
     * */
    protected void attachEntity(T entity) {
    }

    /**
     * Updates the given entities in the database using a transaction.
     * 
     * @param entities
     *            The entities to insert.
     */
    public void updateInTx(Iterable<T> entities) {
        SQLiteStatement stmt = statements.getUpdateStatement();
        db.beginTransaction();
        try {
            synchronized (stmt) {
                if (identityScope != null) {
                    identityScope.lock();
                }
                try {
                    for (T entity : entities) {
                        updateInsideSynchronized(entity, stmt, false);
                    }
                } finally {
                    if (identityScope != null) {
                        identityScope.unlock();
                    }
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Updates the given entities in the database using a transaction.
     * 
     * @param entities
     *            The entities to update.
     */
    public void updateInTx(T... entities) {
        updateInTx(Arrays.asList(entities));
    }

    protected void assertSinglePk() {
        if (config.pkColumns.length != 1) {
            throw new DaoException(this + " (" + config.tablename + ") does not have a single-column primary key");
        }
    }

    public long count() {
        return DatabaseUtils.queryNumEntries(db, '\'' + config.tablename + '\'');
    }

    /** See {@link #getKey(Object)}, but guarantees that the returned key is never null (throws if null). */
    protected K getKeyVerified(T entity) {
        K key = getKey(entity);
        if (key == null) {
            if (entity == null) {
                throw new NullPointerException("Entity may not be null");
            } else {
                throw new DaoException("Entity has no key");
            }
        } else {
            return key;
        }
    }

    /** Gets the SQLiteDatabase for custom database access. Not needed for greenDAO entities. */
    public SQLiteDatabase getDatabase() {
        return db;
    }

    /** Reads the values from the current position of the given cursor and returns a new entity. */
    abstract protected T readEntity(Cursor cursor, int offset);

    /** Reads the key from the current position of the given cursor, or returns null if there's no single-value key. */
    abstract protected K readKey(Cursor cursor, int offset);

    /** Reads the values from the current position of the given cursor into an existing entity. */
    abstract protected void readEntity(Cursor cursor, T entity, int offset);

    /** Binds the entity's values to the statement. Make sure to synchronize the statement outside of the method. */
    abstract protected void bindValues(SQLiteStatement stmt, T entity);

    /**
     * Updates the entity's key if possible (only for Long PKs currently). This method must always return the entity's
     * key regardless of whether the key existed before or not.
     */
    abstract protected K updateKeyAfterInsert(T entity, long rowId);

    /**
     * Returns the value of the primary key, if the entity has a single primary key, or, if not, null. Returns null if
     * entity is null.
     */
    abstract protected K getKey(T entity);

    /** Returns true if the Entity class can be updated, e.g. for setting the PK after insert. */
    abstract protected boolean isEntityUpdateable();

}
