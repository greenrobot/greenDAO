package org.greenrobot.greendao.example;

import android.app.Application;

import net.sqlcipher.database.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.example.DaoMaster.DevOpenHelper;

public class App extends Application {
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        SQLiteDatabase.loadLibs(getApplicationContext());
        DevOpenHelper helper = new DevOpenHelper(this, "notes-db-encrypted");
        Database db = helper.getEncryptedReadableDb("super-secret");
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
