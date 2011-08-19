package de.greenrobot.daotest;

import java.util.List;

import de.greenrobot.dao.IdentityScopeType;
import de.greenrobot.dao.test.RelationEntity;
import de.greenrobot.dao.test.RelationEntityDao;

/**
 * @author Markus
 */
public class RelationEntityTestIdentityScope extends RelationEntityTest {

    @Override
    protected void setUp() {
        identityScopeTypeForSession = IdentityScopeType.Session;
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

    public void testToQueryDeepIdentityScope() {
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
    
    public void testLoadDeepIdentityScope() {
        RelationEntity entity = insertEntityWithRelations(42l);
        RelationEntity entity2 = dao.loadDeep(entity.getId());
        RelationEntity entity3 = dao.loadDeep(entity.getId());
        assertSame(entity, entity2);
        assertSame(entity, entity3);
        assertTestEntity(entity);
    }

    public void testQueryDeepIdentityScope() {
        RelationEntity entity = insertEntityWithRelations(42l);
        
        String columnName = RelationEntityDao.Properties.SimpleString.columnName;
        List<RelationEntity> entityList = dao.queryDeep("WHERE T." + columnName + "=?", "findMe");
        RelationEntity entity2  = entityList.get(0);
        entityList = dao.queryDeep("WHERE T." + columnName + "=?", "findMe");
        RelationEntity entity3  = entityList.get(0);

        assertSame(entity, entity2);
        assertSame(entity, entity3);
        assertTestEntity(entity);
    }

}
