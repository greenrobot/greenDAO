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

import net.sqlcipher.database.SQLiteConstraintException;

import java.util.List;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.daotest.SimpleEntity;
import org.greenrobot.greendao.daotest.SimpleEntityDao.Properties;
import org.greenrobot.greendao.daotest.entity.SimpleEntityTest;

public class EncryptionSimpleEntityTest extends SimpleEntityTest {
    @Override
    protected Database createDatabase() {
        return EncryptedDbUtils.createDatabase(getContext(), null, "password");
    }

    @Override
    public void testInsertTwice() {
        try {
            super.testInsertTwice();
            fail("Expected SQLCipher exception");
        } catch (SQLiteConstraintException ex) {
            // OK, expected
        }
    }

    public void testEncryptedDbUsed() {
        EncryptedDbUtils.assertEncryptedDbUsed(db);
    }

    public void testOrderAscString() {
        // SQLCipher 3.5.0 does not understand "COLLATE LOCALIZED ASC" and crashed here initially
        List<SimpleEntity> result = dao.queryBuilder().orderAsc(Properties.SimpleString).list();
        assertEquals(0, result.size());
    }
}
