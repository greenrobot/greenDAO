package de.greenrobot.daotest.entity;

import de.greenrobot.dao.test.AbstractDaoTestStringPk;
import de.greenrobot.daotest.StringKeyValueEntity;
import de.greenrobot.daotest.StringKeyValueEntityDao;

public class StringKeyValueEntityTest extends AbstractDaoTestStringPk<StringKeyValueEntityDao, StringKeyValueEntity> {

    public StringKeyValueEntityTest() {
        super(StringKeyValueEntityDao.class);
    }

    @Override
    protected StringKeyValueEntity createEntity(String key) {
        StringKeyValueEntity entity = new StringKeyValueEntity();
        entity.setKey(key);
        return entity;
    }

}
