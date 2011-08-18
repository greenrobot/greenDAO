package de.greenrobot.dao.test;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.dao.test.AbcdefEntity;
import de.greenrobot.dao.test.AbcdefEntityDao;

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
