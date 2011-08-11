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

    public void testToOneClearKey() {
        RelationEntity entity = insertEntityWithRelations(42l);
        assertNotNull(entity.getParent());
        entity.setParentId(null);
        assertNull(entity.getParent());
    }

    public void testToOneClearEntity() {
        RelationEntity entity = insertEntityWithRelations(42l);
        assertNotNull(entity.getParentId());
        entity.setParent(null);
        assertNull(entity.getParentId());
    }

    public void testToOneUpdateKey() {
        RelationEntity entity = insertEntityWithRelations(42l);
        TestEntity testEntity = entity.getTestEntity();
        RelationEntity entity2 = insertEntityWithRelations(43l);
        TestEntity testEntity2 = entity2.getTestEntity();

        entity.setTestId(testEntity2.getId());
        assertEquals(testEntity2.getId(), entity.getTestEntity().getId());

        entity.setTestId(null);
        assertNull(entity.getTestEntity());

        entity.setTestId(testEntity.getId());
        assertEquals(testEntity.getId(), entity.getTestEntity().getId());
    }

    public void testToOneUpdateEntity() {
        RelationEntity entity = insertEntityWithRelations(42l);
        TestEntity testEntity = entity.getTestEntity();
        RelationEntity entity2 = insertEntityWithRelations(43l);
        TestEntity testEntity2 = entity2.getTestEntity();

        entity.setTestEntity(testEntity2);
        assertEquals(testEntity2.getId(), entity.getTestId());

        entity.setTestEntity(null);
        assertNull(entity.getTestId());

        entity.setTestEntity(testEntity);
        assertEquals(testEntity.getId(), entity.getTestId());
    }

    public void testToOneLoadDeep() {
        RelationEntity entity = insertEntityWithRelations(42l);
        entity = dao.loadDeep(entity.getId());
        assertTestEntity(entity);
    }

    public void testToOneNoMatch() {
        RelationEntity entity = insertEntityWithRelations(42l);
        assertNotNull(entity.getTestEntity());
        entity.setTestId(23l);
        entity.setTestIdNotNull(-78);
        assertNull(entity.getTestEntity());
        assertNull(entity.getTestNotNull());
    }

    public void testToOneNoMatchLoadDeep() {
        RelationEntity entity = insertEntityWithRelations(42l);
        assertNotNull(entity.getTestEntity());
        entity.setTestId(23l);
        entity.setTestIdNotNull(-78);
        dao.update(entity);
        entity = dao.loadDeep(entity.getId());
        assertNull(entity.getTestEntity());
        assertNull(entity.getTestNotNull());
    }

    public void testToOneLoadDeepNull() {
        RelationEntity entity = insertEntityWithRelations(42l);
        entity.setParentId(null);
        entity.setTestId(null);
        dao.update(entity);
        entity = dao.loadDeep(entity.getId());
        assertNull(entity.getParent());
        assertNull(entity.getTestEntity());
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
        parentEntity.setTestNotNull(testEntity);
        dao.insert(parentEntity);

        RelationEntity entity = createEntity(null);
        entity.setTestId(testEntityId);
        entity.setParentId(parentEntity.getId());
        entity.setSimpleString("findMe");
        entity.setTestNotNull(testEntity);
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
        assertNotNull(entity.getTestNotNull());
    }

}
