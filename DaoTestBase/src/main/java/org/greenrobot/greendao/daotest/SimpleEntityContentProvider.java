package org.greenrobot.greendao.daotest;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.greenrobot.greendao.DaoLog;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.database.Database;

import org.greenrobot.greendao.daotest.DaoSession;
import org.greenrobot.greendao.daotest.SimpleEntityDao;

/* Copy this code snippet into your AndroidManifest.xml inside the <application> element:

    <provider
        android:name="org.greenrobot.greendao.daotest.SimpleEntityContentProvider"
        android:authorities="org.greenrobot.greendao.daotest.provider" />
*/

public class SimpleEntityContentProvider extends ContentProvider {

    public static final String AUTHORITY = "org.greenrobot.greendao.daotest.provider";
    public static final String BASE_PATH = "";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/" + BASE_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/" + BASE_PATH;

    private static final String TABLENAME = SimpleEntityDao.TABLENAME;
    private static final String PK = SimpleEntityDao.Properties.Id.columnName;

    private static final int SIMPLEENTITY_DIR = 0;
    private static final int SIMPLEENTITY_ID = 1;

    private static final UriMatcher sURIMatcher;

    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, SIMPLEENTITY_DIR);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SIMPLEENTITY_ID);
    }

    /**
    * This must be set from outside, it's recommended to do this inside your Application object.
    * Subject to change (static isn't nice).
    */
    public static DaoSession daoSession;

    @Override
    public boolean onCreate() {
        // if(daoSession == null) {
        //     throw new IllegalStateException("DaoSession must be set before content provider is created");
        // }
        DaoLog.d("Content Provider started: " + CONTENT_URI);
        return true;
    }

    protected Database getDatabase() {
        if(daoSession == null) {
            throw new IllegalStateException("DaoSession must be set during content provider is active");
        }
        return daoSession.getDatabase();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("This content provider is readonly");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("This content provider is readonly");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        throw new UnsupportedOperationException("This content provider is readonly");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
        case SIMPLEENTITY_DIR:
            queryBuilder.setTables(TABLENAME);
            break;
        case SIMPLEENTITY_ID:
            queryBuilder.setTables(TABLENAME);
            queryBuilder.appendWhere(PK + "="
                    + uri.getLastPathSegment());
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Database db = getDatabase();
        Cursor cursor = queryBuilder.query(((StandardDatabase) db).getSQLiteDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public final String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
        case SIMPLEENTITY_DIR:
            return CONTENT_TYPE;
        case SIMPLEENTITY_ID:
            return CONTENT_ITEM_TYPE;
        default :
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
