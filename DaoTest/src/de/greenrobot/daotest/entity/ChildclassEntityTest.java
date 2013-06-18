package de.greenrobot.daotest.entity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.ChildclassEntity;
import de.greenrobot.daotest.ChildclassEntityDao;
import de.greenrobot.daotest.TestChildclass;

public class ChildclassEntityTest extends AbstractDaoTestLongPk<ChildclassEntityDao, TestChildclass> {

  public ChildclassEntityTest() {
    super(ChildclassEntityDao.class);
  }

  @Override
  protected TestChildclass createEntity(Long key) {
    TestChildclass entity = new TestChildclass();
    entity.setId(key);
    return entity;
  }

  public void testInheritance() {
    TestChildclass entity = this.createEntityWithRandomPk();
    assertTrue(entity instanceof TestChildclass);
    assertTrue(entity instanceof ChildclassEntity);
  }

}
