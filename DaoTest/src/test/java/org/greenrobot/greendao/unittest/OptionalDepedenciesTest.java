package org.greenrobot.greendao.unittest;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.database.EncryptedDatabase;
import org.greenrobot.greendao.identityscope.IdentityScope;
import org.greenrobot.greendao.query.CountQuery;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.LazyList;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;
import org.greenrobot.greendao.rx.RxDao;
import org.junit.Ignore;
import org.junit.Test;


import static org.mockito.Mockito.mock;

/**
 * We should not expose any optional library classes in signatures of greenDAO's primary classes and interfaces.
 * Reflection utils like Mockito should not fail with NoClassDefFoundError or the likes.
 */
public class OptionalDepedenciesTest {
    @Test(expected = ClassNotFoundException.class)
    public void testOptionalDependenciesAbsentRx() throws Exception {
        Class.forName("rx.Observable");
    }

    @Test(expected = ClassNotFoundException.class)
    @Ignore("Why is it still on classpath??")
    public void testOptionalDependenciesAbsentSQLCipher() throws Exception {
        Class.forName("net.sqlcipher.database.SQLiteDatabase");
    }

    @Test
    public void testMockitoMocks() {
        mock(DaoMaster.class).newSession();
        mock(DaoSession.class).getDatabase();
        mock(Database.class).getRawDatabase();
        mock(DatabaseStatement.class).execute();
        mock(IdentityScope.class).clear();
        mock(AbstractDao.class).queryBuilder();
        mock(MinimalEntityDao.class).queryBuilder();
        mock(MinimalEntity.class).getId();
        mock(Query.class).forCurrentThread();
        mock(QueryBuilder.class).build();
        mock(CountQuery.class).forCurrentThread();
        mock(DeleteQuery.class).forCurrentThread();
        mock(Join.class).getTablePrefix();
        mock(LazyList.class).getLoadedCount();
        mock(WhereCondition.class).appendValuesTo(null);
        mock(Property.class).isNull();
        mock(DaoException.class).getMessage();
    }

    @Test(expected = NoClassDefFoundError.class)
    public void testMockitoMocksFailForRx() {
        mock(RxDao.class);
    }

    @Test(expected = NoClassDefFoundError.class)
    @Ignore("Why is it still on classpath??")
    public void testMockitoMocksFailForSQLCipher() {
        mock(EncryptedDatabase.class);
    }

}
