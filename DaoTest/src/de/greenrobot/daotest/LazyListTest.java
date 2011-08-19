package de.greenrobot.daotest;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import de.greenrobot.dao.CloseableListIterator;
import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.LazyList;
import de.greenrobot.dao.test.TestEntity;
import de.greenrobot.dao.test.TestEntityDao.Properties;

public class LazyListTest extends TestEntityTestBase {

    public void testSizeAndGetAndPeak() {
        ArrayList<TestEntity> list = insert(2);

        LazyList<TestEntity> listLazy = dao.queryBuilder().build().listLazy();
        assertEquals(list.size(), listLazy.size());
        assertNull(listLazy.peak(0));
        assertNull(listLazy.peak(1));

        assertNotNull(listLazy.get(1));
        assertNull(listLazy.peak(0));
        assertNotNull(listLazy.peak(1));

        assertNotNull(listLazy.get(0));
        assertNotNull(listLazy.peak(0));
        assertNotNull(listLazy.peak(1));
    }

    public void testGetAll100() {
        ArrayList<TestEntity> list = insert(100);
        LazyList<TestEntity> listLazy = dao.queryBuilder().orderAsc(Properties.SimpleInteger).build().listLazy();
        assertIds(list, listLazy);
        assertTrue(listLazy.isClosed());
    }

    public void testGetAll100Uncached() {
        ArrayList<TestEntity> list = insert(100);
        LazyList<TestEntity> listLazy = dao.queryBuilder().orderAsc(Properties.SimpleInteger).build()
                .listLazyUncached();
        assertIds(list, listLazy);
        assertFalse(listLazy.isClosed());
        listLazy.close();
    }

    public void testIterator() {
        ArrayList<TestEntity> list = insert(100);
        LazyList<TestEntity> listLazy = dao.queryBuilder().orderAsc(Properties.SimpleInteger).build().listLazy();
        testIerator(list, listLazy, false);
        assertTrue(listLazy.isClosed());
    }

    public void testIteratorUncached() {
        ArrayList<TestEntity> list = insert(100);
        LazyList<TestEntity> listLazy = dao.queryBuilder().orderAsc(Properties.SimpleInteger).build()
                .listLazyUncached();
        testIerator(list, listLazy, true);
        assertFalse(listLazy.isClosed());
        listLazy.close();
    }

    protected void testIerator(ArrayList<TestEntity> list, LazyList<TestEntity> listLazy, boolean uncached) {
        ListIterator<TestEntity> iterator = listLazy.listIterator();
        try {
            iterator.previous();
            fail("previous should throw here");
        } catch (NoSuchElementException expected) {
            // OK
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            assertTrue(iterator.hasNext());
            assertEquals(i > 0, iterator.hasPrevious());
            assertEquals(i, iterator.nextIndex());
            assertEquals(i - 1, iterator.previousIndex());

            if (i > 0) {
                TestEntity entityPrevious = list.get(i - 1);
                assertEquals(entityPrevious.getId(), iterator.previous().getId());
                iterator.next();
            }

            TestEntity entity = list.get(i);
            assertNull(listLazy.peak(i));
            TestEntity lazyEntity = iterator.next();
            if (uncached) {
                assertNull(listLazy.peak(i));
            } else {
                assertNotNull(listLazy.peak(i));
            }
            assertEquals(entity.getId(), lazyEntity.getId());
        }
        assertFalse(iterator.hasNext());
        try {
            iterator.next();
            fail("next should throw here");
        } catch (NoSuchElementException expected) {
            // OK
        }
    }

    public void testEmpty() {
        insert(1);

        LazyList<TestEntity> listLazy = dao.queryBuilder().where(Properties.SimpleInteger.eq(-1)).build().listLazy();
        assertTrue(listLazy.isEmpty());
        assertTrue(listLazy.isClosed());
        try {
            listLazy.get(0);
            fail("Not empty");
        } catch (RuntimeException e) {
            // Expected, OK
        }

    }

    public void testUncached() {
        insert(1);

        LazyList<TestEntity> listLazy = dao.queryBuilder().build().listLazyUncached();
        assertFalse(listLazy.isEmpty());
        assertFalse(listLazy.isClosed());
        TestEntity entity1 = listLazy.get(0);
        TestEntity entity2 = listLazy.get(0);
        assertEquals(entity1.getId(), entity2.getId());
        if (identityScopeForDao == null) {
            assertNotSame(entity1, entity2);
        } else {
            assertSame(entity1, entity2);
        }
        assertFalse(listLazy.isClosed());
        try {
            listLazy.loadRemaining();
            fail("Not empty");
        } catch (DaoException expected) {
            // Expected, OK
        }
        listLazy.close();
        assertTrue(listLazy.isClosed());
    }

    public void testClose() {
        insert(1);

        LazyList<TestEntity> listLazy = dao.queryBuilder().build().listLazy();
        assertFalse(listLazy.isEmpty());
        assertFalse(listLazy.isClosed());
        listLazy.get(0);
        assertTrue(listLazy.isClosed());

        // Closing again should not harm
        listLazy.close();
        listLazy.close();
    }

    public void testAutoClose() {
        insert(10);
        LazyList<TestEntity> lazyList = dao.queryBuilder().build().listLazyUncached();
        CloseableListIterator<TestEntity> iterator = lazyList.listIteratorAutoClose();
        while (iterator.hasNext()) {
            assertFalse(lazyList.isClosed());
            iterator.next();
        }
        assertTrue(lazyList.isClosed());
    }
    
}
