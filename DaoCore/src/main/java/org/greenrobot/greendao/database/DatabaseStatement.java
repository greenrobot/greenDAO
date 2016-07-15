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

public interface DatabaseStatement {
    void execute();

    long simpleQueryForLong();

    void bindNull(int index);

    long executeInsert();

    void bindString(int index, String value);

    void bindBlob(int index, byte[] value);

    void bindLong(int index, long value);

    void clearBindings();

    void bindDouble(int index, double value);

    void close();

    Object getRawStatement();
}
