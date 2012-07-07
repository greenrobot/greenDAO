package de.greenrobot.dao.wrapper;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import net.sqlcipher.database.SQLiteDatabaseCorruptException;
import net.sqlcipher.database.SQLiteDebug;
import net.sqlcipher.database.SQLiteTransactionListener;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteProgram;
import android.database.sqlite.SQLiteStatement;
import android.os.Debug;
import android.os.SystemClock;
import android.util.Log;
import de.greenrobot.dao.wrapper.exception.NotSupportedOperationAndroidException;

public class SQLiteDatabaseWrapper {

	private static final String TAG = SQLiteDatabaseWrapper.class.getSimpleName();

	/**
	 * If set then the SQLiteDatabase is made thread-safe by using locks around critical sections
	 */
	private boolean mLockingEnabled = true;

	/** Synchronize on this when accessing the database */
	private final ReentrantLock mLock = new ReentrantLock(true);

	private long mLockAcquiredWallTime = 0L;
	private long mLockAcquiredThreadTime = 0L;
	private long mLastLockMessageTime = 0L;

	// limit the frequency of complaints about each database to one within 20 sec
	// unless run command adb shell setprop log.tag.Database VERBOSE
	private static final int LOCK_WARNING_WINDOW_IN_MS = 20000;
	/** If the lock is held this long then a warning will be printed when it is released. */
	private static final int LOCK_ACQUIRED_WARNING_TIME_IN_MS = 300;
	private static final int LOCK_ACQUIRED_WARNING_THREAD_TIME_IN_MS = 100;
	private static final int LOCK_ACQUIRED_WARNING_TIME_IN_MS_ALWAYS_PRINT = 2000;

	private final SQLiteDatabase sqliteDatabase;
	private final net.sqlcipher.database.SQLiteDatabase sqlCypheredDatabase;

	private SQLiteDatabaseWrapper(String path, String password, CursorFactory factory, int flags, SQLiteDatabaseHook databaseHook) {
		if (password == null || password.length() == 0) {
			sqliteDatabase = SQLiteDatabase.openDatabase(path, factory, flags);
			sqlCypheredDatabase = null;
		} else {
			sqliteDatabase = null;
			sqlCypheredDatabase = new net.sqlcipher.database.SQLiteDatabase(path, password, (net.sqlcipher.database.SQLiteDatabase.CursorFactory) factory, flags, databaseHook);
		}
	}

	public SQLiteDatabaseWrapper(SQLiteDatabase sqliteDatabase) {
		this.sqliteDatabase = sqliteDatabase;
		this.sqlCypheredDatabase = null;
	}

	public SQLiteDatabaseWrapper(net.sqlcipher.database.SQLiteDatabase sqlCypheredDatabase) {
		this.sqliteDatabase = null;
		this.sqlCypheredDatabase = sqlCypheredDatabase;
	}

	public boolean isCypheredDb() {
		return sqlCypheredDatabase != null;
	}

	public static void loadLibs(Context context) {
		net.sqlcipher.database.SQLiteDatabase.loadLibs(context);
	}

	public static void loadLibs(Context context, File workingDir) {
		net.sqlcipher.database.SQLiteDatabase.loadLibs(context, workingDir);
	}

	public static int releaseMemory() {
		return net.sqlcipher.database.SQLiteDatabase.releaseMemory() + SQLiteDatabase.releaseMemory();
	}

	public void setLockingEnabled(boolean lockingEnabled) {
		if (isCypheredDb())
			sqlCypheredDatabase.setLockingEnabled(lockingEnabled);
		else
			sqliteDatabase.setLockingEnabled(lockingEnabled);
	}

	public void beginTransaction() {
		if (isCypheredDb())
			sqlCypheredDatabase.beginTransaction();
		else
			sqliteDatabase.beginTransaction();
	}

	public void beginTransactionWithListener(SQLiteTransactionListener transactionListener) {
		if (isCypheredDb())
			sqlCypheredDatabase.beginTransactionWithListener(transactionListener);
		else
			throw new NotSupportedOperationAndroidException();
	}

	public void setTransactionSuccessful() {
		if (isCypheredDb())
			sqlCypheredDatabase.setTransactionSuccessful();
		else
			sqliteDatabase.setTransactionSuccessful();
	}

	public void endTransaction() {
		if (isCypheredDb())
			sqlCypheredDatabase.endTransaction();
		else
			sqliteDatabase.endTransaction();
	}

	/**
	 * return true if there is a transaction pending
	 */
	public boolean inTransaction() {
		return isCypheredDb() ? sqlCypheredDatabase.inTransaction() : sqliteDatabase.inTransaction();
	}

