package de.greenrobot.encryption;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.database.SQLCipherDatabase;

/**
 * Helper to create encrypted databases.
 */
public class EncryptedDbFactory {
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
        return new SQLCipherDatabase(sqLiteDatabase);
    }
}
