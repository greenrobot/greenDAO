/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.greenrobot.dao.internal;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

/** Internal class used by greenDAO. */
final public class FastCursor implements Cursor {

    private final CursorWindow window;
    private int position;
    private final int count;

    public FastCursor(CursorWindow window) {
        this.window = window;
        count = window.getNumRows();
    }

    @Override
    public int getCount() {
        return window.getNumRows();
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public boolean move(int offset) {
        return moveToPosition(position + offset);
    }

    @Override
    public boolean moveToPosition(int position) {
        if (position >= 0 && position < count) {
            this.position = position;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean moveToFirst() {
        position = 0;
        return count > 0;
    }

    @Override
    public boolean moveToLast() {
        if (count > 0) {
            position = count - 1;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean moveToNext() {
        if (position < count - 1) {
            position++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean moveToPrevious() {
        if (position > 0) {
            position--;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isFirst() {
        return position == 0;
    }

    @Override
    public boolean isLast() {
        return position == count - 1;
    }

    @Override
    public boolean isBeforeFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAfterLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getColumnIndex(String columnName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getColumnName(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getColumnNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getColumnCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        return window.getBlob(position, columnIndex);
    }

    @Override
    public String getString(int columnIndex) {
        return window.getString(position, columnIndex);
    }

    @Override
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short getShort(int columnIndex) {
        return window.getShort(position, columnIndex);
    }

    @Override
    public int getInt(int columnIndex) {
        return window.getInt(position, columnIndex);
    }

    @Override
    public long getLong(int columnIndex) {
        return window.getLong(position, columnIndex);
    }

    @Override
    public float getFloat(int columnIndex) {
        return window.getFloat(position, columnIndex);
    }

    @Override
    public double getDouble(int columnIndex) {
        return window.getDouble(position, columnIndex);
    }

    @Override
    public boolean isNull(int columnIndex) {
        return window.isNull(position, columnIndex);
    }

    @Override
    public void deactivate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean requery() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClosed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getExtras() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle respond(Bundle extras) {
        throw new UnsupportedOperationException();
    }

    /** Since API level 11 */
    public int getType(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    /** Since API level 19 */
    public Uri getNotificationUri() {
        return null;
    }

}
