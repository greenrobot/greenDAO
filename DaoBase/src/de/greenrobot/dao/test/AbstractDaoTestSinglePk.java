package de.greenrobot.dao.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.database.SQLException;
import android.util.Log;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Column;
import de.greenrobot.dao.test.AbstractDaoTest;

/**
 * Default tests for single-PK entities.
 * 
 * @author Markus
 * 
 * @param <D>
 *            DAO class
 * @param <T>
 *            Entity type of the DAO
 * @param <K>
 *            Key type of the DAO
 */
public abstract class AbstractDaoTestSinglePk<D extends AbstractDao<T, K>, T, K> extends AbstractDaoTest<D, T, K> {

    protected Set<K> usedPks;
    private Column pkColumn;

    public AbstractDaoTestSinglePk(Class<D> daoClass) {
        super(daoClass);
        usedPks = new HashSet<K>();
    }

    @Override
    protected void setUp() {
        super.setUp();
        Column[] columns = daoAccess.getColumnModel();
        for (Column column : columns) {
            if (column.primaryKey) {
                if (pkColumn != null) {
                    throw new RuntimeException("Test does not work with multiple PK columns");
                }
                pkColumn = column;
            }
        }
        if (pkColumn == null) {
            throw new RuntimeException("Test does not work without a PK column");
        }

    }

    public void testInsertAndLoad() {
        K pk = nextPk();
        T entity = createEntity(pk);
        dao.insert(entity);
        T entity2 = dao.load(pk);
        assertNotNull(entity2);
        assertNotSame(entity, entity2); // Unless we'll cache stuff one day
        assertEquals(daoAccess.getPrimaryKeyValue(entity), daoAccess.getPrimaryKeyValue(entity2));
    }

    public void testInsertInTx() {
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < 20; i++) {
            list.add(createEntityWithRandomPk());
        }
        dao.insertInTx(list);
        assertEquals(list.size(), dao.count());
    }

    public void testAssignPk() {
        T entity1 = createEntity(null);
        if (daoAccess.isEntityUpdateable() && entity1 != null) {
            T entity2 = createEntity(null);

            dao.insert(entity1);
            dao.insert(entity2);

            K pk1 = daoAccess.getPrimaryKeyValue(entity1);
            K pk2 = daoAccess.getPrimaryKeyValue(entity2);

            assertFalse(pk1.equals(pk2));

            assertNotNull(dao.load(pk1));
            assertNotNull(dao.load(pk2));
        } else {
            Log.d("DAO", "Skipping testAssignPk for " + daoClass);
        }
    }

    public void testCount() {
        assertEquals(0, dao.count());
        dao.insert(createEntityWithRandomPk());
        assertEquals(1, dao.count());
        dao.insert(createEntityWithRandomPk());
        assertEquals(2, dao.count());
    }

    public void testInsertTwice() {
        K pk = nextPk();
        T entity = createEntity(pk);
        dao.insert(entity);
        try {
            dao.insert(entity);
            fail("Inserting twice should not work");
        } catch (SQLException expected) {
            // OK
        }
    }

    public void testInsertOrReplaceTwice() {
        T entity = createEntityWithRandomPk();
        long rowId1 = dao.insert(entity);
        long rowId2 = dao.insertOrReplace(entity);
        assertEquals(rowId1, rowId2);
    }

    public void testDelete() {
        K pk = nextPk();
        dao.deleteByKey(pk);
        T entity = createEntity(pk);
        dao.insert(entity);
        assertNotNull(dao.load(pk));
        dao.deleteByKey(pk);
        assertNull(dao.load(pk));
    }

    public void testRowId() {
        T entity1 = createEntityWithRandomPk();
        T entity2 = createEntityWithRandomPk();
        long rowId1 = dao.insert(entity1);
        long rowId2 = dao.insert(entity2);
        assertTrue(rowId1 != rowId2);
    }

    public void testLoadAll() {
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < 15; i++) {
            T entity = createEntity(nextPk());
            list.add(entity);
        }
        dao.insertInTx(list);
        List<T> loaded = dao.loadAll();
        assertEquals(list.size(), loaded.size());
    }

    public void testQuery() {
        dao.insert(createEntityWithRandomPk());
        K pkForQuery = nextPk();
        dao.insert(createEntity(pkForQuery));
        dao.insert(createEntityWithRandomPk());

        String where = "WHERE " + dao.getPkColumns()[0] + "=?";
        List<T> list = dao.query(where, pkForQuery.toString());
        assertEquals(1, list.size());
        assertEquals(pkForQuery, daoAccess.getPrimaryKeyValue(list.get(0)));
    }

    public void testUpdate() {
        T entity = createEntityWithRandomPk();
        dao.insert(entity);
        dao.update(entity);
        assertEquals(1, dao.count());
    }

    protected K nextPk() {
        for (int i = 0; i < 100000; i++) {
            K pk = createRandomPk();
            if (usedPks.add(pk)) {
                return pk;
            }
        }
        throw new IllegalStateException("Could not find a new PK");
    }

    protected T createEntityWithRandomPk() {
        return createEntity(createRandomPk());
    }

    /** K does not have to be collision free, check nextPk for collision free PKs. */
    protected abstract K createRandomPk();

    /**
     * Creates an insertable entity. If the given key is null, but the entity's PK is not null the method must return
     * null.
     */
    protected abstract T createEntity(K key);

}