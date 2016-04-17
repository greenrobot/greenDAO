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
package de.greenrobot.dao.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Like android.database.sqlite.SQLiteOpenHelper, but uses greenDAO's {@link Database} abstraction to create and update an unencrypted database.
 */
public abstract class DatabaseOpenHelper extends AbstractDatabaseOpenHelper {
    private SQLiteOpenHelper delegate;

    public DatabaseOpenHelper(Context context, String name, int version) {
        delegate = new Adapter(context, name, null, version);
    }

    public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
        delegate = new Adapter(context, name, factory, version);
    }

    public Database getWritableDatabase() {
        return wrap(delegate.getWritableDatabase());
    }

    public Database getReadableDatabase() {
        return wrap(delegate.getReadableDatabase());
    }

    public void close() {
        delegate.close();
    }

    protected Database wrap(SQLiteDatabase sqLiteDatabase) {
        return new StandardDatabase(sqLiteDatabase);
    }

    private class Adapter extends SQLiteOpenHelper {
        public Adapter(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            DatabaseOpenHelper.this.onCreate(wrap(db));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            DatabaseOpenHelper.this.onUpgrade(wrap(db), oldVersion, newVersion);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            DatabaseOpenHelper.this.onOpen(wrap(db));
        }
    }

}
