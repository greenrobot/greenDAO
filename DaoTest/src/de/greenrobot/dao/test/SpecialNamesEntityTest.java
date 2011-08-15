package de.greenrobot.dao.test;


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
