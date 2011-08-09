package de.greenrobot.performance.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE SIMPLE_ENTITY_NOT_NULL (" + //
                "_id INTEGER PRIMARY KEY NOT NULL ," + // 0
                "SIMPLE_BOOLEAN INTEGER NOT NULL ," + // 1
                "SIMPLE_BYTE INTEGER NOT NULL ," + // 2
                "SIMPLE_SHORT INTEGER NOT NULL ," + // 3
                "SIMPLE_INT INTEGER NOT NULL ," + // 4
                "SIMPLE_LONG INTEGER NOT NULL ," + // 5
                "SIMPLE_FLOAT REAL NOT NULL ," + // 6
                "SIMPLE_DOUBLE REAL NOT NULL ," + // 7
                "SIMPLE_STRING TEXT NOT NULL ," + // 8
                "SIMPLE_BYTE_ARRAY BLOB NOT NULL )"; // 9
        db.execSQL(sql);
        
        String sql2 = "CREATE TABLE MINIMAL_ENTITY (_id INTEGER PRIMARY KEY)";
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS SIMPLE_ENTITY_NOT_NULL");
        db.execSQL("DROP TABLE IF EXISTS MINIMAL_ENTITY");
        onCreate(db);
    }

}
