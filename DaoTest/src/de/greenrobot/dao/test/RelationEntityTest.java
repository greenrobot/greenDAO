package de.greenrobot.dao.test;

import java.util.List;

public class RelationEntityTest extends AbstractDaoTestLongPk<RelationEntityDao, RelationEntity> {

    protected DaoMaster daoMaster;

    public RelationEntityTest() {
        super(RelationEntityDao.class);
    }
    
    @Override
    protected void setUp() {
        super.setUp();
        TestEntityDao.createTable(db, false);
        daoMaster = new DaoMaster(db);
        dao = daoMaster.getRelationEntityDao();
    }

    @Override
    protected RelationEntity createEntity(Long key) {
        RelationEntity entity = new RelationEntity();
        entity.setId(key);
        return entity;
    }

    public void testToOne() {
        RelationEntity entity = insertEntityWithRelations(42l);
        entity = dao.load(entity.getId());
        assertTestEntity(entity);
    }

    public void testToOneSelf() {
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
        RelationEntity entity = insertEntityWithRelations(42l);
        entity = dao.loadDeep(entity.getId());
        assertTestEntity(entity);
    }

    public void testQueryDeep() {
        insertEntityWithRelations(42l);
        String columnName = RelationEntityDao.Properties.SimpleString.columnName;
        List<RelationEntity> entityList = dao.queryDeep("WHERE T." + columnName + "=?", "findMe");
        assertEquals(1, entityList.size());
        assertTestEntity(entityList.get(0));
    }

    protected RelationEntity insertEntityWithRelations(Long testEntityId) {
        TestEntity testEntity = daoMaster.getTestEntityDao().load(testEntityId);
        if (testEntity == null) {
            testEntity = new TestEntity(testEntityId);
            testEntity.setSimpleStringNotNull("mytest");
            daoMaster.getTestEntityDao().insert(testEntity);
        }

        RelationEntity parentEntity = createEntity(null);
        parentEntity.setSimpleString("I'm a parent");
        dao.insert(parentEntity);

        RelationEntity entity = createEntity(null);
        entity.setTestId(testEntityId);
        entity.setParentId(parentEntity.getId());
        entity.setSimpleString("findMe");
        dao.insert(entity);

        return entity;
    }

    protected void assertTestEntity(RelationEntity entity) {
        TestEntity testEntity = entity.getTestEntity();
        assertNotNull(testEntity);
        assertEquals(42l, (long) testEntity.getId());
        assertEquals("mytest", testEntity.getSimpleStringNotNull());
        assertEquals("I'm a parent", entity.getParent().getSimpleString());
        assertEquals(entity.getParentId(), entity.getParent().getId());
        assertSame(testEntity, entity.getTestEntity());
    }

}
