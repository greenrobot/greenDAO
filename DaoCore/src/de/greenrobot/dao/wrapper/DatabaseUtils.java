package de.greenrobot.dao.wrapper;

import android.database.Cursor;

public class DatabaseUtils {

	private static final String[] countProjection = new String[] { "count(*)" };

	/**
	 * Query the table for the number of rows in the table.
	 * 
	 * @param db
	 *            the database the table is in
	 * @param table
	 *            the name of the table to query
	 * @return the number of rows in the table
	 */
	public static long queryNumEntries(SQLiteDatabaseWrapper db, String table) {
		Cursor cursor = db.query(table, countProjection, null, null, null, null, null);
		try {
			cursor.moveToFirst();
			return cursor.getLong(0);
		} finally {
			cursor.close();
		}
	}

}
