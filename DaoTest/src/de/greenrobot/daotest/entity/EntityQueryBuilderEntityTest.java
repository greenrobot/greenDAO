package de.greenrobot.daotest.entity;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.EntityQueryBuilderEntity;
import de.greenrobot.daotest.EntityQueryBuilderEntityDao;

public class EntityQueryBuilderEntityTest extends AbstractDaoTestLongPk<EntityQueryBuilderEntityDao, EntityQueryBuilderEntity> {

  public EntityQueryBuilderEntityTest() {
    super(EntityQueryBuilderEntityDao.class);
  }

  @Override
  protected EntityQueryBuilderEntity createEntity(Long key) {
    EntityQueryBuilderEntity entity = new EntityQueryBuilderEntity();
    entity.setId(key);
    entity.setText("Entity " + key);
    entity.setIntprop2(2);
    entity.setIntprop3(3);
    return entity;
  }

  public void testFindAll() {
    this.dao.deleteAll();
    List<EntityQueryBuilderEntity> list = new ArrayList<EntityQueryBuilderEntity>();
    for (int i = 0; i < 15; i++) {
      EntityQueryBuilderEntity entity = this.createEntity(this.nextPk());
      list.add(entity);
    }
    this.dao.insertInTx(list);
    List<EntityQueryBuilderEntity> loaded = this.dao.queryBuilder().findAll().list();
    assertEquals(list.size(), loaded.size());
  }

  public void testFindByPk() {
    this.dao.deleteAll();
    Long nextPk = this.nextPk();
    this.dao.insert(this.createEntity(nextPk));
    List<EntityQueryBuilderEntity> loaded = this.dao.queryBuilder().findByPrimaryKey(nextPk).list();
    assertEquals(1, loaded.size());
    assertEquals(nextPk, loaded.get(0).getId());
  }

  public void testFindByExamplePk() {
    this.dao.deleteAll();
    Long nextPk = this.nextPk();
    this.dao.insert(this.createEntity(nextPk));

    List<EntityQueryBuilderEntity> loaded = this.dao.queryBuilder().findByExample(new EntityQueryBuilderEntity(nextPk)).list();
    assertEquals(1, loaded.size());
    assertEquals(nextPk, loaded.get(0).getId());
  }

  public void testFindByExampleStringMultiResult() {
    this.dao.deleteAll();
    List<EntityQueryBuilderEntity> list = new ArrayList<EntityQueryBuilderEntity>();
    for (int i = 0; i < 15; i++) {
      EntityQueryBuilderEntity entity = this.createEntity(this.nextPk());
      entity.setText2("text");
      list.add(entity);
    }
    this.dao.insertInTx(list);

    EntityQueryBuilderEntity example = new EntityQueryBuilderEntity();
    example.setText2("text");
    example.setIntprop2(3);
    example.setIntprop3(3);
    List<EntityQueryBuilderEntity> loaded = this.dao.queryBuilder().findByExample(example).list();
    assertEquals(list.size(), loaded.size());
  }

  public void testFindByExampleStringSingleResult() {
    this.dao.deleteAll();
    List<EntityQueryBuilderEntity> list = new ArrayList<EntityQueryBuilderEntity>();
    Long lastPk = null;
    for (int i = 0; i < 15; i++) {
      EntityQueryBuilderEntity entity = this.createEntity(this.nextPk());
      entity.setText2("text");
      lastPk = entity.getId();
      list.add(entity);
    }
    this.dao.insertInTx(list);

    EntityQueryBuilderEntity example = new EntityQueryBuilderEntity(lastPk);
    example.setText2("text");
    List<EntityQueryBuilderEntity> loaded = this.dao.queryBuilder().findByExample(example).list();
    assertEquals(1, loaded.size());
    assertEquals(lastPk, loaded.get(0).getId());
  }

}
