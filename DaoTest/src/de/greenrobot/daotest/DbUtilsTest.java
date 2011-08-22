package de.greenrobot.daotest;

import java.io.IOException;

import android.database.Cursor;
import de.greenrobot.dao.DbUtils;
import de.greenrobot.dao.test.DbTest;

public class DbUtilsTest extends DbTest {

    public void testExecuteSqlScript() throws IOException {
        DbUtils.executeSqlScript(getApplication(), db, "minimal-entity.sql");
        Cursor cursor = db.rawQuery("SELECT count(*) from MINIMAL_ENTITY", null);
        try {
            cursor.moveToFirst();
            assertEquals(5, cursor.getInt(0));
        } finally {
            cursor.close();
        }
    }

}
