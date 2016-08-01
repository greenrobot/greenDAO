/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * This file is part of greenDAO Generator.
 *
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.greenrobot.greendao.daotest;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import org.greenrobot.greendao.DaoLog;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Query;

import java.lang.reflect.Method;

public class DaoSessionConcurrentWALTest extends DaoSessionConcurrentTest {

    @Override
    protected Database createDatabase() {
        int MODE_ENABLE_WRITE_AHEAD_LOGGING = 8;
        getContext().deleteDatabase(DB_NAME);
        SQLiteDatabase sqLiteDatabase = getContext().openOrCreateDatabase(DB_NAME, MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
        return new StandardDatabase(sqLiteDatabase);
    }

    public void testConcurrentLockAndQueryDuringTxWAL() throws InterruptedException {
        if (Build.VERSION.SDK_INT >= 16) {
            try {
                Object rawDatabase = db.getRawDatabase();
                Method method = rawDatabase.getClass().getMethod("isWriteAheadLoggingEnabled");
                boolean walEnabled = (Boolean) method.invoke(rawDatabase);
                if (!walEnabled) {
                    throw new RuntimeException("WAL is disabled. This test will deadlock without WAL");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            DaoLog.e("Sorry, we need at least API level 16 for WAL");
            return;
        }

        final TestEntity entity = createEntity(null);
        dao.insert(entity);
        final Query<TestEntity> query = dao.queryBuilder().build();
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                synchronized (query) {
                    query.forCurrentThread().list();
                }
            }
        };

        initThreads(runnable1);
        // Builds the statement so it is ready immediately in the thread
        query.list();
        doTx(new Runnable() {
            @Override
            public void run() {
                synchronized (query) {
                    query.list();
                }
            }
        });
        latchThreadsDone.await();
    }
}
