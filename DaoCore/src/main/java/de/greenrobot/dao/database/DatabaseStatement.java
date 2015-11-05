package de.greenrobot.dao.database;

public interface DatabaseStatement {
    void execute();

    long simpleQueryForLong();

    void bindNull(int index);

    long executeInsert();

    void bindString(int index, String value);

    void bindBlob(int index, byte[] value);

    void bindLong(int index, long value);

    void clearBindings();

    void bindDouble(int index, double value);

    void close();
}
