package de.greenrobot.dao.test;

import java.util.List;

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
        RelationEntity entity = insertEntityWithRelations();
        entity = dao.load(entity.getId());
        assertTestEntity(entity);
    }

    public void testToOneSelf() {
        DaoMaster daoMaster = new DaoMaster(db);
        dao = daoMaster.getRelationEntityDao();

        RelationEntity entity = createEntity(1l);
        dao.insert(entity);

        entity = dao.load(1l);
        assertNull(entity.getParent());

        entity.setParentId(entity.getId());
        dao.update(entity);

        entity = dao.load(1l);
        RelationEntity parent = entity.getParent();
        assertEquals(entity.getId(), parent.getId());
    }

    public void testToOneLoadDeep() {
        RelationEntity entity = insertEntityWithRelations();
        entity = dao.loadDeep(entity.getId());
        assertTestEntity(entity);
    }

    public void testQueryDeep() {
        insertEntityWithRelations();
        String columnName = RelationEntityDao.Properties.SimpleString.columnName;
        List<RelationEntity> entityList = dao.queryDeep("WHERE T." + columnName + "=?", "findMe");
        assertEquals(1, entityList.size());
        assertTestEntity(entityList.get(0));
    }

    protected RelationEntity insertEntityWithRelations() {
        DaoMaster daoMaster = new DaoMaster(db);
        dao = daoMaster.getRelationEntityDao();

        TestEntityDao.createTable(db, false);
        TestEntity testEntity = new TestEntity(42l);
        testEntity.setSimpleStringNotNull("mytest");
        daoMaster.getTestEntityDao().insert(testEntity);

        RelationEntity parentEntity = createEntity(null);
        parentEntity.setSimpleString("I'm a parent");
        dao.insert(parentEntity);

        RelationEntity entity = createEntity(null);
        entity.setTestId(42l);
        entity.setParentId(parentEntity.getId());
        entity.setSimpleString("findMe");
        dao.insert(entity);

        return entity;
    }

    protected void assertTestEntity(RelationEntity entity) {
        TestEntity testEntity2 = entity.getTestEntity();
        assertNotNull(testEntity2);
        assertEquals(42l, (long) testEntity2.getId());
        assertEquals("mytest", testEntity2.getSimpleStringNotNull());
        assertEquals("I'm a parent", entity.getParent().getSimpleString());
        assertEquals(entity.getParentId(), entity.getParent().getId());
        assertSame(testEntity2, entity.getTestEntity());
    }

}
