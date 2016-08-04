/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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

package org.greenrobot.greendao.daotest.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenrobot.greendao.test.AbstractDaoSessionTest;
import org.greenrobot.greendao.daotest.DaoMaster;
import org.greenrobot.greendao.daotest.DaoSession;
import org.greenrobot.greendao.daotest.DateEntity;
import org.greenrobot.greendao.daotest.DateEntityDao;
import org.greenrobot.greendao.daotest.JoinManyToDateEntity;
import org.greenrobot.greendao.daotest.ToManyEntity;
import org.greenrobot.greendao.daotest.ToManyEntityDao;
import org.greenrobot.greendao.daotest.ToManyTargetEntity;
import org.greenrobot.greendao.daotest.ToManyTargetEntityDao;

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

    public void testGetToManyTwice() {
        prepareToMany(1, 3);

        ToManyEntity testEntity = toManyEntityDao.load(1L);
        List<ToManyTargetEntity> resolvedToMany1 = testEntity.getToManyTargetEntityList();
        List<ToManyTargetEntity> resolvedToMany2 = testEntity.getToManyTargetEntityList();
        assertSame(resolvedToMany1, resolvedToMany2);
    }

    public void testResetToMany() {
        ToManyTargetEntity[] targetEntities = prepareToMany(1, 3);

        ToManyEntity testEntity = toManyEntityDao.load(1L);
        List<ToManyTargetEntity> resolvedToMany1 = testEntity.getToManyTargetEntityList();
        testEntity.resetToManyTargetEntityList();
        List<ToManyTargetEntity> resolvedToMany2 = testEntity.getToManyTargetEntityList();
        assertNotSame(resolvedToMany1, resolvedToMany2);
        assertSameEntities(targetEntities, resolvedToMany2);
    }

    public void testChangeResetToMany() {
        prepareToMany(1, 3);

        ToManyEntity testEntity = toManyEntityDao.load(1L);
        List<ToManyTargetEntity> resolvedToMany1 = testEntity.getToManyTargetEntityList();
        testEntity.resetToManyTargetEntityList();

        ToManyTargetEntity newEntity = new ToManyTargetEntity();
        newEntity.setToManyId(1L);
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

        ToManyEntity testEntity = toManyEntityDao.load(1L);
        List<ToManyTargetEntity> resolvedToManyAsc = testEntity.getToManyTargetEntityList();
        List<ToManyTargetEntity> resolvedToManyDesc = testEntity.getToManyDescList();
        assertNotSame(resolvedToManyAsc, resolvedToManyDesc);
        assertEquals(resolvedToManyAsc.get(0).getId(), resolvedToManyDesc.get(2).getId());
        assertSame(resolvedToManyAsc.get(0), resolvedToManyDesc.get(2));
        assertSame(resolvedToManyAsc.get(1), resolvedToManyDesc.get(1));
        assertSame(resolvedToManyAsc.get(2), resolvedToManyDesc.get(0));
    }

    public void testJoinProperty() {
        ToManyEntity entity = new ToManyEntity(1L);
        entity.setSourceJoinProperty("JOIN ME");
        daoSession.insert(entity);
        insertTargetEntities(null, 3, "JOIN ME");

        ToManyEntity testEntity = toManyEntityDao.load(1L);
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
        ToManyEntity entity = new ToManyEntity(1L);
        entity.setSourceJoinProperty("JOIN ME");
        daoSession.insert(entity);
        insertTargetEntities(1L, 3, "JOIN ME");

        ToManyEntity testEntity = toManyEntityDao.load(1L);
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

    public void testToManyWithJoin() {
        ToManyEntity entity = new ToManyEntity(1L);
        daoSession.insert(entity);
        List<DateEntity> dateEntities = entity.getDateEntityList();
        assertEquals(0, dateEntities.size());

        DateEntityDao dateDao = daoSession.getDateEntityDao();
        Date now = new Date();
        DateEntity date1 = new DateEntity(1L, null, now);
        DateEntity date2 = new DateEntity(2L, null, now);
        DateEntity date3 = new DateEntity(3L, null, now);
        DateEntity date4 = new DateEntity(4L, null, now);
        dateDao.insertInTx(date1, date2, date3, date4);

        daoSession.insert(new JoinManyToDateEntity(1L,2L,1L));
        daoSession.insert(new JoinManyToDateEntity(2L,1L,3L));
        entity.resetDateEntityList();
        dateEntities = entity.getDateEntityList();
        assertEquals(1, dateEntities.size());
        assertEquals(3L, (long) dateEntities.get(0).getId());

        daoSession.insert(new JoinManyToDateEntity(3L, 1L, 4L));
        entity.resetDateEntityList();
        dateEntities = entity.getDateEntityList();
        assertEquals(2, dateEntities.size());
    }

    private void assertSameEntities(ToManyTargetEntity[] targetEntities, List<ToManyTargetEntity> resolvedToMany) {
        int count = targetEntities.length;
        assertEquals(count, resolvedToMany.size());

        Map<Long, ToManyTargetEntity> resolvedMap = new HashMap<>();
        for (ToManyTargetEntity resolvedEntity : resolvedToMany) {
            resolvedMap.put(resolvedEntity.getId(), resolvedEntity);
        }
        for (int i = 0; i < count; i++) {
            long entityId = targetEntities[i].getId();
            assertTrue("ID=" + entityId, resolvedMap.containsKey(entityId));
            assertSame(targetEntities[i], resolvedMap.get(entityId));
        }
    }

    private ToManyTargetEntity[] prepareToMany(long id, int count) {
        ToManyEntity entity = new ToManyEntity(id);
        daoSession.insert(entity);
        return insertTargetEntities(id, count, null);
    }

    private ToManyTargetEntity[] insertTargetEntities(Long toManyId, int count, String joinProperty) {
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


}
