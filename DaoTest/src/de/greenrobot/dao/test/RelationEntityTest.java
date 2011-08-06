package de.greenrobot.dao.test;

import de.greenrobot.testdao.DaoMaster;
import de.greenrobot.testdao.RelationEntity;
import de.greenrobot.testdao.RelationEntityDao;
import de.greenrobot.testdao.TestEntity;
import de.greenrobot.testdao.TestEntityDao;

public class RelationEntityTest extends AbstractDaoTestLongPk<RelationEntityDao, RelationEntity> {

    public RelationEntityTest() {
        super(RelationEntityDao.class);
    }

    @Override
    protected RelationEntity createEntity(Long key) {
        RelationEntity entity = new RelationEntity();
        entity.setId(key);
        return entity;
    }

    public void testToOne() {
        DaoMaster daoMaster = new DaoMaster(db);
        dao = daoMaster.getRelationEntityDao();

        TestEntityDao.createTable(db, false);
        TestEntity testEntity = new TestEntity(42l);
        testEntity.setSimpleStringNotNull("mytest");
        daoMaster.getTestEntityDao().insert(testEntity);

        RelationEntity entity = createEntity(1l);
        entity.setTestId(42l);
        dao.insert(entity);

        entity = dao.load(1l);
        TestEntity testEntity2 = entity.getTestEntity();
        assertNotNull(testEntity2);
        assertEquals(42l, (long) testEntity2.getId());
        assertEquals("mytest", testEntity2.getSimpleStringNotNull());
    }

}
