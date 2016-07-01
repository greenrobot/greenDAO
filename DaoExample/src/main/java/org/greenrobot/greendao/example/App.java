package org.greenrobot.greendao.example;

import android.app.Application;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.example.DaoMaster.EncryptedDevOpenHelper;

public class App extends Application {
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        EncryptedDevOpenHelper helper = new EncryptedDevOpenHelper(this, "notes-db-encrypted");
        Database db = helper.getWritableDatabase("super-secret");
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
