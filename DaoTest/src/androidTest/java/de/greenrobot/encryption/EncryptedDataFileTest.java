package de.greenrobot.encryption;

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
