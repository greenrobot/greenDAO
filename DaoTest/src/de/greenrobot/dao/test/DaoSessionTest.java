package de.greenrobot.dao.test;

public class DaoSessionTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    public DaoSessionTest() {
        super(DaoMaster.class);
    }

    public void testInsertAndLoad() {
        SimpleEntity entity = new SimpleEntity();
        daoSession.insert(entity);
        Long id = entity.getId();
        assertNotNull(id);
        SimpleEntity entity2 = daoSession.load(SimpleEntity.class, id);
        assertNotNull(entity2);
    }

    public void testIdentity() {
        SimpleEntity entity = new SimpleEntity();
        daoSession.insert(entity);
        SimpleEntity entity2 = daoSession.load(SimpleEntity.class, entity.getId());
        SimpleEntity entity3 = daoSession.load(SimpleEntity.class, entity.getId());
        assertSame(entity, entity2);
        assertSame(entity, entity3);
    }

    public void testIdentityPerSession() {
        SimpleEntity entity = new SimpleEntity();
        daoSession.insert(entity);
        DaoSession session2 = daoMaster.newSession();
        SimpleEntity entity2 = session2.load(SimpleEntity.class, entity.getId());
        assertNotSame(entity, entity2);
    }

    public void testSessionReset() {
        SimpleEntity entity = new SimpleEntity();
        daoSession.insert(entity);
        daoSession.clear();
        SimpleEntity entity2 = daoSession.load(SimpleEntity.class, entity.getId());
        assertNotSame(entity, entity2);
    }
}