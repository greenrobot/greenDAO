package de.greenrobot.daotest.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.test.AbstractDaoSessionTest;
import de.greenrobot.daotest.DaoMaster;
import de.greenrobot.daotest.DaoSession;
import de.greenrobot.daotest.ToManyEntity;
import de.greenrobot.daotest.ToManyEntityDao;
import de.greenrobot.daotest.ToManyTargetEntity;
import de.greenrobot.daotest.ToManyTargetEntityDao;

public class ToManyEntityTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    private ToManyEntityDao toManyEntityDao;
    private ToManyTargetEntityDao toManyTargetEntityDao;

    public ToManyEntityTest() {
        super(DaoMaster.class);
    }

    @Override
    protected void setUp() {
        super.setUp();
        toManyEntityDao = daoSession.getToManyEntityDao();
        toManyTargetEntityDao = daoSession.getToManyTargetEntityDao();
    }

    public void testToManyBasics() {
        int count = 0;
        for (int i = 0; i < 16; i++) {
            runTestToManyBasics(i + 1, i);
            count += i;
        }
        assertEquals(count, toManyTargetEntityDao.count());
    }

    public void runTestToManyBasics(long id, int count) {
        ToManyTargetEntity[] targetEntities = prepareToMany(id, count);

        ToManyEntity testEntity = toManyEntityDao.load(id);
        List<ToManyTargetEntity> resolvedToMany = testEntity.getToManyTargetEntity();
        assertSameEntities(targetEntities, resolvedToMany);
    }

    private void assertSameEntities(ToManyTargetEntity[] targetEntities, List<ToManyTargetEntity> resolvedToMany) {
        int count = targetEntities.length;
        assertEquals(count, resolvedToMany.size());

        Map<Long, ToManyTargetEntity> resolvedMap = new HashMap<Long, ToManyTargetEntity>();
        for (ToManyTargetEntity resolvedEntity : resolvedToMany) {
            resolvedMap.put(resolvedEntity.getId(), resolvedEntity);
        }
        for (int i = 0; i < count; i++) {
            long entityId = (long) targetEntities[i].getId();
            assertTrue("ID=" + entityId, resolvedMap.containsKey(entityId));
            assertSame(targetEntities[i], resolvedMap.get(entityId));
        }
    }

    private ToManyTargetEntity[] prepareToMany(long id, int count) {
        ToManyEntity entity = new ToManyEntity(id);
        daoSession.insert(entity);
        ToManyTargetEntity[] targetEntities = new ToManyTargetEntity[count];
        for (int i = 0; i < count; i++) {
            ToManyTargetEntity target = new ToManyTargetEntity();
            target.setToManyId(id);
            targetEntities[i] = target;
        }

        toManyTargetEntityDao.insertInTx(targetEntities);
        return targetEntities;
    }

    public void testGetToManyTwice() {
        prepareToMany(1, 3);

        ToManyEntity testEntity = toManyEntityDao.load(1l);
        List<ToManyTargetEntity> resolvedToMany1 = testEntity.getToManyTargetEntity();
        List<ToManyTargetEntity> resolvedToMany2 = testEntity.getToManyTargetEntity();
        assertSame(resolvedToMany1, resolvedToMany2);
    }

    public void testResetToMany() {
        ToManyTargetEntity[] targetEntities = prepareToMany(1, 3);

        ToManyEntity testEntity = toManyEntityDao.load(1l);
        List<ToManyTargetEntity> resolvedToMany1 = testEntity.getToManyTargetEntity();
        testEntity.resetToManyTargetEntity();
        List<ToManyTargetEntity> resolvedToMany2 = testEntity.getToManyTargetEntity();
        assertNotSame(resolvedToMany1, resolvedToMany2);
        assertSameEntities(targetEntities, resolvedToMany2);
    }

    public void testChangeResetToMany() {
        prepareToMany(1, 3);

        ToManyEntity testEntity = toManyEntityDao.load(1l);
        List<ToManyTargetEntity> resolvedToMany1 = testEntity.getToManyTargetEntity();
        testEntity.resetToManyTargetEntity();

        ToManyTargetEntity newEntity = new ToManyTargetEntity();
        newEntity.setToManyId(1l);
        daoSession.insert(newEntity);

        List<ToManyTargetEntity> resolvedToMany2 = testEntity.getToManyTargetEntity();
        assertNotSame(resolvedToMany1, resolvedToMany2);
        assertEquals(resolvedToMany1.size() + 1, resolvedToMany2.size());

        testEntity.resetToManyTargetEntity();
        toManyTargetEntityDao.deleteAll();
        List<ToManyTargetEntity> resolvedToMany3 = testEntity.getToManyTargetEntity();
        assertEquals(0, resolvedToMany3.size());
    }

}
