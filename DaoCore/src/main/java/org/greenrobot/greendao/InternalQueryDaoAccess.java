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

package org.greenrobot.greendao;

import java.util.List;

import android.database.Cursor;
import org.greenrobot.greendao.internal.TableStatements;

/** For internal use by greenDAO only. */
public final class InternalQueryDaoAccess<T> {
    private final AbstractDao<T, ?> dao;

    public InternalQueryDaoAccess(AbstractDao<T, ?> abstractDao) {
        dao = abstractDao;
    }

    public T loadCurrent(Cursor cursor, int offset, boolean lock) {
        return dao.loadCurrent(cursor, offset, lock);
    }

    public List<T> loadAllAndCloseCursor(Cursor cursor) {
        return dao.loadAllAndCloseCursor(cursor);
    }

    public T loadUniqueAndCloseCursor(Cursor cursor) {
        return dao.loadUniqueAndCloseCursor(cursor);
    }

    public TableStatements getStatements() {
        return dao.getStatements();
    }

    public static <T2> TableStatements getStatements(AbstractDao<T2, ?> dao) {
        return dao.getStatements();
    }

}