package de.greenrobot.daotest.entity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.dao.test.SpecialNamesEntity;
import de.greenrobot.dao.test.SpecialNamesEntityDao;


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