	/**
	 * Checks if the database lock is held by this thread.
	 * 
	 * @return true, if this thread is holding the database lock.
	 */
	public boolean isDbLockedByCurrentThread() {
		return isCypheredDb() ? sqlCypheredDatabase.isDbLockedByCurrentThread() : sqliteDatabase.isDbLockedByCurrentThread();
	}

	/**
	 * Checks if the database is locked by another thread. This is just an estimate, since this status can change at any time, including after the call is made but before the result has been acted upon.
	 * 
	 * @return true, if the database is locked by another thread
	 */
	public boolean isDbLockedByOtherThreads() {
		return isCypheredDb() ? sqlCypheredDatabase.isDbLockedByOtherThreads() : sqliteDatabase.isDbLockedByOtherThreads();
	}

	/**
	 * Temporarily end the transaction to let other threads run. The transaction is assumed to be successful so far. Do not call setTransactionSuccessful before calling this. When this returns a new transaction will have been created but not marked as successful.
	 * 
	 * @return true if the transaction was yielded
	 * @deprecated if the db is locked more than once (becuase of nested transactions) then the lock will not be yielded. Use yieldIfContendedSafely instead.
	 */
	@Deprecated
	public boolean yieldIfContended() {
		return isCypheredDb() ? sqlCypheredDatabase.yieldIfContended() : sqliteDatabase.yieldIfContended();
	}

	/**
	 * Temporarily end the transaction to let other threads run. The transaction is assumed to be successful so far. Do not call setTransactionSuccessful before calling this. When this returns a new transaction will have been created but not marked as successful. This assumes that there are no nested transactions (beginTransaction has only been called once) and will throw an exception if that is not the case.
	 * 
	 * @return true if the transaction was yielded
	 */
	public boolean yieldIfContendedSafely() {
		return isCypheredDb() ? sqlCypheredDatabase.yieldIfContendedSafely() : sqliteDatabase.yieldIfContendedSafely();
	}

	/**
	 * Temporarily end the transaction to let other threads run. The transaction is assumed to be successful so far. Do not call setTransactionSuccessful before calling this. When this returns a new transaction will have been created but not marked as successful. This assumes that there are no nested transactions (beginTransaction has only been called once) and will throw an exception if that is not the case.
	 * 
	 * @param sleepAfterYieldDelay
	 *            if > 0, sleep this long before starting a new transaction if the lock was actually yielded. This will allow other background threads to make some more progress than they would if we started the transaction immediately.
	 * @return true if the transaction was yielded
	 */
	public boolean yieldIfContendedSafely(long sleepAfterYieldDelay) {
		if (isCypheredDb())
			return sqlCypheredDatabase.yieldIfContendedSafely(sleepAfterYieldDelay);
		else
			throw new NotSupportedOperationAndroidException();
	}

	public Map<String, String> getSyncedTables() {
		return isCypheredDb() ? sqlCypheredDatabase.getSyncedTables() : sqliteDatabase.getSyncedTables();
	}

	/**
	 * Open the database according to the flags {@link #OPEN_READWRITE} {@link #OPEN_READONLY} {@link #CREATE_IF_NECESSARY} and/or {@link #NO_LOCALIZED_COLLATORS}.
	 * 
	 * <p>
	 * Sets the locale of the database to the the system's current locale. Call {@link #setLocale} if you would like something else.
	 * </p>
	 * 
	 * @param path
	 *            to database file to open and/or create
	 * @param factory
	 *            an optional factory class that is called to instantiate a cursor when query is called, or null for default
	 * @param flags
	 *            to control database access mode
	 * @return the newly opened database
	 * @throws SQLiteException
	 *             if the database cannot be opened
	 */
	public static SQLiteDatabaseWrapper openDatabase(String path, String password, CursorFactory factory, int flags, SQLiteDatabaseHook databaseHook) {
		SQLiteDatabaseWrapper sqliteDatabase = null;
		try {
			// Open the database.
			sqliteDatabase = new SQLiteDatabaseWrapper(path, password, factory, flags, databaseHook);
			if (SQLiteDebug.DEBUG_SQL_STATEMENTS) {
				sqliteDatabase.enableSqlTracing(path);
			}
			if (SQLiteDebug.DEBUG_SQL_TIME) {
				sqliteDatabase.enableSqlProfiling(path);
			}
		} catch (SQLiteDatabaseCorruptException e) {
			// Try to recover from this, if we can.
			// TODO: should we do this for other open failures?
			Log.e(TAG, "Deleting and re-creating corrupt database " + path, e);
			// EventLog.writeEvent(EVENT_DB_CORRUPT, path);
			if (!path.equalsIgnoreCase(":memory")) {
				// delete is only for non-memory database files
				new File(path).delete();
			}
			sqliteDatabase = new SQLiteDatabaseWrapper(path, password, factory, flags, databaseHook);
		}
		ActiveDatabases.getInstance().mActiveDatabases.add(new WeakReference<SQLiteDatabaseWrapper>(sqliteDatabase));
		return sqliteDatabase;
	}

