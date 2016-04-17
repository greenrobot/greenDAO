package de.greenrobot.encryption;

import android.content.Context;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.database.EncryptedDatabase;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Helper to create encrypted databases.
 */
public class EncryptedDbUtils {
    private static boolean loadedLibs;

    public static Database createDatabase(Context context, String dbName, String password) {
        if (!loadedLibs) {
            loadedLibs = true;
            SQLiteDatabase.loadLibs(context);
        }
        SQLiteDatabase sqLiteDatabase;
        if (dbName == null) {
            sqLiteDatabase = SQLiteDatabase.create(null, password);
        } else {
            File dbFile = context.getDatabasePath(dbName);
            context.deleteDatabase(dbName);
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbFile, password, null);
        }
        return new EncryptedDatabase(sqLiteDatabase);
    }

    public static String queryString(Database db, String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        try {
            assertTrue(cursor.moveToNext());
            return cursor.getString(0);
        } finally {
            cursor.close();
        }
    }

    public static void assertEncryptedDbUsed(Database db) {
        assertEquals("3.11.0", queryString(db, "select sqlite_version()"));
        String cipherProvider = queryString(db, "PRAGMA cipher_provider_version");
        assertTrue(cipherProvider, cipherProvider.contains("OpenSSL"));
    }

}
