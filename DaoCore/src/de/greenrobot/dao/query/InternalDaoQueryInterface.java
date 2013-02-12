package de.greenrobot.dao.query;

import java.util.List;

import android.database.Cursor;
import de.greenrobot.dao.TableStatements;

/** For internal use by greenDAO only. */
public interface InternalDaoQueryInterface<T> {
    T loadCurrent(Cursor cursor, int offset, boolean lock);

    List<T> loadAllAndCloseCursor(Cursor cursor);

    T loadUniqueAndCloseCursor(Cursor cursor);

    TableStatements getStatements();
}
