/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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
package org.greenrobot.greendao.database;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase.CursorFactory;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * Like android.database.sqlite.SQLiteOpenHelper, but uses greenDAO's {@link Database} abstraction to create and update
 * an encrypted database.
 */
public abstract class EncryptedDatabaseOpenHelper extends SQLiteOpenHelper implements DatabaseOpenHelper {
    public EncryptedDatabaseOpenHelper(Context context, String name, int version) {
        this(context, name, null, version, true);
    }

    /**
     * @param cursorFactory  Must be null or of type net.sqlcipher.database.SQLiteDatabase.CursorFactory
     *                       (using Object here to prevent DaoMaster referencing any SQLCipher classes, which is nicer
     *                       for plain Java unit tests)
     * @param loadNativeLibs if true, {@link SQLiteDatabase#loadLibs(Context)} will be called.
     */
    public EncryptedDatabaseOpenHelper(Context context, String name, Object cursorFactory, int version,
                                       boolean loadNativeLibs) {
        super(checkLoadnativeLibs(context, loadNativeLibs), name, (CursorFactory) cursorFactory, version);
    }

    // Static method to ensure this is called before super constructor
    private static Context checkLoadnativeLibs(Context context, boolean loadNativeLibs) {
        if (loadNativeLibs) {
            SQLiteDatabase.loadLibs(context);
        }
        return context;
    }

    public Database getWritableDb(String password) {
        return wrap(getWritableDatabase(password));
    }

    public Database getWritableDb(char[] password) {
        return wrap(getWritableDatabase(password));
    }

    public Database getReadableDb(String password) {
        return wrap(getReadableDatabase(password));
    }

    public Database getReadableDb(char[] password) {
        return wrap(getReadableDatabase(password));
    }

    protected Database wrap(SQLiteDatabase sqLiteDatabase) {
        return new EncryptedDatabase(sqLiteDatabase);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onCreate(wrap(db));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(wrap(db), oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        onOpen(wrap(db));
    }

    @Override
    public void onOpen(Database db) {
    }
}
