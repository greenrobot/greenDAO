package de.greenrobot.daotest.entity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.SpecialNamesEntity;
import de.greenrobot.daotest.SpecialNamesEntityDao;


public class SpecialNamesEntityTest extends AbstractDaoTestLongPk<SpecialNamesEntityDao, SpecialNamesEntity> {

    public SpecialNamesEntityTest() {
        super(SpecialNamesEntityDao.class);
    }

    @Override
    protected SpecialNamesEntity createEntity(Long key) {
        SpecialNamesEntity entity = new SpecialNamesEntity();
        entity.setId(key);
        return entity;
    }

}
