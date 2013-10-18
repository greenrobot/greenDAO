/*
 * Copyright (C) 2012 Markus Junginger, greenrobot (http://greenrobot.de)
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

import de.greenrobot.dao.identityscope.IdentityScopeObject;
import de.greenrobot.daotest.StringKeyValueEntity;

public class StringKeyValueEntityIdentityScopeTest extends StringKeyValueEntityTest {
    @Override
    protected void setUp() {
        setIdentityScopeBeforeSetUp(new IdentityScopeObject<String, StringKeyValueEntity>());
        super.setUp();
    }

    public void testLoadIdScope() {
        StringKeyValueEntity entity = createEntityWithRandomPk();
        dao.insert(entity);
        StringKeyValueEntity entity2 = dao.load(entity.getKey());
        StringKeyValueEntity entity3 = dao.load(entity.getKey());

        assertSame(entity, entity2);
        assertSame(entity2, entity3);
    }

    public void testLoadIdScope_load() {
        StringKeyValueEntity entity = createEntityWithRandomPk();
        dao.insert(entity);
        dao.detach(entity);
        StringKeyValueEntity entity2 = dao.load(entity.getKey());
        StringKeyValueEntity entity3 = dao.load(entity.getKey());

        assertNotSame(entity, entity2);
        assertSame(entity2, entity3);
    }

    public void testDetach() {
        StringKeyValueEntity entity = createEntityWithRandomPk();
        dao.insert(entity);
        dao.detach(entity);
        StringKeyValueEntity entity2 = dao.load(entity.getKey());
        dao.detach(entity2);
        StringKeyValueEntity entity3 = dao.load(entity.getKey());

        assertNotSame(entity, entity2);
        assertNotSame(entity2, entity3);
        assertNotSame(entity, entity3);
    }

    public void testDetachOther() {
        StringKeyValueEntity entity = createEntityWithRandomPk();
        dao.insert(entity);
        dao.detach(entity);
        StringKeyValueEntity entity2 = dao.load(entity.getKey());
        dao.detach(entity);
        StringKeyValueEntity entity3 = dao.load(entity.getKey());

        assertSame(entity2, entity3);
    }

    public void testLoadAllScope() {
        StringKeyValueEntity entity = createEntityWithRandomPk();
        dao.insert(entity);
        StringKeyValueEntity entity2 = dao.loadAll().get(0);
        StringKeyValueEntity entity3 = dao.loadAll().get(0);

        assertSame(entity, entity2);
        assertSame(entity2, entity3);
    }

}
