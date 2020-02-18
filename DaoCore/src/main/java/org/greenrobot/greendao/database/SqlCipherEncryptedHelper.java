package org.greenrobot.greendao.database;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

class SqlCipherEncryptedHelper extends SQLiteOpenHelper implements DatabaseOpenHelper.EncryptedHelper {

    private final DatabaseOpenHelper delegate;

    public SqlCipherEncryptedHelper(DatabaseOpenHelper delegate, Context context, String name, int version, boolean loadLibs) {
        super(context, name, null, version);
        this.delegate = delegate;
        if (loadLibs) {
            SQLiteDatabase.loadLibs(context);
        }
    }

    private Database wrap(SQLiteDatabase sqLiteDatabase) {
        return new EncryptedDatabase(sqLiteDatabase);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        delegate.onCreate(wrap(db));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        delegate.onUpgrade(wrap(db), oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        delegate.onOpen(wrap(db));
    }

    @Override
    public Database getEncryptedReadableDb(String password) {
        return wrap(getReadableDatabase(password));
    }

    @Override
    public Database getEncryptedReadableDb(char[] password) {
        return wrap(getReadableDatabase(password));
    }

    @Override
    public Database getEncryptedWritableDb(String password) {
        return wrap(getWritableDatabase(password));
    }

    @Override
    public Database getEncryptedWritableDb(char[] password) {
        return wrap(getWritableDatabase(password));
    }

}
