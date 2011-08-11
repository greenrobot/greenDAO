package de.greenrobot.dao.test;

import java.util.List;

import de.greenrobot.dao.IdentityScope;

/**
 * @author Markus
 * 
 */
public class RelationEntityTestIdentityScope extends RelationEntityTest {

    @Override
    protected void setUp() {
        setIdentityScopeBeforeSetUp(new IdentityScope<Long, RelationEntity>());
        super.setUp();
    }

    public void testToOneLoadDeepIdentityScope() {
        RelationEntity entity = insertEntityWithRelations(42l);
        RelationEntity entity2 = insertEntityWithRelations(42l);
        entity = dao.loadDeep(entity.getId());
        entity2 = dao.loadDeep(entity2.getId());
        assertFalse(entity.getId().equals(entity2.getId()));
        assertTestEntity(entity);
        assertTestEntity(entity2);
        assertSame(entity.getTestEntity(), entity2.getTestEntity());
    }

    public void testQueryDeepIdentityScope() {
        insertEntityWithRelations(42l);
        RelationEntity entity2 = insertEntityWithRelations(42l);
        String columnName = RelationEntityDao.Properties.SimpleString.columnName;
        List<RelationEntity> entityList = dao.queryDeep("WHERE T." + columnName + "=?", "findMe");
        assertEquals(2, entityList.size());
        RelationEntity entity = entityList.get(0);
        assertTestEntity(entity);
        entity2 = entityList.get(1);
        assertTestEntity(entity2);
        assertSame(entity.getTestEntity(), entity2.getTestEntity());
    }

}
