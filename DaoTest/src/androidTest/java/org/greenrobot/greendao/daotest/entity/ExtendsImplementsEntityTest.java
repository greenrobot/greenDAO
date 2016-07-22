package org.greenrobot.greendao.daotest.entity;

import java.io.Serializable;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;
import org.greenrobot.greendao.daotest.ExtendsImplementsEntity;
import org.greenrobot.greendao.daotest.ExtendsImplementsEntityDao;
import org.greenrobot.greendao.daotest.TestInterface;
import org.greenrobot.greendao.daotest.TestSuperclass;

public class ExtendsImplementsEntityTest extends
        AbstractDaoTestLongPk<ExtendsImplementsEntityDao, ExtendsImplementsEntity> {

    public ExtendsImplementsEntityTest() {
        super(ExtendsImplementsEntityDao.class);
    }

    @Override
    protected ExtendsImplementsEntity createEntity(Long key) {
        ExtendsImplementsEntity entity = new ExtendsImplementsEntity();
        entity.setId(key);
        return entity;
    }

    public void testInheritance() {
        ExtendsImplementsEntity entity = createEntityWithRandomPk();
        assertTrue(entity instanceof TestSuperclass);
        assertTrue(entity instanceof TestInterface);
        assertTrue(entity instanceof Serializable);
    }

}
