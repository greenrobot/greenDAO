package de.greenrobot.encryption;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.greenrobot.greendao.daotest.DaoMaster;
import org.greenrobot.greendao.daotest.DaoMaster.DevOpenHelper;
import org.greenrobot.greendao.daotest.DaoMaster.OpenHelper;
import org.greenrobot.greendao.daotest.DaoSession;
import org.greenrobot.greendao.daotest.SimpleEntity;
import org.greenrobot.greendao.database.Database;

public class EncryptedDatabaseOpenHelperTest extends ApplicationTestCase {

    public EncryptedDatabaseOpenHelperTest() {
        super(Application.class);
    }

    public void testEncryptedDevOpenHelper() {
        createApplication();
        Database db = new DevOpenHelper(getApplication(), null).getEncryptedReadableDb("password");
        assertDbEncryptedAndFunctional(db);
    }

    public void testEncryptedOpenHelper() {
        createApplication();
        Database db = new OpenHelper(getApplication(), null) {
            @Override
            public void onUpgrade(Database db, int oldVersion, int newVersion) {

            }
        }.getEncryptedReadableDb("password");
        assertDbEncryptedAndFunctional(db);
    }

    private void assertDbEncryptedAndFunctional(Database db) {
        EncryptedDbUtils.assertEncryptedDbUsed(db);
        DaoSession daoSession = new DaoMaster(db).newSession();
        daoSession.insert(new SimpleEntity());
        assertEquals(1, daoSession.loadAll(SimpleEntity.class).size());
    }

}
