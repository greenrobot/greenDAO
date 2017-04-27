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

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class StandardDatabase implements Database {
    private final SQLiteDatabase delegate;

    public StandardDatabase(SQLiteDatabase delegate) {
        this.delegate = delegate;
    }

    @Override
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return delegate.rawQuery(sql, selectionArgs);
    }

    @Override
    public void execSQL(String sql) throws SQLException {
        delegate.execSQL(sql);
    }

    @Override
    public void beginTransaction() {
        delegate.beginTransaction();
    }

    @Override
    public void endTransaction() {
        delegate.endTransaction();
    }

    @Override
    public boolean inTransaction() {
        return delegate.inTransaction();
    }

    @Override
    public void setTransactionSuccessful() {
        delegate.setTransactionSuccessful();
    }

    @Override
    public void execSQL(String sql, Object[] bindArgs) throws SQLException {
        delegate.execSQL(sql, bindArgs);
    }

    @Override
    public DatabaseStatement compileStatement(String sql) {
        return new StandardDatabaseStatement(delegate.compileStatement(sql));
    }

    @Override
    public boolean isDbLockedByCurrentThread() {
        return delegate.isDbLockedByCurrentThread();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public Object getRawDatabase() {
        return delegate;
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return delegate;
    }
}
