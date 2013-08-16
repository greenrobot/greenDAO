package de.greenrobot.daotest.entity;

import java.util.List;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.SaveEntity;
import de.greenrobot.daotest.SaveEntityDao;

public class SaveEntityTest extends AbstractDaoTestLongPk<SaveEntityDao, SaveEntity> {

  public SaveEntityTest() {
    super(SaveEntityDao.class);
  }

  @Override
  protected SaveEntity createEntity(Long key) {
    SaveEntity entity = new SaveEntity(key, "save");
    return entity;
  }

  public void testSaveInsert() {
    SaveEntity entity = this.createEntity(null);
    SaveEntity actual = this.dao.save(entity);

    assertNotNull(actual);
    assertNotNull(actual.getId());
    assertEquals("save", actual.getText());

    this.assertEntity(actual.getId(), "save");

  }

  protected void assertEntity(Long pk, String assertString) {
    List<SaveEntity> queryresult = this.dao.queryBuilder().findByPrimaryKey(pk).list();

    assertNotNull(queryresult);
    assertEquals(1, queryresult.size());
    assertEquals(assertString, queryresult.get(0).getText());
  }

  public void testSaveUpdateWithoutCheck() {
    Long nextPk = this.nextPk();
    SaveEntity entity = this.createEntity(nextPk);
    SaveEntity actual = this.dao.save(entity);

    assertNotNull(actual);
    assertNotNull(actual.getId());
    assertEquals("save", actual.getText());
    this.assertEntity(nextPk, "save");

    entity.setText("new text");

    actual = this.dao.save(entity);

    assertNotNull(actual);
    assertEquals(nextPk, actual.getId());
    assertEquals("new text", actual.getText());
    this.assertEntity(nextPk, "new text");

    entity.setText("another new text");

    actual = this.dao.save(entity, false);

    assertNotNull(actual);
    assertEquals(nextPk, actual.getId());
    assertEquals("another new text", actual.getText());
    this.assertEntity(nextPk, "another new text");
  }

  public void testSaveUpdateWithCheck() {
    Long nextPk = this.nextPk();
    SaveEntity entity = this.createEntity(nextPk);
    SaveEntity actual = this.dao.save(entity, true);

    assertNotNull(actual);
    assertNotNull(actual.getId());
    assertEquals("save", actual.getText());
    this.assertEntity(nextPk, "save");

    entity.setText("new text");

    actual = this.dao.save(entity, true);

    assertNotNull(actual);
    assertEquals(nextPk, actual.getId());
    assertEquals("new text", actual.getText());
    this.assertEntity(nextPk, "new text");
  }
}
