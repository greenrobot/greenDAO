package de.greenrobot.orm;

import android.database.sqlite.SQLiteDatabase;

public class DbUtils {
    
    public static void vacuum(SQLiteDatabase db) {
        db.execSQL("VACUUM");
    }

}
