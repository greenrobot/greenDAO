package org.greenrobot.greendao.entityannotation;

import android.app.Application;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.entityannotation.DaoMaster.DevOpenHelper;

public class App extends Application {
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DevOpenHelper helper = new DevOpenHelper(this, "annotation-test-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
