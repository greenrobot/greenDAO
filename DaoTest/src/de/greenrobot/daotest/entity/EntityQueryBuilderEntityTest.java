package de.greenrobot.daotest.entity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import de.greenrobot.daotest.EntityQueryBuilderEntity;
import de.greenrobot.daotest.EntityQueryBuilderEntityDao;

public class EntityQueryBuilderEntityTest extends AbstractDaoTestLongPk<EntityQueryBuilderEntityDao, EntityQueryBuilderEntity> {

    public EntityQueryBuilderEntityTest() {
        super(EntityQueryBuilderEntityDao.class);
    }

    @Override
    protected EntityQueryBuilderEntity createEntity(Long key) {
        EntityQueryBuilderEntity entity = new EntityQueryBuilderEntity();
        entity.setId(key);
        return entity;
    }

}
