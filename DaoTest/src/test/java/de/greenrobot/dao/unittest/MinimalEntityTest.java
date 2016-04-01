package de.greenrobot.dao.unittest;

import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;

import de.greenrobot.dao.query.Query;
import de.greenrobot.daotest.dummyapp.BuildConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 16)
public class MinimalEntityTest {

    private DaoSession daoSession;
    private MinimalEntityDao minimalEntityDao;

    @Before
    public void setUp() {
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(RuntimeEnvironment.application, null, null);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();
        minimalEntityDao = daoSession.getMinimalEntityDao();
    }

    @Test
    public void testBasics() {
        MinimalEntity entity = new MinimalEntity();
        daoSession.insert(entity);
        assertNotNull(entity.getId());
        assertNotNull(minimalEntityDao.load(entity.getId()));
        assertEquals(1, minimalEntityDao.count());
        assertEquals(1, daoSession.loadAll(MinimalEntity.class).size());

        daoSession.update(entity);
        daoSession.delete(entity);
        assertNull(minimalEntityDao.load(entity.getId()));
    }

    @Test
    // Testing the work around for Process.myTid() being always 0 in Robolectric
    public void testQueryForCurrentThread() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Query<MinimalEntity>[] queryHolder = new Query[1];
        new Thread() {
            @Override
            public void run() {
                try {
                    queryHolder[0] = minimalEntityDao.queryBuilder().build();
                    queryHolder[0].list();
                } finally {
                    latch.countDown();
                }
            }
        }.start();
        latch.await();
        Query<MinimalEntity> query = queryHolder[0].forCurrentThread();
        query.list();
    }

}
