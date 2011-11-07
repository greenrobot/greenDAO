package de.greenrobot.daotest.entity;

import java.io.Serializable;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.ExtendsImplementsEntity;
import de.greenrobot.daotest.ExtendsImplementsEntityDao;
import de.greenrobot.daotest.TestInterface;
import de.greenrobot.daotest.TestSuperclass;

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
