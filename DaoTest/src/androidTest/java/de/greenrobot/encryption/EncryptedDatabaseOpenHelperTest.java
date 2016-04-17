package de.greenrobot.encryption;

import android.app.Application;
import android.test.ApplicationTestCase;

import de.greenrobot.dao.database.Database;
import de.greenrobot.daotest.DaoMaster;
import de.greenrobot.daotest.DaoMaster.EncryptedDevOpenHelper;
import de.greenrobot.daotest.DaoMaster.EncryptedOpenHelper;
import de.greenrobot.daotest.DaoSession;
import de.greenrobot.daotest.SimpleEntity;

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
