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

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import org.greenrobot.greendao.DaoException;

import java.lang.reflect.Constructor;

/**
 * SQLiteOpenHelper to allow working with greenDAO's {@link Database} abstraction to create and update database schemas.
 */
public abstract class DatabaseOpenHelper extends SQLiteOpenHelper {

    private final Context context;
    private final String name;
    private final int version;

    private EncryptedHelper encryptedHelper;
    private boolean loadSQLCipherNativeLibs = true;

    public DatabaseOpenHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
        this.name = name;
        this.version = version;
    }

    @SuppressLint("NewApi")
    public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        this.context = context;
        this.name = name;
        this.version = version;
    }

    /**
     * Flag to load SQLCipher native libs (default: true).
     */
    public void setLoadSQLCipherNativeLibs(boolean loadSQLCipherNativeLibs) {
        this.loadSQLCipherNativeLibs = loadSQLCipherNativeLibs;
    }

    /**
     * Like {@link #getWritableDatabase()}, but returns a greenDAO abstraction of the database.
     * The backing DB is an standard {@link SQLiteDatabase}.
     */
    public Database getWritableDb() {
        return wrap(getWritableDatabase());
    }

    /**
     * Like {@link #getReadableDatabase()}, but returns a greenDAO abstraction of the database.
     * The backing DB is an standard {@link SQLiteDatabase}.
     */
    public Database getReadableDb() {
        return wrap(getReadableDatabase());
    }

    protected Database wrap(SQLiteDatabase sqLiteDatabase) {
        return new StandardDatabase(sqLiteDatabase);
    }

    /**
     * Delegates to {@link #onCreate(Database)}, which uses greenDAO's database abstraction.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        onCreate(wrap(db));
    }

    /**
     * Override this if you do not want to depend on {@link SQLiteDatabase}.
     */
    public void onCreate(Database db) {
        // Do nothing by default
    }

    /**
     * Delegates to {@link #onUpgrade(Database, int, int)}, which uses greenDAO's database abstraction.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(wrap(db), oldVersion, newVersion);
    }

    /**
     * Override this if you do not want to depend on {@link SQLiteDatabase}.
     */
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        // Do nothing by default
    }

    /**
     * Delegates to {@link #onOpen(Database)}, which uses greenDAO's database abstraction.
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        onOpen(wrap(db));
    }

    /**
     * Override this if you do not want to depend on {@link SQLiteDatabase}.
     */
    public void onOpen(Database db) {
        // Do nothing by default
    }

    interface EncryptedHelper {
        Database getEncryptedReadableDb(String password);
        Database getEncryptedReadableDb(char[] password);
        Database getEncryptedWritableDb(String password);
        Database getEncryptedWritableDb(char[] password);
    }

    private EncryptedHelper checkEncryptedHelper() {
        if (encryptedHelper == null) {
            try {
                Class.forName("net.sqlcipher.database.SQLiteOpenHelper");
            } catch (ClassNotFoundException e) {
                throw new DaoException("Using an encrypted database requires SQLCipher, " +
                        "make sure to add it to dependencies: " +
                        "https://greenrobot.org/greendao/documentation/database-encryption/");
            }

            // Avoid referencing SqlCipherEncryptedHelper to avoid
            // "Rejecting re-init on previously-failed class java.lang.NoClassDefFoundError" logs
            // if SQLCipher is not in classpath.
            try {
                Class<?> helperClass = Class.forName(
                        "org.greenrobot.greendao.database.SqlCipherEncryptedHelper");
                Constructor<?> constructor = helperClass.getConstructor(
                        DatabaseOpenHelper.class, Context.class, String.class, int.class, boolean.class);
                encryptedHelper = (EncryptedHelper) constructor.newInstance(
                        this, context, name, version, loadSQLCipherNativeLibs);
            } catch (Exception e) {
                throw new DaoException(e);
            }
        }
        return encryptedHelper;
    }

    /**
     * Use this to initialize an encrypted SQLCipher database.
     *
     * @see #onCreate(Database)
     * @see #onUpgrade(Database, int, int)
     */
    public Database getEncryptedWritableDb(String password) {
        EncryptedHelper encryptedHelper = checkEncryptedHelper();
        return encryptedHelper.getEncryptedWritableDb(password);
    }

    /**
     * Use this to initialize an encrypted SQLCipher database.
     *
     * @see #onCreate(Database)
     * @see #onUpgrade(Database, int, int)
     */
    public Database getEncryptedWritableDb(char[] password) {
        EncryptedHelper encryptedHelper = checkEncryptedHelper();
        return encryptedHelper.getEncryptedWritableDb(password);
    }

    /**
     * Use this to initialize an encrypted SQLCipher database.
     *
     * @see #onCreate(Database)
     * @see #onUpgrade(Database, int, int)
     */
    public Database getEncryptedReadableDb(String password) {
        EncryptedHelper encryptedHelper = checkEncryptedHelper();
        return encryptedHelper.getEncryptedReadableDb(password);
    }

    /**
     * Use this to initialize an encrypted SQLCipher database.
     *
     * @see #onCreate(Database)
     * @see #onUpgrade(Database, int, int)
     */
    public Database getEncryptedReadableDb(char[] password) {
        EncryptedHelper encryptedHelper = checkEncryptedHelper();
        return encryptedHelper.getEncryptedReadableDb(password);
    }

}
