package org.greenrobot.greendao.daotest.entity;

import junit.framework.Assert;
import org.greenrobot.greendao.test.AbstractDaoTestStringPk;
import org.greenrobot.greendao.daotest.StringKeyValueEntity;
import org.greenrobot.greendao.daotest.StringKeyValueEntityDao;

public class StringKeyValueEntityTest extends AbstractDaoTestStringPk<StringKeyValueEntityDao, StringKeyValueEntity> {

    public StringKeyValueEntityTest() {
        super(StringKeyValueEntityDao.class);
    }

    @Override
    protected StringKeyValueEntity createEntity(String key) {
        if(key == null) {
            return null;
        }
        StringKeyValueEntity entity = new StringKeyValueEntity();
        entity.setKey(key);
        return entity;
    }

    public void testInsertWithoutPK() {
        StringKeyValueEntity entity = createEntity(null);
        try {
            dao.insert(entity);
            Assert.fail("Insert without pre-set PK succeeded");
        } catch (Exception e) {
            // Expected
        }
    }

}
