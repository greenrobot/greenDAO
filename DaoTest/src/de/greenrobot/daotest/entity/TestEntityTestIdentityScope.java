package de.greenrobot.daotest.entity;

import de.greenrobot.dao.IdentityScope;
import de.greenrobot.dao.test.TestEntity;

public class TestEntityTestIdentityScope extends TestEntityTest {
    @Override
    protected void setUp() {
        setIdentityScopeBeforeSetUp(new IdentityScope<Long, TestEntity>());
        super.setUp();
    }

    public void testLoadIdScope() {
        TestEntity entity = createEntity(null);
        dao.insert(entity);
        TestEntity entity2 = dao.load(entity.getId());
        TestEntity entity3 = dao.load(entity.getId());

        assertSame(entity, entity2);
        assertSame(entity2, entity3);
    }

    public void testDetach() {
        TestEntity entity = createEntity(null);
        dao.insert(entity);
        dao.detach(entity);
        TestEntity entity2 = dao.load(entity.getId());
        dao.detach(entity2);
        TestEntity entity3 = dao.load(entity.getId());

        assertNotSame(entity, entity2);
        assertNotSame(entity2, entity3);
        assertNotSame(entity, entity3);
    }

    public void testDetachOther() {
        TestEntity entity = createEntity(null);
        dao.insert(entity);
        dao.detach(entity);
        TestEntity entity2 = dao.load(entity.getId());
        dao.detach(entity);
        TestEntity entity3 = dao.load(entity.getId());

        assertSame(entity2, entity3);
    }

    public void testLoadAllScope() {
        TestEntity entity = createEntity(null);
        dao.insert(entity);
        TestEntity entity2 = dao.loadAll().get(0);
        TestEntity entity3 = dao.loadAll().get(0);

        assertSame(entity, entity2);
        assertSame(entity2, entity3);
    }

}
