package org.greenrobot.greendao.daotest.entity;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import org.greenrobot.greendao.daotest.JoinManyToDateEntity;
import org.greenrobot.greendao.daotest.JoinManyToDateEntityDao;

public class JoinManyToDateEntityTest extends AbstractDaoTestLongPk<JoinManyToDateEntityDao, JoinManyToDateEntity> {

    public JoinManyToDateEntityTest() {
        super(JoinManyToDateEntityDao.class);
    }

    @Override
    protected JoinManyToDateEntity createEntity(Long key) {
        JoinManyToDateEntity entity = new JoinManyToDateEntity();
        entity.setId(key);
        return entity;
    }

}
