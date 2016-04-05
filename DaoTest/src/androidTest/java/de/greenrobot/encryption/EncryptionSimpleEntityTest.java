package de.greenrobot.encryption;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteConstraintException;

import de.greenrobot.dao.database.Database;
import de.greenrobot.daotest.entity.SimpleEntityTest;

public class EncryptionSimpleEntityTest extends SimpleEntityTest {
    @Override
    protected Database createDatabase() {
        return EncryptedDbFactory.createDatabase(getContext(), null, "password");
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
        assertEquals("3.11.0", queryString("select sqlite_version()"));
        String cipherProvider = queryString("PRAGMA cipher_provider_version");
        assertTrue(cipherProvider, cipherProvider.contains("OpenSSL"));
    }

    private String queryString(String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        try {
            assertTrue(cursor.moveToNext());
            return cursor.getString(0);
        } finally {
            cursor.close();
        }
    }

}
