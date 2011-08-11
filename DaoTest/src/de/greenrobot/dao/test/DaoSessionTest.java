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
        assertNotSame(entity, entity2); // Unless we'll cache stuff one day
    }

}