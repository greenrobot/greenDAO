package de.greenrobot.encryption;

import net.sqlcipher.database.SQLiteConstraintException;

import de.greenrobot.dao.database.Database;
import de.greenrobot.daotest.entity.SimpleEntityTest;

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

}
