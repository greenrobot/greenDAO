/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * This file is part of greenDAO Generator.
 * 
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.greenrobot.daotest.contentprovider;

import android.database.Cursor;
import android.test.suitebuilder.annotation.Suppress;

import de.greenrobot.dao.test.AbstractDaoSessionTest;
import de.greenrobot.daotest.DaoMaster;
import de.greenrobot.daotest.DaoSession;
import de.greenrobot.daotest.SimpleEntity;
import de.greenrobot.daotest.SimpleEntityContentProvider;
import de.greenrobot.daotest.SimpleEntityDao;

@Suppress
// TODO Activate once the gradle build is fixed (AndroidManifest.xml is not used for instrumentTest)
public class SimpleEntityContentProviderTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    public SimpleEntityContentProviderTest() {
        super(DaoMaster.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SimpleEntityContentProvider.daoSession = daoSession;
    }

    public void testQuery() {
        SimpleEntity entity = new SimpleEntity();
        entity.setSimpleString("hello");
        daoSession.insert(entity);
        long id = entity.getId();

        SimpleEntity entity2 = new SimpleEntity();
        entity2.setSimpleString("content");
        daoSession.insert(entity2);
        long id2 = entity2.getId();
        Cursor cursor = getContext().getContentResolver().query(SimpleEntityContentProvider.CONTENT_URI, null,
                null, null, "_id");
        assertEquals(2, cursor.getCount());
        int idxId = cursor.getColumnIndexOrThrow(SimpleEntityDao.Properties.Id.columnName);
        int idxString = cursor.getColumnIndexOrThrow(SimpleEntityDao.Properties.SimpleString.columnName);

        assertTrue(cursor.moveToFirst());
        assertEquals("hello", cursor.getString(idxString));
        assertEquals(id, cursor.getLong(idxId));

        assertTrue(cursor.moveToNext());
        assertEquals("content", cursor.getString(idxString));
        assertEquals(id2, cursor.getLong(idxId));
    }

}