	private void enableSqlProfiling(String path) {
		// TODO Need to call private native methods of SQLiteDatabase.... Should I use reflection ?
	}

	private void enableSqlTracing(String path) {
		// TODO Need to call private native methods of SQLiteDatabase.... Should I use reflection ?
	}

	public static SQLiteDatabaseWrapper openDatabase(String path, String password, CursorFactory factory, int flags) {
		return openDatabase(path, password, factory, flags, null);
	}

	public static SQLiteDatabaseWrapper openOrCreateDatabase(File file, String password, CursorFactory factory, SQLiteDatabaseHook databaseHook) {
		return openOrCreateDatabase(file.getPath(), password, factory, databaseHook);
	}

	public static SQLiteDatabaseWrapper openOrCreateDatabase(String path, String password, CursorFactory factory, SQLiteDatabaseHook databaseHook) {
		return openDatabase(path, password, factory, SQLiteDatabase.CREATE_IF_NECESSARY, databaseHook);
	}

	/**
	 * Equivalent to openDatabase(file.getPath(), factory, CREATE_IF_NECESSARY).
	 */
	public static SQLiteDatabaseWrapper openOrCreateDatabase(File file, String password, CursorFactory factory) {
		return openOrCreateDatabase(file.getPath(), password, factory, null);
	}

	/**
	 * Equivalent to openDatabase(path, factory, CREATE_IF_NECESSARY).
	 */
	public static SQLiteDatabaseWrapper openOrCreateDatabase(String path, String password, CursorFactory factory) {
		return openDatabase(path, password, factory, SQLiteDatabase.CREATE_IF_NECESSARY, null);
	}

	/**
	 * Create a memory backed SQLite database. Its contents will be destroyed when the database is closed.
	 * 
	 * <p>
	 * Sets the locale of the database to the the system's current locale. Call {@link #setLocale} if you would like something else.
	 * </p>
	 * 
	 * @param factory
	 *            an optional factory class that is called to instantiate a cursor when query is called
	 * @return a SQLiteDatabase object, or null if the database can't be created
	 */
	public static SQLiteDatabaseWrapper create(CursorFactory factory, String password) {
		return openDatabase(":memory:", password, factory, SQLiteDatabase.CREATE_IF_NECESSARY);
	}

	/**
	 * Close the database.
	 */
	public void close() {
		if (isCypheredDb())
			sqlCypheredDatabase.close();
		else
			sqliteDatabase.close();
	}

	/**
	 * Gets the database version.
	 * 
	 * @return the database version
	 */
	public int getVersion() {
		return isCypheredDb() ? sqlCypheredDatabase.getVersion() : sqliteDatabase.getVersion();
	}

	/**
	 * Sets the database version.
	 * 
	 * @param version
	 *            the new database version
	 */
	public void setVersion(int version) {
		if (isCypheredDb())
			sqlCypheredDatabase.setVersion(version);
		else
			sqliteDatabase.setVersion(version);
	}

	/**
	 * Returns the maximum size the database may grow to.
	 * 
	 * @return the new maximum database size
	 */
	public long getMaximumSize() {
		return isCypheredDb() ? sqlCypheredDatabase.getMaximumSize() : sqliteDatabase.getMaximumSize();
	}

	/**
	 * Sets the maximum size the database will grow to. The maximum size cannot be set below the current size.
	 * 
	 * @param numBytes
	 *            the maximum database size, in bytes
	 * @return the new maximum database size
	 */
	public long setMaximumSize(long numBytes) {
		return isCypheredDb() ? sqlCypheredDatabase.setMaximumSize(numBytes) : sqliteDatabase.setMaximumSize(numBytes);
	}

	/**
	 * Returns the current database page size, in bytes.
	 * 
	 * @return the database page size, in bytes
	 */
	public long getPageSize() {
		return isCypheredDb() ? sqlCypheredDatabase.getPageSize() : sqliteDatabase.getPageSize();
	}

	/**
	 * Sets the database page size. The page size must be a power of two. This method does not work if any data has been written to the database file, and must be called right after the database has been created.
	 * 
	 * @param numBytes
	 *            the database page size, in bytes
	 */
	public void setPageSize(long numBytes) {
		if (isCypheredDb())
			sqlCypheredDatabase.setPageSize(numBytes);
		else
			sqliteDatabase.setPageSize(numBytes);
	}

