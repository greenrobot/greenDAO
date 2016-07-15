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

/**
 * Database abstraction used internally by greenDAO.
 */
public interface Database {
    Cursor rawQuery(String sql, String[] selectionArgs);

    void execSQL(String sql) throws SQLException;

    void beginTransaction();

    void endTransaction();

    boolean inTransaction();

    void setTransactionSuccessful();

    void execSQL(String sql, Object[] bindArgs) throws SQLException;

    DatabaseStatement compileStatement(String sql);

    boolean isDbLockedByCurrentThread();

    void close();

    Object getRawDatabase();
}
