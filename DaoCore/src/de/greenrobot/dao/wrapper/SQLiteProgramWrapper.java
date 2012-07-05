package de.greenrobot.dao.wrapper;

import android.database.sqlite.SQLiteProgram;

public class SQLiteProgramWrapper {

	private final SQLiteProgram sqliteProgram;
	private final net.sqlcipher.database.SQLiteProgram sqlCypheredProgram;

	public SQLiteProgramWrapper(SQLiteProgram sqliteProgram) {
		this.sqliteProgram = sqliteProgram;
		this.sqlCypheredProgram = null;
	}

	public SQLiteProgramWrapper(net.sqlcipher.database.SQLiteProgram sqlCypheredProgram) {
		this.sqliteProgram = null;
		this.sqlCypheredProgram = sqlCypheredProgram;
	}

	public boolean isCypheredDb() {
		return sqlCypheredProgram != null;
	}

	/**
	 * Bind a NULL value to this statement. The value remains bound until {@link #clearBindings} is called.
	 * 
	 * @param index
	 *            The 1-based index to the parameter to bind null to
	 */
	public void bindNull(int index) {
		if (isCypheredDb())
			sqlCypheredProgram.bindNull(index);
		else
			sqliteProgram.bindNull(index);
	}

	/**
	 * Bind a long value to this statement. The value remains bound until {@link #clearBindings} is called.
	 * 
	 * @param index
	 *            The 1-based index to the parameter to bind
	 * @param value
	 *            The value to bind
	 */
	public void bindLong(int index, long value) {
		if (isCypheredDb())
			sqlCypheredProgram.bindLong(index, value);
		else
			sqliteProgram.bindLong(index, value);
	}

	/**
	 * Bind a double value to this statement. The value remains bound until {@link #clearBindings} is called.
	 * 
	 * @param index
	 *            The 1-based index to the parameter to bind
	 * @param value
	 *            The value to bind
	 */
	public void bindDouble(int index, double value) {
		if (isCypheredDb())
			sqlCypheredProgram.bindDouble(index, value);
		else
			sqliteProgram.bindDouble(index, value);
	}

	/**
	 * Bind a String value to this statement. The value remains bound until {@link #clearBindings} is called.
	 * 
	 * @param index
	 *            The 1-based index to the parameter to bind
	 * @param value
	 *            The value to bind
	 */
	public void bindString(int index, String value) {
		if (isCypheredDb())
			sqlCypheredProgram.bindString(index, value);
		else
			sqliteProgram.bindString(index, value);
	}

	/**
	 * Bind a byte array value to this statement. The value remains bound until {@link #clearBindings} is called.
	 * 
	 * @param index
	 *            The 1-based index to the parameter to bind
	 * @param value
	 *            The value to bind
	 */
	public void bindBlob(int index, byte[] value) {
		if (isCypheredDb())
			sqlCypheredProgram.bindBlob(index, value);
		else
			sqliteProgram.bindBlob(index, value);
	}

	/**
	 * Clears all existing bindings. Unset bindings are treated as NULL.
	 */
	public void clearBindings() {
		if (isCypheredDb())
			sqlCypheredProgram.clearBindings();
		else
			sqliteProgram.clearBindings();
	}

	/**
	 * Release this program's resources, making it invalid.
	 */
	public void close() {
		if (isCypheredDb())
			sqlCypheredProgram.close();
		else
			sqliteProgram.close();
	}

}
