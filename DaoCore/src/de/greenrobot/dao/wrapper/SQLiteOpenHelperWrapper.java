package de.greenrobot.dao.wrapper;

import java.io.File;

import net.sqlcipher.database.SQLiteException;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public abstract class SQLiteOpenHelperWrapper {

	private static final String TAG = SQLiteOpenHelperWrapper.class.getSimpleName();

	private final Context mContext;
	private final String mName;
	private final String mPassword;
	private final CursorFactory mFactory;
	private final int mNewVersion;

	private SQLiteDatabaseWrapper mDatabase = null;
	private boolean mIsInitializing = false;

	/**
	 * 
	 * @param context
	 * @param name
	 *            name of the database
	 * @param password
	 *            if not null nor empty the database will be encrypted
	 * @param factory
	 * @param version
	 */
	public SQLiteOpenHelperWrapper(Context context, String name, String password, CursorFactory factory, int version) {
		if (version < 1)
			throw new IllegalArgumentException("Version must be >= 1, was " + version);

		mContext = context;
		mName = name;
		mPassword = password;
		mFactory = factory;
		mNewVersion = version;

		if (mPassword != null && mPassword.length() > 0) {
			// Load SQLcipher libraries if needed
			SQLiteDatabaseWrapper.loadLibs(mContext);
		}
	}

	public boolean isCypheredDb() {
		return mDatabase.isCypheredDb();
	}

	/**
	 * Create and/or open a database that will be used for reading and writing. Once opened successfully, the database is cached, so you can call this method every time you need to write to the database. Make sure to call {@link #close} when you no longer need it.
	 * 
	 * <p>
	 * Errors such as bad permissions or a full disk may cause this operation to fail, but future attempts may succeed if the problem is fixed.
	 * </p>
	 * 
	 * @throws SQLiteException
	 *             if the database cannot be opened for writing
	 * @return a read/write database object valid until {@link #close} is called
	 */
	public synchronized SQLiteDatabaseWrapper getWritableDatabase() {
		if (mDatabase != null && mDatabase.isOpen() && !mDatabase.isReadOnly()) {
			return mDatabase; // The database is already open for business
		}

		if (mIsInitializing) {
			throw new IllegalStateException("getWritableDatabase called recursively");
		}

		// If we have a read-only database open, someone could be using it
		// (though they shouldn't), which would cause a lock to be held on
		// the file, and our attempts to open the database read-write would
		// fail waiting for the file lock. To prevent that, we acquire the
		// lock on the read-only database, which shuts out other users.

		boolean success = false;
		SQLiteDatabaseWrapper db = null;
		if (mDatabase != null)
			mDatabase.lock();
		try {
			mIsInitializing = true;
			if (mName == null) {
				db = SQLiteDatabaseWrapper.create(null, mPassword);
			} else {
				String path = mContext.getDatabasePath(mName).getPath();

				File dbPathFile = new File(path);
				if (!dbPathFile.exists())
					dbPathFile.getParentFile().mkdirs();

				db = SQLiteDatabaseWrapper.openOrCreateDatabase(path, mPassword, mFactory);
			}

			int version = db.getVersion();
			if (version != mNewVersion) {
				db.beginTransaction();
				try {
					if (version == 0) {
						onCreate(db);
					} else {
						if (version > mNewVersion) {
							Log.w(TAG, "Can't downgrade read-only database from version " + version + " to " + mNewVersion + ": " + db.getPath());
						}
						onUpgrade(db, version, mNewVersion);
					}
					db.setVersion(mNewVersion);
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
			}

			onOpen(db);
			success = true;
			return db;
		} finally {
			mIsInitializing = false;
			if (success) {
				if (mDatabase != null) {
					try {
						mDatabase.close();
					} catch (Exception e) {
					}
					mDatabase.unlock();
				}
				mDatabase = db;
			} else {
				if (mDatabase != null)
					mDatabase.unlock();
				if (db != null)
					db.close();
			}
		}
	}

	/**
	 * Create and/or open a database. This will be the same object returned by {@link #getWritableDatabase} unless some problem, such as a full disk, requires the database to be opened read-only. In that case, a read-only database object will be returned. If the problem is fixed, a future call to {@link #getWritableDatabase} may succeed, in which case the read-only database object will be closed and the read/write object will be returned in the future.
	 * 
	 * @throws SQLiteException
	 *             if the database cannot be opened
	 * @return a database object valid until {@link #getWritableDatabase} or {@link #close} is called.
	 */
	public synchronized SQLiteDatabaseWrapper getReadableDatabase() {
		if (mDatabase != null && mDatabase.isOpen()) {
			return mDatabase; // The database is already open for business
		}

		if (mIsInitializing) {
			throw new IllegalStateException("getReadableDatabase called recursively");
		}

		try {
			return getWritableDatabase();
		} catch (SQLiteException e) {
			if (mName == null)
				throw e; // Can't open a temp database read-only!
			Log.e(TAG, "Couldn't open " + mName + " for writing (will try read-only):", e);
		}

		SQLiteDatabaseWrapper db = null;
		try {
			mIsInitializing = true;
			String path = mContext.getDatabasePath(mName).getPath();
			File databasePath = new File(path);
			File databasesDirectory = new File(mContext.getDatabasePath(mName).getParent());

			if (!databasesDirectory.exists()) {
				databasesDirectory.mkdirs();
			}
			if (!databasePath.exists()) {
				mIsInitializing = false;
				db = getWritableDatabase();
				mIsInitializing = true;
				db.close();
			}
			db = SQLiteDatabaseWrapper.openDatabase(path, mPassword, mFactory, SQLiteDatabase.OPEN_READONLY);
			if (db.getVersion() != mNewVersion) {
				throw new SQLiteException("Can't upgrade read-only database from version " + db.getVersion() + " to " + mNewVersion + ": " + path);
			}

			onOpen(db);
			Log.w(TAG, "Opened " + mName + " in read-only mode");
			mDatabase = db;
			return mDatabase;
		} finally {
			mIsInitializing = false;
			if (db != null && db != mDatabase)
				db.close();
		}
	}

	/**
	 * Close any open database object.
	 */
	public synchronized void close() {
		if (mIsInitializing)
			throw new IllegalStateException("Closed during initialization");

		if (mDatabase != null && mDatabase.isOpen()) {
			mDatabase.close();
			mDatabase = null;
		}
	}

	/**
	 * Called when the database is created for the first time. This is where the creation of tables and the initial population of the tables should happen.
	 * 
	 * @param db
	 *            The database.
	 */
	public abstract void onCreate(SQLiteDatabaseWrapper db);

	/**
	 * Called when the database needs to be upgraded. The implementation should use this method to drop tables, add tables, or do anything else it needs to upgrade to the new schema version.
	 * 
	 * <p>
	 * The SQLite ALTER TABLE documentation can be found <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns you can use ALTER TABLE to insert them into a live table. If you rename or remove columns you can use ALTER TABLE to rename the old table, then create the new table and then populate the new table with the contents of the old table.
	 * 
	 * @param db
	 *            The database.
	 * @param oldVersion
	 *            The old database version.
	 * @param newVersion
	 *            The new database version.
	 */
	public abstract void onUpgrade(SQLiteDatabaseWrapper db, int oldVersion, int newVersion);

	/**
	 * Called when the database has been opened. Override method should check {@link SQLiteDatabase#isReadOnly} before updating the database.
	 * 
	 * @param db
	 *            The database.
	 */
	public void onOpen(SQLiteDatabaseWrapper db) {
	}

}
