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

public interface DatabaseOpenHelper {
    /**
     * See <a href="http://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html#onCreate(android.database.sqlite.SQLiteDatabase)">
     * android.database.sqlite.SQLiteOpenHelper.onCreate</a>
     */
    void onCreate(Database db);

    /**
     * See <a href="http://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)">
     * android.database.sqlite.SQLiteOpenHelper.onUpgrade</a>
     */
    void onUpgrade(Database db, int oldVersion, int newVersion);

    /**
     * See <a href="http://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html#onOpen(android.database.sqlite.SQLiteDatabase)">
     * android.database.sqlite.SQLiteOpenHelper.onOpen</a>
     */
    void onOpen(Database db);

}
