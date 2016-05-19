package org.greenrobot.greendao.daotest.entity;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import org.greenrobot.greendao.daotest.IndexedStringEntity;
import org.greenrobot.greendao.daotest.IndexedStringEntityDao;

public class IndexedStringEntityTest extends AbstractDaoTestLongPk<IndexedStringEntityDao, IndexedStringEntity> {

    public IndexedStringEntityTest() {
        super(IndexedStringEntityDao.class);
    }

    @Override
    protected IndexedStringEntity createEntity(Long key) {
        IndexedStringEntity entity = new IndexedStringEntity();
        entity.setId(key);
        return entity;
    }

}
