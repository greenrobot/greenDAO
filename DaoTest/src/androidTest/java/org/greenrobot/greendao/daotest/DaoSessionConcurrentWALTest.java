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
