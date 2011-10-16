package de.greenrobot.daotest.entity;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.ToManyTargetEntity;
import de.greenrobot.daotest.ToManyTargetEntityDao;

public class ToManyTargetEntityTest extends AbstractDaoTestLongPk<ToManyTargetEntityDao, ToManyTargetEntity> {

    public ToManyTargetEntityTest() {
        super(ToManyTargetEntityDao.class);
    }

    @Override
    protected ToManyTargetEntity createEntity(Long key) {
        ToManyTargetEntity entity = new ToManyTargetEntity();
        entity.setId(key);
        return entity;
    }

}
