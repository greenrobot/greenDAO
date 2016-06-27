package de.greenrobot.encryption;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendaotest.DaoMaster;
import org.greenrobot.greendaotest.DaoMaster.EncryptedDevOpenHelper;
import org.greenrobot.greendaotest.DaoMaster.EncryptedOpenHelper;
import org.greenrobot.greendaotest.DaoSession;
import org.greenrobot.greendaotest.SimpleEntity;

public class EncryptedDatabaseOpenHelperTest extends ApplicationTestCase {

    public EncryptedDatabaseOpenHelperTest() {
        super(Application.class);
    }

    public void testEncryptedDevOpenHelper() {
        createApplication();
        Database db = new EncryptedDevOpenHelper(getApplication(), null).getReadableDatabase("password");
        assertDbEncryptedAndFunctional(db);
    }

    public void testEncryptedOpenHelper() {
        createApplication();
        Database db = new EncryptedOpenHelper(getApplication(), null) {
            @Override
            public void onUpgrade(Database db, int oldVersion, int newVersion) {

            }
        }.getReadableDatabase("password");
        assertDbEncryptedAndFunctional(db);
    }

    private void assertDbEncryptedAndFunctional(Database db) {
        EncryptedDbUtils.assertEncryptedDbUsed(db);
        DaoSession daoSession = new DaoMaster(db).newSession();
        daoSession.insert(new SimpleEntity());
        assertEquals(1, daoSession.loadAll(SimpleEntity.class).size());
    }

}
