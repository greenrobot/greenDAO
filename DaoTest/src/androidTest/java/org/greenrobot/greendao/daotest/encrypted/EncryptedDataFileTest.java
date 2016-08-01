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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.daotest.SimpleEntity;

public class EncryptedDataFileTest extends EncryptionSimpleEntityTest {

    public static final String ENCRYPTED_DB_FILE = "encrypted-db-file";

    @Override
    protected Database createDatabase() {
        getContext().deleteDatabase(ENCRYPTED_DB_FILE);
        return EncryptedDbUtils.createDatabase(getContext(), ENCRYPTED_DB_FILE, "password");
        // You can do a sanity check by disabling encryption and see the test fail:
        // SQLiteDatabase sqLiteDatabase = getContext().openOrCreateDatabase(ENCRYPTED_DB_FILE, 0, null);
        // return new StandardDatabase(sqLiteDatabase);
    }

    public void testFileisEncrypted() throws IOException {
        EncryptedDbUtils.assertEncryptedDbUsed(db);

        SimpleEntity simpleEntity = createEntityWithRandomPk();
        String text = "Catch me if you can";
        simpleEntity.setSimpleString(text);
        dao.insert(simpleEntity);
        File dbFile = getContext().getDatabasePath(ENCRYPTED_DB_FILE);
        int length = (int) dbFile.length();
        assertTrue(length > 0);
        byte[] buffer = new byte[length];
        // TODO readAll
        int read = new FileInputStream(dbFile).read(buffer);
        String contents = new String(buffer, 0, read, "US-ASCII");
        assertFalse(contents, contents.startsWith("SQLite"));
        assertFalse(contents, contents.contains("CREATE TABLE"));
        assertFalse(contents, contents.contains(text));
    }

    public void testEncryptedDbUsed() {
        EncryptedDbUtils.assertEncryptedDbUsed(db);
    }
}
