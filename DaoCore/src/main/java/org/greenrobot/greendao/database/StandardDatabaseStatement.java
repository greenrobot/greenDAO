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

import android.database.sqlite.SQLiteStatement;

public class StandardDatabaseStatement implements DatabaseStatement {
    private final SQLiteStatement delegate;

    public StandardDatabaseStatement(SQLiteStatement delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute() {
        delegate.execute();
    }

    @Override
    public long simpleQueryForLong() {
        return delegate.simpleQueryForLong();
    }

    @Override
    public void bindNull(int index) {
        delegate.bindNull(index);
    }

    @Override
    public long executeInsert() {
        return delegate.executeInsert();
    }

    @Override
    public void bindString(int index, String value) {
        delegate.bindString(index, value);
    }

    @Override
    public void bindBlob(int index, byte[] value) {
        delegate.bindBlob(index, value);
    }

    @Override
    public void bindLong(int index, long value) {
        delegate.bindLong(index, value);
    }

    @Override
    public void clearBindings() {
        delegate.clearBindings();
    }

    @Override
    public void bindDouble(int index, double value) {
        delegate.bindDouble(index, value);
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public Object getRawStatement() {
        return delegate;
    }

}
