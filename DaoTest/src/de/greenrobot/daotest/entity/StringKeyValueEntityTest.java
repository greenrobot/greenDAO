package de.greenrobot.daotest.entity;

import de.greenrobot.dao.test.AbstractDaoTestSinglePk;
import de.greenrobot.daotest.StringKeyValueEntity;
import de.greenrobot.daotest.StringKeyValueEntityDao;

public class StringKeyValueEntityTest extends AbstractDaoTestSinglePk<StringKeyValueEntityDao, StringKeyValueEntity, String> {

    public StringKeyValueEntityTest() {
        super(StringKeyValueEntityDao.class);
    }

    @Override
    protected StringKeyValueEntity createEntity(String key) {
        StringKeyValueEntity entity = new StringKeyValueEntity();
        entity.setKey(key);
        return entity;
    }

    @Override
    protected String createRandomPk() {
        int len = 1 + random.nextInt(30);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char c = (char) ('a' + random.nextInt('z' - 'a'));
            builder.append(c);
        }
        return builder.toString();
    }

}
