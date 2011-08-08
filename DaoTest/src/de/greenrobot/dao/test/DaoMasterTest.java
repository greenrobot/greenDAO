package de.greenrobot.dao.test;


public class DaoMasterTest extends AbstractDaoMasterTest<DaoMaster> {

    public DaoMasterTest() {
        super(DaoMaster.class);
    }

    public void testInsertAndLoad() {
        SimpleEntity entity = new SimpleEntity();
        daoMaster.insert(entity);
        Long id = entity.getId();
        assertNotNull(id);
        SimpleEntity entity2 = daoMaster.load(SimpleEntity.class, id);
        assertNotNull(entity2);
        assertNotSame(entity, entity2); // Unless we'll cache stuff one day
    }

}