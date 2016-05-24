package de.greenrobot.dao.query;

import de.greenrobot.dao.unittest.*;

import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.WhereCollector;
import de.greenrobot.daotest.dummyapp.BuildConfig;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 16)
public class WhereCollectorTest {

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
    public void testCheckProperty() {
    	WhereCollector whereCollector = new WhereCollector(minimalEntityDao, "T");
    	final Property Id2 = new Property(0, Long.class, "id", true, "_id");

    	whereCollector.checkProperty(Id2);
    }

}
