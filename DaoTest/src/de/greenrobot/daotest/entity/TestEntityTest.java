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

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.TestEntity;
import de.greenrobot.daotest.TestEntityDao;

public class TestEntityTest extends AbstractDaoTestLongPk<TestEntityDao, TestEntity> {

    public TestEntityTest() {
        super(TestEntityDao.class);
    }

    @Override
    protected TestEntity createEntity(Long key) {
        TestEntity entity = new TestEntity();
        entity.setId(key);
        entity.setSimpleStringNotNull("green");
        return entity;
    }

    public void testRefresh() {
        TestEntity entity = createEntity(1l);
        entity.setSimpleInteger(42);
        entity.setSimpleString(null);
        dao.insert(entity);
        entity.setSimpleInteger(null);
        entity.setSimpleString("temp");
        dao.refresh(entity);
        assertEquals(42, (int) entity.getSimpleInteger());
        assertNull(entity.getSimpleString());
    }

    public void testRefreshIllegal() {
        TestEntity entity = createEntity(1l);
        try {
            dao.refresh(entity);
            fail("Exception expected");
        } catch (DaoException expected) {
        }
        dao.insert(entity);
        dao.delete(entity);
        try {
            dao.refresh(entity);
            fail("Exception expected");
        } catch (DaoException expected) {
        }
    }

}
