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
 * Base class for all DAOs: Implements entity operations like insert, load,
 * delete, and query.
 * 
 * This class is thread-safe.
 * 
 * @author Markus
 * 
 * @param <T>
 *          Entity type
 * @param <K>
 *          Primary key (PK) type; use Void if entity does not have exactly one
 *          PK
 */
/*
 * When operating on TX, statements, or identity scope the following locking
 * order must be met to avoid deadlocks:
 * 
 * 1.) If not inside a TX already, begin a TX to acquire a DB connection
 * (connection is to be handled like a lock)
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
    this.db = config.db;
    this.identityScope = (IdentityScope<K, T>) config.getIdentityScope();
    if (this.identityScope instanceof IdentityScopeLong) {
      this.identityScopeLong = (IdentityScopeLong<T>) this.identityScope;
    }
    this.statements = config.statements;
    this.pkOrdinal = config.pkProperty != null ? config.pkProperty.ordinal : -1;
  }

  public AbstractDaoSession getSession() {
    return this.session;
  }

  TableStatements getStatements() {
    return this.config.statements;
  }

  public String getTablename() {
    return this.config.tablename;
  }

  public Property[] getProperties() {
    return this.config.properties;
  }

  public Property getPkProperty() {
    return this.config.pkProperty;
  }

  public String[] getAllColumns() {
    return this.config.allColumns;
  }

  public String[] getPkColumns() {
    return this.config.pkColumns;
  }

  public String[] getNonPkColumns() {
    return this.config.nonPkColumns;
  }

  /**
   * Loads and entity for the given PK.
   * 
   * @param key
   *          a PK value or null
   * @return The entity or null, if no entity matched the PK value
   */
  public T load(K key) {
    this.assertSinglePk();
    if (key == null) {
      return null;
    }
    if (this.identityScope != null) {
      T entity = this.identityScope.get(key);
      if (entity != null) {
        return entity;
      }
    }
    String sql = this.statements.getSelectByKey();
    String[] keyArray = new String[] { key.toString() };
    Cursor cursor = this.db.rawQuery(sql, keyArray);
    return this.loadUniqueAndCloseCursor(cursor);
  }

  public T loadByRowId(long rowId) {
    String[] idArray = new String[] { Long.toString(rowId) };
    Cursor cursor = this.db.rawQuery(this.statements.getSelectByRowId(), idArray);
    return this.loadUniqueAndCloseCursor(cursor);
  }

  protected T loadUniqueAndCloseCursor(Cursor cursor) {
    try {
      return this.loadUnique(cursor);
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
    return this.loadCurrent(cursor, 0, true);
  }

  /** Loads all available entities from the database. */
  public List<T> loadAll() {
    Cursor cursor = this.db.rawQuery(this.statements.getSelectAll(), null);
    return this.loadAllAndCloseCursor(cursor);
  }

  /**
   * Detaches an entity from the identity scope (session). Subsequent query
   * results won't return this object.
   */
  public boolean detach(T entity) {
    if (this.identityScope != null) {
      K key = this.getKeyVerified(entity);
      return this.identityScope.detach(key, entity);
    } else {
      return false;
    }
  }

  protected List<T> loadAllAndCloseCursor(Cursor cursor) {
    try {
      return this.loadAllFromCursor(cursor);
    } finally {
      cursor.close();
    }
  }

  /**
   * Inserts the given entities in the database using a transaction.
   * 
   * @param entities
   *          The entities to insert.
   */
  public void insertInTx(Iterable<T> entities) {
    this.insertInTx(entities, this.isEntityUpdateable());
  }

  /**
   * Inserts the given entities in the database using a transaction.
   * 
   * @param entities
   *          The entities to insert.
   */
  public void insertInTx(T... entities) {
    this.insertInTx(Arrays.asList(entities), this.isEntityUpdateable());
  }

  /**
   * Inserts the given entities in the database using a transaction. The given
   * entities will become tracked if the PK is set.
   * 
   * @param entities
   *          The entities to insert.
   * @param setPrimaryKey
   *          if true, the PKs of the given will be set after the insert; pass
   *          false to improve performance.
   */
  public void insertInTx(Iterable<T> entities, boolean setPrimaryKey) {
    SQLiteStatement stmt = this.statements.getInsertStatement();
    this.executeInsertInTx(stmt, entities, setPrimaryKey);
  }

  /**
   * Inserts or replaces the given entities in the database using a transaction.
   * The given entities will become tracked if the PK is set.
   * 
   * @param entities
   *          The entities to insert.
   * @param setPrimaryKey
   *          if true, the PKs of the given will be set after the insert; pass
   *          false to improve performance.
   */
  public void insertOrReplaceInTx(Iterable<T> entities, boolean setPrimaryKey) {
    SQLiteStatement stmt = this.statements.getInsertOrReplaceStatement();
    this.executeInsertInTx(stmt, entities, setPrimaryKey);
  }

  /**
   * Inserts or replaces the given entities in the database using a transaction.
   * 
   * @param entities
   *          The entities to insert.
   */
  public void insertOrReplaceInTx(Iterable<T> entities) {
    this.insertOrReplaceInTx(entities, this.isEntityUpdateable());
  }

  /**
   * Inserts or replaces the given entities in the database using a transaction.
   * 
   * @param entities
   *          The entities to insert.
   */
  public void insertOrReplaceInTx(T... entities) {
    this.insertOrReplaceInTx(Arrays.asList(entities), this.isEntityUpdateable());
  }

  private void executeInsertInTx(SQLiteStatement stmt, Iterable<T> entities, boolean setPrimaryKey) {
    this.db.beginTransaction();
    try {
      synchronized (stmt) {
        if (this.identityScope != null) {
          this.identityScope.lock();
        }
        try {
          for (T entity : entities) {
            this.bindValues(stmt, entity);
            if (setPrimaryKey) {
              long rowId = stmt.executeInsert();
              this.updateKeyAfterInsertAndAttach(entity, rowId, false);
            } else {
              stmt.execute();
            }
          }
        } finally {
          if (this.identityScope != null) {
            this.identityScope.unlock();
          }
        }
      }
      this.db.setTransactionSuccessful();
    } finally {
      this.db.endTransaction();
    }
  }

  /**
   * Insert an entity into the table associated with a concrete DAO.
   * 
   * @return row ID of newly inserted entity
   */
  public long insert(T entity) {
    return this.executeInsert(entity, this.statements.getInsertStatement());
  }

  /**
   * Insert an entity into the table associated with a concrete DAO
   * <b>without</b> setting key property. Warning: This may be faster, but the
   * entity should not be used anymore. The entity also won't be attached to
   * identy scope.
   * 
   * @return row ID of newly inserted entity
   */
  public long insertWithoutSettingPk(T entity) {
    SQLiteStatement stmt = this.statements.getInsertStatement();
    long rowId;
    if (this.db.isDbLockedByCurrentThread()) {
      synchronized (stmt) {
        this.bindValues(stmt, entity);
        rowId = stmt.executeInsert();
      }
    } else {
      // Do TX to acquire a connection before locking the stmt to avoid
      // deadlocks
      this.db.beginTransaction();
      try {
        synchronized (stmt) {
          this.bindValues(stmt, entity);
          rowId = stmt.executeInsert();
        }
        this.db.setTransactionSuccessful();
      } finally {
        this.db.endTransaction();
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
    return this.executeInsert(entity, this.statements.getInsertOrReplaceStatement());
  }

  private long executeInsert(T entity, SQLiteStatement stmt) {
    long rowId;
    if (this.db.isDbLockedByCurrentThread()) {
      synchronized (stmt) {
        this.bindValues(stmt, entity);
        rowId = stmt.executeInsert();
      }
    } else {
      // Do TX to acquire a connection before locking the stmt to avoid
      // deadlocks
      this.db.beginTransaction();
      try {
        synchronized (stmt) {
          this.bindValues(stmt, entity);
          rowId = stmt.executeInsert();
        }
        this.db.setTransactionSuccessful();
      } finally {
        this.db.endTransaction();
      }
    }
    this.updateKeyAfterInsertAndAttach(entity, rowId, true);
    return rowId;
  }

  protected void updateKeyAfterInsertAndAttach(T entity, long rowId, boolean lock) {
    if (rowId != -1) {
      K key = this.updateKeyAfterInsert(entity, rowId);
      this.attachEntity(key, entity, lock);
    } else {
      // TODO When does this actually happen? Should we throw instead?
      DaoLog.w("Could not insert row (executeInsert returned -1)");
    }
  }

  /**
   * Reads all available rows from the given cursor and returns a list of
   * entities.
   */
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
      if (this.identityScope != null) {
        this.identityScope.lock();
        this.identityScope.reserveRoom(count);
      }
      try {
        do {
          list.add(this.loadCurrent(cursor, 0, false));
        } while (cursor.moveToNext());
      } finally {
        if (this.identityScope != null) {
          this.identityScope.unlock();
        }
      }
    }
    return list;
  }

  /** Internal use only. Considers identity scope. */
  final protected T loadCurrent(Cursor cursor, int offset, boolean lock) {
    if (this.identityScopeLong != null) {
      if (offset != 0) {
        // Occurs with deep loads (left outer joins)
        if (cursor.isNull(this.pkOrdinal + offset)) {
          return null;
        }
      }

      long key = cursor.getLong(this.pkOrdinal + offset);
      T entity = lock ? this.identityScopeLong.get2(key) : this.identityScopeLong.get2NoLock(key);
      if (entity != null) {
        return entity;
      } else {
        entity = this.readEntity(cursor, offset);
        if (lock) {
          this.identityScopeLong.put2(key, entity);
        } else {
          this.identityScopeLong.put2NoLock(key, entity);
        }
        this.attachEntity(entity);
        return entity;
      }
    } else if (this.identityScope != null) {
      K key = this.readKey(cursor, offset);
      if ((offset != 0) && (key == null)) {
        // Occurs with deep loads (left outer joins)
        return null;
      }
      T entity = lock ? this.identityScope.get(key) : this.identityScope.getNoLock(key);
      if (entity != null) {
        return entity;
      } else {
        entity = this.readEntity(cursor, offset);
        this.attachEntity(key, entity, lock);
        return entity;
      }
    } else {
      // Check offset, assume a value !=0 indicating a potential outer join, so
      // check PK
      if (offset != 0) {
        K key = this.readKey(cursor, offset);
        if (key == null) {
          // Occurs with deep loads (left outer joins)
          return null;
        }
      }
      T entity = this.readEntity(cursor, offset);
      this.attachEntity(entity);
      return entity;
    }
  }

  /** Internal use only. Considers identity scope. */
  final protected <O> O loadCurrentOther(AbstractDao<O, ?> dao, Cursor cursor, int offset) {
    return dao.loadCurrent(cursor, offset, /* TODO check this */true);
  }

  /** A raw-style query where you can pass any WHERE clause and arguments. */
  public List<T> queryRaw(String where, String... selectionArg) {
    Cursor cursor = this.db.rawQuery(this.statements.getSelectAll() + where, selectionArg);
    return this.loadAllAndCloseCursor(cursor);
  }

  /**
   * Creates a repeatable {@link Query} object based on the given raw SQL where
   * you can pass any WHERE clause and arguments.
   */
  public Query<T> queryRawCreate(String where, Object... selectionArg) {
    List<Object> argList = Arrays.asList(selectionArg);
    return this.queryRawCreateListArgs(where, argList);
  }

  /**
   * Creates a repeatable {@link Query} object based on the given raw SQL where
   * you can pass any WHERE clause and arguments.
   */
  public Query<T> queryRawCreateListArgs(String where, Collection<Object> selectionArg) {
    return Query.internalCreate(this, this.statements.getSelectAll() + where, selectionArg.toArray());
  }

  public void deleteAll() {
    // String sql = SqlUtils.createSqlDelete(config.tablename, null);
    // db.execSQL(sql);

    this.db.execSQL("DELETE FROM '" + this.config.tablename + "'");
    if (this.identityScope != null) {
      this.identityScope.clear();
    }
  }

  /**
   * Deletes the given entity from the database. Currently, only single value PK
   * entities are supported.
   */
  public void delete(T entity) {
    this.assertSinglePk();
    K key = this.getKeyVerified(entity);
    this.deleteByKey(key);
  }

  /**
   * Deletes an entity with the given PK from the database. Currently, only
   * single value PK entities are supported.
   */
  public void deleteByKey(K key) {
    this.assertSinglePk();
    SQLiteStatement stmt = this.statements.getDeleteStatement();
    if (this.db.isDbLockedByCurrentThread()) {
      synchronized (stmt) {
        this.deleteByKeyInsideSynchronized(key, stmt);
      }
    } else {
      // Do TX to acquire a connection before locking the stmt to avoid
      // deadlocks
      this.db.beginTransaction();
      try {
        synchronized (stmt) {
          this.deleteByKeyInsideSynchronized(key, stmt);
        }
        this.db.setTransactionSuccessful();
      } finally {
        this.db.endTransaction();
      }
    }
    if (this.identityScope != null) {
      this.identityScope.remove(key);
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
    this.assertSinglePk();
    SQLiteStatement stmt = this.statements.getDeleteStatement();
    List<K> keysToRemoveFromIdentityScope = null;
    this.db.beginTransaction();
    try {
      synchronized (stmt) {
        if (this.identityScope != null) {
          this.identityScope.lock();
          keysToRemoveFromIdentityScope = new ArrayList<K>();
        }
        try {
          if (entities != null) {
            for (T entity : entities) {
              K key = this.getKeyVerified(entity);
              this.deleteByKeyInsideSynchronized(key, stmt);
              if (keysToRemoveFromIdentityScope != null) {
                keysToRemoveFromIdentityScope.add(key);
              }
            }
          }
          if (keys != null) {
            for (K key : keys) {
              this.deleteByKeyInsideSynchronized(key, stmt);
              if (keysToRemoveFromIdentityScope != null) {
                keysToRemoveFromIdentityScope.add(key);
              }
            }
          }
        } finally {
          if (this.identityScope != null) {
            this.identityScope.unlock();
          }
        }
      }
      this.db.setTransactionSuccessful();
      if ((keysToRemoveFromIdentityScope != null) && (this.identityScope != null)) {
        this.identityScope.remove(keysToRemoveFromIdentityScope);
      }
    } finally {
      this.db.endTransaction();
    }
  }

  /**
   * Deletes the given entities in the database using a transaction.
   * 
   * @param entities
   *          The entities to delete.
   */
  public void deleteInTx(Iterable<T> entities) {
    this.deleteInTxInternal(entities, null);
  }

  /**
   * Deletes the given entities in the database using a transaction.
   * 
   * @param entities
   *          The entities to delete.
   */
  public void deleteInTx(T... entities) {
    this.deleteInTxInternal(Arrays.asList(entities), null);
  }

  /**
   * Deletes all entities with the given keys in the database using a
   * transaction.
   * 
   * @param keys
   *          Keys of the entities to delete.
   */
  public void deleteByKeyInTx(Iterable<K> keys) {
    this.deleteInTxInternal(null, keys);
  }

  /**
   * Deletes all entities with the given keys in the database using a
   * transaction.
   * 
   * @param keys
   *          Keys of the entities to delete.
   */
  public void deleteByKeyInTx(K... keys) {
    this.deleteInTxInternal(null, Arrays.asList(keys));
  }

  /**
   * Resets all locally changed properties of the entity by reloading the values
   * from the database.
   */
  public void refresh(T entity) {
    this.assertSinglePk();
    K key = this.getKeyVerified(entity);
    String sql = this.statements.getSelectByKey();
    String[] keyArray = new String[] { key.toString() };
    Cursor cursor = this.db.rawQuery(sql, keyArray);
    try {
      boolean available = cursor.moveToFirst();
      if (!available) {
        throw new DaoException("Entity does not exist in the database anymore: " + entity.getClass() + " with key " + key);
      } else if (!cursor.isLast()) {
        throw new DaoException("Expected unique result, but count was " + cursor.getCount());
      }
      this.readEntity(cursor, entity, 0);
      this.attachEntity(key, entity, true);
    } finally {
      cursor.close();
    }
  }

  public void update(T entity) {
    this.assertSinglePk();
    SQLiteStatement stmt = this.statements.getUpdateStatement();
    if (this.db.isDbLockedByCurrentThread()) {
      synchronized (stmt) {
        this.updateInsideSynchronized(entity, stmt, true);
      }
    } else {
      // Do TX to acquire a connection before locking the stmt to avoid
      // deadlocks
      this.db.beginTransaction();
      try {
        synchronized (stmt) {
          this.updateInsideSynchronized(entity, stmt, true);
        }
        this.db.setTransactionSuccessful();
      } finally {
        this.db.endTransaction();
      }
    }
  }

  public QueryBuilder<T> queryBuilder() {
    return QueryBuilder.internalCreate(this);
  }

  protected void updateInsideSynchronized(T entity, SQLiteStatement stmt, boolean lock) {
    // To do? Check if it's worth not to bind PKs here (performance).
    this.bindValues(stmt, entity);
    int index = this.config.allColumns.length + 1;
    K key = this.getKey(entity);
    if (key instanceof Long) {
      stmt.bindLong(index, (Long) key);
    } else if (key == null) {
      throw new DaoException("Cannot update entity without key - was it inserted before?");
    } else {
      stmt.bindString(index, key.toString());
    }
    stmt.execute();
    this.attachEntity(key, entity, lock);
  }

  /**
   * Attaches the entity to the identity scope. Calls attachEntity(T entity).
   * 
   * @param key
   *          Needed only for identity scope, pass null if there's none.
   * @param entity
   *          The entitiy to attach
   * */
  protected final void attachEntity(K key, T entity, boolean lock) {
    if ((this.identityScope != null) && (key != null)) {
      if (lock) {
        this.identityScope.put(key, entity);
      } else {
        this.identityScope.putNoLock(key, entity);
      }
    }
    this.attachEntity(entity);
  }

  /**
   * Sub classes with relations additionally set the DaoMaster here.
   * 
   * @param entity
   *          The entitiy to attach
   * */
  protected void attachEntity(T entity) {
  }

  /**
   * Updates the given entities in the database using a transaction.
   * 
   * @param entities
   *          The entities to insert.
   */
  public void updateInTx(Iterable<T> entities) {
    SQLiteStatement stmt = this.statements.getUpdateStatement();
    this.db.beginTransaction();
    try {
      synchronized (stmt) {
        if (this.identityScope != null) {
          this.identityScope.lock();
        }
        try {
          for (T entity : entities) {
            this.updateInsideSynchronized(entity, stmt, false);
          }
        } finally {
          if (this.identityScope != null) {
            this.identityScope.unlock();
          }
        }
      }
      this.db.setTransactionSuccessful();
    } finally {
      this.db.endTransaction();
    }
  }

  /**
   * Updates the given entities in the database using a transaction.
   * 
   * @param entities
   *          The entities to update.
   */
  public void updateInTx(T... entities) {
    this.updateInTx(Arrays.asList(entities));
  }

  protected void assertSinglePk() {
    if (this.config.pkColumns.length != 1) {
      throw new DaoException(this + " (" + this.config.tablename + ") does not have a single-column primary key");
    }
  }

  public long count() {
    return DatabaseUtils.queryNumEntries(this.db, '\'' + this.config.tablename + '\'');
  }

  /**
   * See {@link #getKey(Object)}, but guarantees that the returned key is never
   * null (throws if null).
   */
  protected K getKeyVerified(T entity) {
    K key = this.getKey(entity);
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

  /**
   * Gets the SQLiteDatabase for custom database access. Not needed for greenDAO
   * entities.
   */
  public SQLiteDatabase getDatabase() {
    return this.db;
  }

  /**
   * just stores the given entity, if the primary key field is filled, it will
   * be updated, a check if the given primary key exists will be performed.
   * Otherwise it will be inserted.
   * 
   * @see AbstractDao#save(Object, boolean)
   * @param entity
   *          the entity to save
   * @return the saved entity
   */
  public T save(T entity) {
    return this.save(entity, true);
  }

  /**
   * just stores the given entity. if <code>checkExisting</code> is set to
   * <code>true</code>, it will be checked if the given entity with the given
   * primary key exists in the database
   * 
   * @param entity
   *          the entity to save
   * @param checkExistingPK
   *          if <code>true</code> the primary key of the entity will be checked
   *          against the database
   * @return the saved entity
   */
  public T save(T entity, boolean checkExistingPK) {
    K primaryKeyValue = this.getKey(entity);
    if (primaryKeyValue == null) { // insert if there is no PK
      this.insert(entity);
    } else {
      if (checkExistingPK) { // if have to chack, load the entity for the PK and
                             // insert or update
        T loadedEntity = this.load(primaryKeyValue);
        if (loadedEntity != null) {
          this.update(entity);
        } else {
          this.insert(entity);
        }
      } else { // if pk is set and no check wanted, just update. maybe an error
               // will be thrown
        this.update(entity);
      }
    }
    return entity;
  }

  /**
   * Reads the values from the current position of the given cursor and returns
   * a new entity.
   */
  abstract protected T readEntity(Cursor cursor, int offset);

  /**
   * Reads the key from the current position of the given cursor, or returns
   * null if there's no single-value key.
   */
  abstract protected K readKey(Cursor cursor, int offset);

  /**
   * Reads the values from the current position of the given cursor into an
   * existing entity.
   */
  abstract protected void readEntity(Cursor cursor, T entity, int offset);

  /**
   * Binds the entity's values to the statement. Make sure to synchronize the
   * statement outside of the method.
   */
  abstract protected void bindValues(SQLiteStatement stmt, T entity);

  /**
   * Updates the entity's key if possible (only for Long PKs currently). This
   * method must always return the entity's key regardless of whether the key
   * existed before or not.
   */
  abstract protected K updateKeyAfterInsert(T entity, long rowId);

  /**
   * Returns the value of the primary key, if the entity has a single primary
   * key, or, if not, null. Returns null if entity is null.
   */
  abstract protected K getKey(T entity);

  /**
   * Returns true if the Entity class can be updated, e.g. for setting the PK
   * after insert.
   */
  abstract protected boolean isEntityUpdateable();
}
