package org.greenrobot.greendao.example;

import android.app.Application;
import android.content.Context;

import org.greenrobot.greendao.database.Database;

public class App extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        // regular SQLite database
        ExampleOpenHelper helper = new ExampleOpenHelper(this, "notes-db");
        Database db = helper.getWritableDb();

        // encrypted SQLCipher database
        // note: you need to add SQLCipher to your dependencies, check the build.gradle file
        // ExampleOpenHelper helper = new ExampleOpenHelper(this, "notes-db-encrypted");
        // Database db = helper.getEncryptedWritableDb("encryption-key");

        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static class ExampleOpenHelper extends DaoMaster.OpenHelper {

        public ExampleOpenHelper(Context context, String name) {
            super(context, name);
        }

        @Override
        public void onCreate(Database db) {
            super.onCreate(db);

            // Insert some example data.
            // INSERT INTO NOTE (_id, DATE, TEXT) VALUES(1, 0, 'Example Note')
            db.execSQL("INSERT INTO " + NoteDao.TABLENAME + " (" +
                    NoteDao.Properties.Id.columnName + ", " +
                    NoteDao.Properties.Date.columnName + ", " +
                    NoteDao.Properties.Text.columnName +
                    ") VALUES(1, 0, 'Example Note')");
        }
    }
}