	/**
	 * Mark this table as syncable. When an update occurs in this table the _sync_dirty field will be set to ensure proper syncing operation.
	 * 
	 * @param table
	 *            the table to mark as syncable
	 * @param deletedTable
	 *            The deleted table that corresponds to the syncable table
	 */
	public void markTableSyncable(String table, String deletedTable) {
		if (isCypheredDb())
			sqlCypheredDatabase.markTableSyncable(table, deletedTable);
		else
			sqliteDatabase.markTableSyncable(table, deletedTable);
	}

	/**
	 * Mark this table as syncable, with the _sync_dirty residing in another table. When an update occurs in this table the _sync_dirty field of the row in updateTable with the _id in foreignKey will be set to ensure proper syncing operation.
	 * 
	 * @param table
	 *            an update on this table will trigger a sync time removal
	 * @param foreignKey
	 *            this is the column in table whose value is an _id in updateTable
	 * @param updateTable
	 *            this is the table that will have its _sync_dirty
	 */
	public void markTableSyncable(String table, String foreignKey, String updateTable) {
		if (isCypheredDb())
			sqlCypheredDatabase.markTableSyncable(table, foreignKey, updateTable);
		else
			sqliteDatabase.markTableSyncable(table, foreignKey, updateTable);
	}

	/**
	 * Finds the name of the first table, which is editable.
	 * 
	 * @param tables
	 *            a list of tables
	 * @return the first table listed
	 */
	public String findEditTable(String tables) {
		if (isCypheredDb())
			return net.sqlcipher.database.SQLiteDatabase.findEditTable(tables);
		else
			return SQLiteDatabase.findEditTable(tables);
	}

	/**
	 * Compiles an SQL statement into a reusable pre-compiled statement object. The parameters are identical to {@link #execSQL(String)}. You may put ?s in the statement and fill in those values with {@link SQLiteProgram#bindString} and {@link SQLiteProgram#bindLong} each time you want to run the statement. Statements may not return result sets larger than 1x1.
	 * 
	 * @param sql
	 *            The raw SQL statement, may contain ? for unknown values to be bound later.
	 * @return A pre-compiled {@link SQLiteStatement} object. Note that {@link SQLiteStatement}s are not synchronized, see the documentation for more details.
	 */
	public SQLiteStatementWrapper compileStatement(String sql) throws SQLException {
		if (isCypheredDb())
			return new SQLiteStatementWrapper(sqlCypheredDatabase.compileStatement(sql));
		else
			return new SQLiteStatementWrapper(sqliteDatabase.compileStatement(sql));
	}

