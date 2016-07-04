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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Extends android.database.sqlite.SQLiteOpenHelper to allow working with greenDAO's {@link DatabaseOpenHelper} for
 * an {@link Database} abstraction to create and update an unencrypted database.
 */
public abstract class StandardDatabaseOpenHelper extends SQLiteOpenHelper implements DatabaseOpenHelper {

    public StandardDatabaseOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    public StandardDatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public Database getWritableDb() {
        return wrap(getWritableDatabase());
    }

    public Database getReadableDb() {
        return wrap(getReadableDatabase());
    }

    protected Database wrap(SQLiteDatabase sqLiteDatabase) {
        return new StandardDatabase(sqLiteDatabase);
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
