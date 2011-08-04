package de.greenrobot.daoexample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.daoexample.NoteDao;

public class MyDbHelper extends SQLiteOpenHelper {

    public MyDbHelper(Context context) {
        super(context, "note-db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        NoteDao.createTable(db, false);
        Log.d("DaoExample", "Created table " + NoteDao.TABLENAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DaoExample", "Upgrading from " + oldVersion + " to " + newVersion);
        NoteDao.dropTable(db, false);
        onCreate(db);
    }

}
