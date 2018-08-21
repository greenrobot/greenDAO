/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * This file is part of greenDAO Generator.
 *
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.greenrobot.greendao.daotest.encrypted;

import android.content.Context;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.EncryptedDatabase;

import java.io.File;

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
            dbFile.getParentFile().mkdir();
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
        assertEquals("3.15.2", queryString(db, "select sqlite_version()"));
        String cipherProvider = queryString(db, "PRAGMA cipher_provider_version");
        assertTrue(cipherProvider, cipherProvider.contains("OpenSSL"));
    }

}
