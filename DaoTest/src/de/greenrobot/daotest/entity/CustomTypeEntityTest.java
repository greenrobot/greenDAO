package de.greenrobot.daotest.entity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import de.greenrobot.daotest.CustomTypeEntity;
import de.greenrobot.daotest.CustomTypeEntityDao;

public class CustomTypeEntityTest extends AbstractDaoTestLongPk<CustomTypeEntityDao, CustomTypeEntity> {

    public CustomTypeEntityTest() {
        super(CustomTypeEntityDao.class);
    }

    @Override
    protected CustomTypeEntity createEntity(Long key) {
        CustomTypeEntity entity = new CustomTypeEntity();
        entity.setId(key);
        return entity;
    }

}