	/**
	 * Query the given URL, returning a {@link Cursor} over the result set.
	 * 
	 * @param distinct
	 *            true if you want each row to be unique, false otherwise.
	 * @param table
	 *            The table name to compile the query against.
	 * @param columns
	 *            A list of which columns to return. Passing null will return all columns, which is discouraged to prevent reading data from storage that isn't going to be used.
	 * @param selection
	 *            A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	 * @param selectionArgs
	 *            You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
	 * @param groupBy
	 *            A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself). Passing null will cause the rows to not be grouped.
	 * @param having
	 *            A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row groups to be included, and is required when row grouping is not being used.
	 * @param orderBy
	 *            How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 * @param limit
	 *            Limits the number of rows returned by the query, formatted as LIMIT clause. Passing null denotes no LIMIT clause.
	 * @return A {@link Cursor} object, which is positioned before the first entry. Note that {@link Cursor}s are not synchronized, see the documentation for more details.
	 * @see Cursor
	 */
	public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		if (isCypheredDb())
			return sqlCypheredDatabase.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		else
			return sqliteDatabase.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

	}

	/**
	 * Query the given URL, returning a {@link Cursor} over the result set.
	 * 
	 * @param cursorFactory
	 *            the cursor factory to use, or null for the default factory
	 * @param distinct
	 *            true if you want each row to be unique, false otherwise.
	 * @param table
	 *            The table name to compile the query against.
	 * @param columns
	 *            A list of which columns to return. Passing null will return all columns, which is discouraged to prevent reading data from storage that isn't going to be used.
	 * @param selection
	 *            A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	 * @param selectionArgs
	 *            You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
	 * @param groupBy
	 *            A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself). Passing null will cause the rows to not be grouped.
	 * @param having
	 *            A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row groups to be included, and is required when row grouping is not being used.
	 * @param orderBy
	 *            How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 * @param limit
	 *            Limits the number of rows returned by the query, formatted as LIMIT clause. Passing null denotes no LIMIT clause.
	 * @return A {@link Cursor} object, which is positioned before the first entry. Note that {@link Cursor}s are not synchronized, see the documentation for more details.
	 * @see Cursor
	 */
	public Cursor queryWithFactory(CursorFactory cursorFactory, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		if (isCypheredDb())
			return sqlCypheredDatabase.queryWithFactory((net.sqlcipher.database.SQLiteDatabase.CursorFactory) cursorFactory, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		else
			return sqliteDatabase.queryWithFactory(cursorFactory, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
	}

	/**
	 * Query the given table, returning a {@link Cursor} over the result set.
	 * 
	 * @param table
	 *            The table name to compile the query against.
	 * @param columns
	 *            A list of which columns to return. Passing null will return all columns, which is discouraged to prevent reading data from storage that isn't going to be used.
	 * @param selection
	 *            A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	 * @param selectionArgs
	 *            You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
	 * @param groupBy
	 *            A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself). Passing null will cause the rows to not be grouped.
	 * @param having
	 *            A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row groups to be included, and is required when row grouping is not being used.
	 * @param orderBy
	 *            How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 * @return A {@link Cursor} object, which is positioned before the first entry. Note that {@link Cursor}s are not synchronized, see the documentation for more details.
	 * @see Cursor
	 */
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		if (isCypheredDb())
			return sqlCypheredDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		else
			return sqliteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}

	/**
	 * Query the given table, returning a {@link Cursor} over the result set.
	 * 
	 * @param table
	 *            The table name to compile the query against.
	 * @param columns
	 *            A list of which columns to return. Passing null will return all columns, which is discouraged to prevent reading data from storage that isn't going to be used.
	 * @param selection
	 *            A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	 * @param selectionArgs
	 *            You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
	 * @param groupBy
	 *            A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself). Passing null will cause the rows to not be grouped.
	 * @param having
	 *            A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row groups to be included, and is required when row grouping is not being used.
	 * @param orderBy
	 *            How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 * @param limit
	 *            Limits the number of rows returned by the query, formatted as LIMIT clause. Passing null denotes no LIMIT clause.
	 * @return A {@link Cursor} object, which is positioned before the first entry. Note that {@link Cursor}s are not synchronized, see the documentation for more details.
	 * @see Cursor
	 */
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		if (isCypheredDb())
			return sqlCypheredDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		else
			return sqliteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
	}

	/**
	 * Runs the provided SQL and returns a {@link Cursor} over the result set.
	 * 
	 * @param sql
	 *            the SQL query. The SQL string must not be ; terminated
	 * @param selectionArgs
	 *            You may include ?s in where clause in the query, which will be replaced by the values from selectionArgs. The values will be bound as Strings.
	 * @return A {@link Cursor} object, which is positioned before the first entry. Note that {@link Cursor}s are not synchronized, see the documentation for more details.
	 */
	public Cursor rawQuery(String sql, String[] selectionArgs) {
		return isCypheredDb() ? sqlCypheredDatabase.rawQuery(sql, selectionArgs) : sqliteDatabase.rawQuery(sql, selectionArgs);
	}

	/**
	 * Runs the provided SQL and returns a cursor over the result set.
	 * 
	 * @param cursorFactory
	 *            the cursor factory to use, or null for the default factory
	 * @param sql
	 *            the SQL query. The SQL string must not be ; terminated
	 * @param selectionArgs
	 *            You may include ?s in where clause in the query, which will be replaced by the values from selectionArgs. The values will be bound as Strings.
	 * @param editTable
	 *            the name of the first table, which is editable
	 * @return A {@link Cursor} object, which is positioned before the first entry. Note that {@link Cursor}s are not synchronized, see the documentation for more details.
	 */
	public Cursor rawQueryWithFactory(CursorFactory cursorFactory, String sql, String[] selectionArgs, String editTable) {
		if (isCypheredDb())
			return sqlCypheredDatabase.rawQueryWithFactory((net.sqlcipher.database.SQLiteDatabase.CursorFactory) cursorFactory, sql, selectionArgs, editTable);
		else
			return sqliteDatabase.rawQueryWithFactory(cursorFactory, sql, selectionArgs, editTable);
	}

	/**
	 * Runs the provided SQL and returns a cursor over the result set. The cursor will read an initial set of rows and the return to the caller. It will continue to read in batches and send data changed notifications when the later batches are ready.
	 * 
	 * @param sql
	 *            the SQL query. The SQL string must not be ; terminated
	 * @param selectionArgs
	 *            You may include ?s in where clause in the query, which will be replaced by the values from selectionArgs. The values will be bound as Strings.
	 * @param initialRead
	 *            set the initial count of items to read from the cursor
	 * @param maxRead
	 *            set the count of items to read on each iteration after the first
	 * @return A {@link Cursor} object, which is positioned before the first entry. Note that {@link Cursor}s are not synchronized, see the documentation for more details.
	 * 
	 *         This work is incomplete and not fully tested or reviewed, so currently hidden.
	 * @hide
	 */
	public Cursor rawQuery(String sql, String[] selectionArgs, int initialRead, int maxRead) {
		if (isCypheredDb())
			return sqlCypheredDatabase.rawQuery(sql, selectionArgs, initialRead, maxRead);
		else
			throw new NotSupportedOperationAndroidException();
	}

	/**
	 * Convenience method for inserting a row into the database.
	 * 
	 * @param table
	 *            the table to insert the row into
	 * @param nullColumnHack
	 *            SQL doesn't allow inserting a completely empty row, so if initialValues is empty this column will explicitly be assigned a NULL value
	 * @param values
	 *            this map contains the initial column values for the row. The keys should be the column names and the values the column values
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long insert(String table, String nullColumnHack, ContentValues values) {
		if (isCypheredDb())
			return sqlCypheredDatabase.insert(table, nullColumnHack, values);
		else
			return sqliteDatabase.insert(table, nullColumnHack, values);
	}

	/**
	 * Convenience method for inserting a row into the database.
	 * 
	 * @param table
	 *            the table to insert the row into
	 * @param nullColumnHack
	 *            SQL doesn't allow inserting a completely empty row, so if initialValues is empty this column will explicitly be assigned a NULL value
	 * @param values
	 *            this map contains the initial column values for the row. The keys should be the column names and the values the column values
	 * @throws SQLException
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long insertOrThrow(String table, String nullColumnHack, ContentValues values) throws SQLException {
		if (isCypheredDb())
			return sqlCypheredDatabase.insertOrThrow(table, nullColumnHack, values);
		else
			return sqliteDatabase.insertOrThrow(table, nullColumnHack, values);
	}

	/**
	 * Convenience method for replacing a row in the database.
	 * 
	 * @param table
	 *            the table in which to replace the row
	 * @param nullColumnHack
	 *            SQL doesn't allow inserting a completely empty row, so if initialValues is empty this row will explicitly be assigned a NULL value
	 * @param initialValues
	 *            this map contains the initial column values for the row. The key
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long replace(String table, String nullColumnHack, ContentValues initialValues) {
		if (isCypheredDb())
			return sqlCypheredDatabase.replace(table, nullColumnHack, initialValues);
		else
			return sqliteDatabase.replace(table, nullColumnHack, initialValues);
	}

	/**
	 * Convenience method for replacing a row in the database.
	 * 
	 * @param table
	 *            the table in which to replace the row
	 * @param nullColumnHack
	 *            SQL doesn't allow inserting a completely empty row, so if initialValues is empty this row will explicitly be assigned a NULL value
	 * @param initialValues
	 *            this map contains the initial column values for the row. The key
	 * @throws SQLException
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long replaceOrThrow(String table, String nullColumnHack, ContentValues initialValues) throws SQLException {
		if (isCypheredDb())
			return sqlCypheredDatabase.replaceOrThrow(table, nullColumnHack, initialValues);
		else
			return sqliteDatabase.replaceOrThrow(table, nullColumnHack, initialValues);
	}

	/**
	 * General method for inserting a row into the database.
	 * 
	 * @param table
	 *            the table to insert the row into
	 * @param nullColumnHack
	 *            SQL doesn't allow inserting a completely empty row, so if initialValues is empty this column will explicitly be assigned a NULL value
	 * @param initialValues
	 *            this map contains the initial column values for the row. The keys should be the column names and the values the column values
	 * @param conflictAlgorithm
	 *            for insert conflict resolver
	 * @return the row ID of the newly inserted row OR the primary key of the existing row if the input param 'conflictAlgorithm' = {@link #CONFLICT_IGNORE} OR -1 if any error
	 */
	public long insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm) {
		if (isCypheredDb())
			return sqlCypheredDatabase.insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);
		else
			throw new NotSupportedOperationAndroidException();
	}

	/**
	 * Convenience method for deleting rows in the database.
	 * 
	 * @param table
	 *            the table to delete from
	 * @param whereClause
	 *            the optional WHERE clause to apply when deleting. Passing null will delete all rows.
	 * @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all rows and get a count pass "1" as the whereClause.
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		if (isCypheredDb())
			return sqlCypheredDatabase.delete(table, whereClause, whereArgs);
		else
			return sqliteDatabase.delete(table, whereClause, whereArgs);
	}

	/**
	 * Convenience method for updating rows in the database.
	 * 
	 * @param table
	 *            the table to update in
	 * @param values
	 *            a map from column names to new column values. null is a valid value that will be translated to NULL.
	 * @param whereClause
	 *            the optional WHERE clause to apply when updating. Passing null will update all rows.
	 * @return the number of rows affected
	 */
	public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		if (isCypheredDb())
			return sqlCypheredDatabase.update(table, values, whereClause, whereArgs);
		else
			return sqliteDatabase.update(table, values, whereClause, whereArgs);
	}

	/**
	 * Convenience method for updating rows in the database.
	 * 
	 * @param table
	 *            the table to update in
	 * @param values
	 *            a map from column names to new column values. null is a valid value that will be translated to NULL.
	 * @param whereClause
	 *            the optional WHERE clause to apply when updating. Passing null will update all rows.
	 * @param conflictAlgorithm
	 *            for update conflict resolver
	 * @return the number of rows affected
	 */
	public int updateWithOnConflict(String table, ContentValues values, String whereClause, String[] whereArgs, int conflictAlgorithm) {
		if (isCypheredDb())
			return sqlCypheredDatabase.updateWithOnConflict(table, values, whereClause, whereArgs, conflictAlgorithm);
		else
			throw new NotSupportedOperationAndroidException();
	}

	/**
	 * Execute a single SQL statement that is not a query. For example, CREATE TABLE, DELETE, INSERT, etc. Multiple statements separated by ;s are not supported. it takes a write lock
	 * 
	 * @throws SQLException
	 *             If the SQL string is invalid for some reason
	 */
	public void execSQL(String sql) throws SQLException {
		if (isCypheredDb())
			sqlCypheredDatabase.execSQL(sql);
		else
			sqliteDatabase.execSQL(sql);
	}

	public void rawExecSQL(String sql) {
		if (isCypheredDb())
			sqlCypheredDatabase.rawExecSQL(sql);
		else
			throw new NotSupportedOperationAndroidException();
	}

	/**
	 * Execute a single SQL statement that is not a query. For example, CREATE TABLE, DELETE, INSERT, etc. Multiple statements separated by ;s are not supported. it takes a write lock,
	 * 
	 * @param sql
	 * @param bindArgs
	 *            only byte[], String, Long and Double are supported in bindArgs.
	 * @throws SQLException
	 *             If the SQL string is invalid for some reason
	 */
	public void execSQL(String sql, Object[] bindArgs) throws SQLException {
		if (isCypheredDb())
			sqlCypheredDatabase.execSQL(sql, bindArgs);
		else
			sqliteDatabase.execSQL(sql, bindArgs);
	}

	/**
	 * return whether the DB is opened as read only.
	 * 
	 * @return true if DB is opened as read only
	 */
	public boolean isReadOnly() {
		return isCypheredDb() ? sqlCypheredDatabase.isReadOnly() : sqliteDatabase.isReadOnly();
	}

	/**
	 * @return true if the DB is currently open (has not been closed)
	 */
	public boolean isOpen() {
		return isCypheredDb() ? sqlCypheredDatabase.isOpen() : sqliteDatabase.isOpen();

	}

	public boolean needUpgrade(int newVersion) {
		return isCypheredDb() ? sqlCypheredDatabase.needUpgrade(newVersion) : sqliteDatabase.needUpgrade(newVersion);

	}

	/**
	 * Getter for the path to the database file.
	 * 
	 * @return the path to our database file.
	 */
	public final String getPath() {
		return isCypheredDb() ? sqlCypheredDatabase.getPath() : sqliteDatabase.getPath();

	}

	/**
	 * Sets the locale for this database. Does nothing if this database has the NO_LOCALIZED_COLLATORS flag set or was opened read only.
	 * 
	 * @throws SQLException
	 *             if the locale could not be set. The most common reason for this is that there is no collator available for the locale you requested. In this case the database remains unchanged.
	 */
	public void setLocale(Locale locale) {
		if (isCypheredDb())
			sqlCypheredDatabase.setLocale(locale);
		else
			sqliteDatabase.setLocale(locale);
	}

	/**
	 * returns true if the given sql is cached in compiled-sql cache.
	 * 
	 * @hide
	 */
	public boolean isInCompiledSqlCache(String sql) {
		if (isCypheredDb())
			return sqlCypheredDatabase.isInCompiledSqlCache(sql);
		else
			throw new NotSupportedOperationAndroidException();

	}

	/**
	 * purges the given sql from the compiled-sql cache.
	 * 
	 * @hide
	 */
	public void purgeFromCompiledSqlCache(String sql) {
		if (isCypheredDb())
			sqlCypheredDatabase.purgeFromCompiledSqlCache(sql);
		else
			throw new NotSupportedOperationAndroidException();
	}

	/**
	 * remove everything from the compiled sql cache
	 * 
	 * @hide
	 */
	public void resetCompiledSqlCache() {
		if (isCypheredDb())
			sqlCypheredDatabase.resetCompiledSqlCache();
		else
			throw new NotSupportedOperationAndroidException();
	}

	/**
	 * return the current maxCacheSqlCacheSize
	 * 
	 * @hide
	 */
	public synchronized int getMaxSqlCacheSize() {
		if (isCypheredDb())
			return sqlCypheredDatabase.getMaxSqlCacheSize();
		else
			throw new NotSupportedOperationAndroidException();

	}

	/**
	 * set the max size of the compiled sql cache for this database after purging the cache. (size of the cache = number of compiled-sql-statements stored in the cache).
	 * 
	 * max cache size can ONLY be increased from its current size (default = 0). if this method is called with smaller size than the current value of mMaxSqlCacheSize, then IllegalStateException is thrown
	 * 
	 * synchronized because we don't want t threads to change cache size at the same time.
	 * 
	 * @param cacheSize
	 *            the size of the cache. can be (0 to MAX_SQL_CACHE_SIZE)
	 * @throws IllegalStateException
	 *             if input cacheSize > MAX_SQL_CACHE_SIZE or < 0 or < the value set with previous setMaxSqlCacheSize() call.
	 * 
	 * @hide
	 */
	public synchronized void setMaxSqlCacheSize(int cacheSize) {
		if (isCypheredDb())
			sqlCypheredDatabase.setMaxSqlCacheSize(cacheSize);
		else
			throw new NotSupportedOperationAndroidException();
	}

	/**
	 * Locks the database for exclusive access. The database lock must be held when touch the native sqlite3* object since it is single threaded and uses a polling lock contention algorithm. The lock is recursive, and may be acquired multiple times by the same thread. This is a no-op if mLockingEnabled is false.
	 * 
	 * @see #unlock()
	 */
	/* package */void lock() {
		if (!mLockingEnabled)
			return;
		mLock.lock();
		if (SQLiteDebug.DEBUG_LOCK_TIME_TRACKING) {
			if (mLock.getHoldCount() == 1) {
				// Use elapsed real-time since the CPU may sleep when waiting for IO
				mLockAcquiredWallTime = SystemClock.elapsedRealtime();
				mLockAcquiredThreadTime = Debug.threadCpuTimeNanos();
			}
		}
	}

	/**
	 * Releases the database lock. This is a no-op if mLockingEnabled is false.
	 * 
	 * @see #unlock()
	 */
	/* package */void unlock() {
		if (!mLockingEnabled)
			return;
		if (SQLiteDebug.DEBUG_LOCK_TIME_TRACKING) {
			if (mLock.getHoldCount() == 1) {
				checkLockHoldTime();
			}
		}
		mLock.unlock();
	}

	private void checkLockHoldTime() {
		// Use elapsed real-time since the CPU may sleep when waiting for IO
		long elapsedTime = SystemClock.elapsedRealtime();
		long lockedTime = elapsedTime - mLockAcquiredWallTime;
		if (lockedTime < LOCK_ACQUIRED_WARNING_TIME_IN_MS_ALWAYS_PRINT && !Log.isLoggable(TAG, Log.VERBOSE) && (elapsedTime - mLastLockMessageTime) < LOCK_WARNING_WINDOW_IN_MS) {
			return;
		}
		if (lockedTime > LOCK_ACQUIRED_WARNING_TIME_IN_MS) {
			int threadTime = (int) ((Debug.threadCpuTimeNanos() - mLockAcquiredThreadTime) / 1000000);
			if (threadTime > LOCK_ACQUIRED_WARNING_THREAD_TIME_IN_MS || lockedTime > LOCK_ACQUIRED_WARNING_TIME_IN_MS_ALWAYS_PRINT) {
				mLastLockMessageTime = elapsedTime;
				String msg = "lock held on " + getPath() + " for " + lockedTime + "ms. Thread time was " + threadTime + "ms";
				if (SQLiteDebug.DEBUG_LOCK_TIME_TRACKING_STACK_TRACE) {
					Log.d(TAG, msg, new Exception());
				} else {
					Log.d(TAG, msg);
				}
			}
		}
	}

	static class ActiveDatabases {
		private static final ActiveDatabases activeDatabases = new ActiveDatabases();
		private HashSet<WeakReference<SQLiteDatabaseWrapper>> mActiveDatabases = new HashSet<WeakReference<SQLiteDatabaseWrapper>>();

		private ActiveDatabases() {
		} // disable instantiation of this class

		static ActiveDatabases getInstance() {
			return activeDatabases;
		}
	}

}
