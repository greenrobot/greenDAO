package de.greenrobot.dao.wrapper;

import android.database.sqlite.SQLiteStatement;

public class SQLiteStatementWrapper extends SQLiteProgramWrapper {

	private final SQLiteStatement sqliteStatement;
	private final net.sqlcipher.database.SQLiteStatement sqlCypheredStatement;

	public SQLiteStatementWrapper(SQLiteStatement sqliteStatement) {
		super(sqliteStatement);
		this.sqliteStatement = sqliteStatement;
		this.sqlCypheredStatement = null;
	}

	public SQLiteStatementWrapper(net.sqlcipher.database.SQLiteStatement sqlCypheredStatement) {
		super(sqlCypheredStatement);
		this.sqliteStatement = null;
		this.sqlCypheredStatement = sqlCypheredStatement;
	}

	public boolean isCypheredDb() {
		return sqlCypheredStatement != null;
	}

	/**
	 * Execute this SQL statement, if it is not a query. For example, CREATE TABLE, DELTE, INSERT, etc.
	 * 
	 * @throws android.database.SQLException
	 *             If the SQL string is invalid for some reason
	 */
	public void execute() {
		if (isCypheredDb())
			sqlCypheredStatement.execute();
		else
			sqliteStatement.execute();
	}

	/**
	 * Execute this SQL statement and return the ID of the row inserted due to this call. The SQL statement should be an INSERT for this to be a useful call.
	 * 
	 * @return the row ID of the last row inserted, if this insert is successful. -1 otherwise.
	 * 
	 * @throws android.database.SQLException
	 *             If the SQL string is invalid for some reason
	 */
	public long executeInsert() {
		return isCypheredDb() ? sqlCypheredStatement.executeInsert() : sqliteStatement.executeInsert();
	}

	/**
	 * Execute a statement that returns a 1 by 1 table with a numeric value. For example, SELECT COUNT(*) FROM table;
	 * 
	 * @return The result of the query.
	 * 
	 * @throws android.database.sqlite.SQLiteDoneException
	 *             if the query returns zero rows
	 */
	public long simpleQueryForLong() {
		return isCypheredDb() ? sqlCypheredStatement.simpleQueryForLong() : sqliteStatement.simpleQueryForLong();
	}

	/**
	 * Execute a statement that returns a 1 by 1 table with a text value. For example, SELECT COUNT(*) FROM table;
	 * 
	 * @return The result of the query.
	 * 
	 * @throws android.database.sqlite.SQLiteDoneException
	 *             if the query returns zero rows
	 */
	public String simpleQueryForString() {
		return isCypheredDb() ? sqlCypheredStatement.simpleQueryForString() : sqliteStatement.simpleQueryForString();
	}

}
