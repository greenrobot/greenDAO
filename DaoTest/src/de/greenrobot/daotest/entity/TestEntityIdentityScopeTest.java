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

import de.greenrobot.dao.identityscope.IdentityScopeLong;
import de.greenrobot.daotest.TestEntity;

public class TestEntityIdentityScopeTest extends TestEntityTest {
    @Override
    protected void setUp() throws Exception {
        setIdentityScopeBeforeSetUp(new IdentityScopeLong<TestEntity>());
        super.setUp();
    }

    public void testLoadIdScope() {
        TestEntity entity = createEntity(null);
        dao.insert(entity);
        TestEntity entity2 = dao.load(entity.getId());
        TestEntity entity3 = dao.load(entity.getId());

        assertSame(entity, entity2);
        assertSame(entity2, entity3);
    }

    public void testDetach() {
        TestEntity entity = createEntity(null);
        dao.insert(entity);
        dao.detach(entity);
        TestEntity entity2 = dao.load(entity.getId());
        dao.detach(entity2);
        TestEntity entity3 = dao.load(entity.getId());

        assertNotSame(entity, entity2);
        assertNotSame(entity2, entity3);
        assertNotSame(entity, entity3);
    }

    public void testDetachOther() {
        TestEntity entity = createEntity(null);
        dao.insert(entity);
        dao.detach(entity);
        TestEntity entity2 = dao.load(entity.getId());
        dao.detach(entity);
        TestEntity entity3 = dao.load(entity.getId());

        assertSame(entity2, entity3);
    }

    public void testLoadAllScope() {
        TestEntity entity = createEntity(null);
        dao.insert(entity);
        TestEntity entity2 = dao.loadAll().get(0);
        TestEntity entity3 = dao.loadAll().get(0);

        assertSame(entity, entity2);
        assertSame(entity2, entity3);
    }

}
