/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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

package org.greenrobot.greendao.daotest.encrypted;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.greenrobot.greendao.daotest.DaoMaster;
import org.greenrobot.greendao.daotest.DaoMaster.DevOpenHelper;
import org.greenrobot.greendao.daotest.DaoMaster.OpenHelper;
import org.greenrobot.greendao.daotest.DaoSession;
import org.greenrobot.greendao.daotest.SimpleEntity;
import org.greenrobot.greendao.database.Database;

public class EncryptedDatabaseOpenHelperTest extends ApplicationTestCase {

    public EncryptedDatabaseOpenHelperTest() {
        super(Application.class);
    }

    public void testEncryptedDevOpenHelper() {
        createApplication();
        Database db = new DevOpenHelper(getApplication(), null).getEncryptedReadableDb("password");
        assertDbEncryptedAndFunctional(db);
    }

    public void testEncryptedOpenHelper() {
        createApplication();
        Database db = new OpenHelper(getApplication(), null) {
            @Override
            public void onUpgrade(Database db, int oldVersion, int newVersion) {

            }
        }.getEncryptedReadableDb("password");
        assertDbEncryptedAndFunctional(db);
    }

    private void assertDbEncryptedAndFunctional(Database db) {
        EncryptedDbUtils.assertEncryptedDbUsed(db);
        DaoSession daoSession = new DaoMaster(db).newSession();
        daoSession.insert(new SimpleEntity());
        assertEquals(1, daoSession.loadAll(SimpleEntity.class).size());
    }

}
