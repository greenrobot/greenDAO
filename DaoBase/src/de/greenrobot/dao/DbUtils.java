package de.greenrobot.dao;

import android.database.sqlite.SQLiteDatabase;

public class DbUtils {
    
    public static void vacuum(SQLiteDatabase db) {
        db.execSQL("VACUUM");
    }

}
