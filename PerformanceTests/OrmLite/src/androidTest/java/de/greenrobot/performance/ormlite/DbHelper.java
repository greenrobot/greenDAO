package de.greenrobot.performance.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;

/**
 * https://github.com/j256/ormlite-examples/blob/master/android/HelloAndroid/src/com/example/helloandroid/DatabaseHelper.java
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {

    public DbHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, IndexedStringEntity.class);
            TableUtils.createTable(connectionSource, SimpleEntityNotNull.class);
            TableUtils.createTable(connectionSource, MinimalEntity.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
            int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, IndexedStringEntity.class, true);
            TableUtils.dropTable(connectionSource, SimpleEntityNotNull.class, true);
            TableUtils.dropTable(connectionSource, MinimalEntity.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
