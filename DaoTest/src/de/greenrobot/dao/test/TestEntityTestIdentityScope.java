package de.greenrobot.dao.test;

import de.greenrobot.dao.IdentityScope;

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

    public void testLoadAllScope() {
        TestEntity entity = createEntity(null);
        dao.insert(entity);
        TestEntity entity2 = dao.loadAll().get(0);
        TestEntity entity3 = dao.loadAll().get(0);
        
        assertSame(entity, entity2);
        assertSame(entity2, entity3);
    }

}
