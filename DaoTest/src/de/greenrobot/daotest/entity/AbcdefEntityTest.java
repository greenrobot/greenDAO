package de.greenrobot.daotest.entity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.AbcdefEntity;
import de.greenrobot.daotest.AbcdefEntityDao;

public class AbcdefEntityTest extends AbstractDaoTestLongPk<AbcdefEntityDao, AbcdefEntity> {

    public AbcdefEntityTest() {
        super(AbcdefEntityDao.class);
    }

    @Override
    protected AbcdefEntity createEntity(Long key) {
        AbcdefEntity entity = new AbcdefEntity();
        entity.setId(key);
        return entity;
    }

}
