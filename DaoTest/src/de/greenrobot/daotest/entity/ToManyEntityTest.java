/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * This file is part of greenDAO Generator.
 * 
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    protected void setUp() throws Exception {
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
        List<ToManyTargetEntity> resolvedToMany = testEntity.getToManyTargetEntityList();
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
        return insertTargetEntitites(id, count, null);
    }

    private ToManyTargetEntity[] insertTargetEntitites(Long toManyId, int count, String joinProperty) {
        ToManyTargetEntity[] targetEntities = new ToManyTargetEntity[count];
        for (int i = 0; i < count; i++) {
            ToManyTargetEntity target = new ToManyTargetEntity();
            target.setToManyId(toManyId);
            target.setToManyIdDesc(toManyId);
            target.setTargetJoinProperty(joinProperty);
            targetEntities[i] = target;
        }
        toManyTargetEntityDao.insertInTx(targetEntities);
        return targetEntities;
    }

    public void testGetToManyTwice() {
        prepareToMany(1, 3);

        ToManyEntity testEntity = toManyEntityDao.load(1l);
        List<ToManyTargetEntity> resolvedToMany1 = testEntity.getToManyTargetEntityList();
        List<ToManyTargetEntity> resolvedToMany2 = testEntity.getToManyTargetEntityList();
        assertSame(resolvedToMany1, resolvedToMany2);
    }

    public void testResetToMany() {
        ToManyTargetEntity[] targetEntities = prepareToMany(1, 3);

        ToManyEntity testEntity = toManyEntityDao.load(1l);
        List<ToManyTargetEntity> resolvedToMany1 = testEntity.getToManyTargetEntityList();
        testEntity.resetToManyTargetEntityList();
        List<ToManyTargetEntity> resolvedToMany2 = testEntity.getToManyTargetEntityList();
        assertNotSame(resolvedToMany1, resolvedToMany2);
        assertSameEntities(targetEntities, resolvedToMany2);
    }

    public void testChangeResetToMany() {
        prepareToMany(1, 3);

        ToManyEntity testEntity = toManyEntityDao.load(1l);
        List<ToManyTargetEntity> resolvedToMany1 = testEntity.getToManyTargetEntityList();
        testEntity.resetToManyTargetEntityList();

        ToManyTargetEntity newEntity = new ToManyTargetEntity();
        newEntity.setToManyId(1l);
        daoSession.insert(newEntity);

        List<ToManyTargetEntity> resolvedToMany2 = testEntity.getToManyTargetEntityList();
        assertNotSame(resolvedToMany1, resolvedToMany2);
        assertEquals(resolvedToMany1.size() + 1, resolvedToMany2.size());

        testEntity.resetToManyTargetEntityList();
        toManyTargetEntityDao.deleteAll();
        List<ToManyTargetEntity> resolvedToMany3 = testEntity.getToManyTargetEntityList();
        assertEquals(0, resolvedToMany3.size());
    }

    public void testToManyOrder() {
        prepareToMany(1, 3);

        ToManyEntity testEntity = toManyEntityDao.load(1l);
        List<ToManyTargetEntity> resolvedToManyAsc = testEntity.getToManyTargetEntityList();
        List<ToManyTargetEntity> resolvedToManyDesc = testEntity.getToManyDescList();
        assertNotSame(resolvedToManyAsc, resolvedToManyDesc);
        assertEquals(resolvedToManyAsc.get(0).getId(), resolvedToManyDesc.get(2).getId());
        assertSame(resolvedToManyAsc.get(0), resolvedToManyDesc.get(2));
        assertSame(resolvedToManyAsc.get(1), resolvedToManyDesc.get(1));
        assertSame(resolvedToManyAsc.get(2), resolvedToManyDesc.get(0));
    }

    public void testJoinProperty() {
        ToManyEntity entity = new ToManyEntity(1l);
        entity.setSourceJoinProperty("JOIN ME");
        daoSession.insert(entity);
        insertTargetEntitites(null, 3, "JOIN ME");

        ToManyEntity testEntity = toManyEntityDao.load(1l);
        List<ToManyTargetEntity> targetEntities = testEntity.getToManyByJoinProperty();
        assertEquals(3, targetEntities.size());

        ToManyTargetEntity middleEntity = targetEntities.get(1);
        middleEntity.setTargetJoinProperty("DON'T JOIN ME");
        toManyTargetEntityDao.update(middleEntity);

        testEntity.resetToManyByJoinProperty();
        targetEntities = testEntity.getToManyByJoinProperty();
        assertEquals(2, targetEntities.size());

        assertFalse(middleEntity.getId() == targetEntities.get(0).getId());
        assertFalse(middleEntity.getId() == targetEntities.get(1).getId());
    }

    public void testTwoJoinProperty() {
        ToManyEntity entity = new ToManyEntity(1l);
        entity.setSourceJoinProperty("JOIN ME");
        daoSession.insert(entity);
        insertTargetEntitites(1l, 3, "JOIN ME");

        ToManyEntity testEntity = toManyEntityDao.load(1l);
        List<ToManyTargetEntity> targetEntities = testEntity.getToManyJoinTwo();
        assertEquals(3, targetEntities.size());

        ToManyTargetEntity middleEntity = targetEntities.get(1);
        middleEntity.setTargetJoinProperty("DON'T JOIN ME");
        toManyTargetEntityDao.update(middleEntity);

        testEntity.resetToManyJoinTwo();
        targetEntities = testEntity.getToManyJoinTwo();
        assertEquals(2, targetEntities.size());

        assertFalse(middleEntity.getId() == targetEntities.get(0).getId());
        assertFalse(middleEntity.getId() == targetEntities.get(1).getId());
    }

}
