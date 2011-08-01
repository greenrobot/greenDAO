package de.greenrobot.dao.test;

import de.greenrobot.orm.test.AbstractDaoTestLongPk;
import de.greenrobot.testdao.SimpleEntity;
import de.greenrobot.testdao.SimpleEntityDao;

public class SimpleEntityTest extends AbstractDaoTestLongPk<SimpleEntityDao, SimpleEntity> {

    public SimpleEntityTest() {
        super(SimpleEntityDao.class);
    }

    @Override
    protected SimpleEntity createEntity(Long key) {
        SimpleEntity entity = new SimpleEntity();
        entity.setId(key);
        return entity;
    }

}
