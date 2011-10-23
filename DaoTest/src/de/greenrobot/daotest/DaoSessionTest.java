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
package de.greenrobot.daotest;

import de.greenrobot.dao.test.AbstractDaoSessionTest;

public class DaoSessionTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    public DaoSessionTest() {
        super(DaoMaster.class);
    }

    public void testInsertAndLoad() {
        SimpleEntity entity = new SimpleEntity();
        daoSession.insert(entity);
        Long id = entity.getId();
        assertNotNull(id);
        SimpleEntity entity2 = daoSession.load(SimpleEntity.class, id);
        assertNotNull(entity2);
    }

    public void testIdentity() {
        SimpleEntity entity = new SimpleEntity();
        daoSession.insert(entity);
        SimpleEntity entity2 = daoSession.load(SimpleEntity.class, entity.getId());
        SimpleEntity entity3 = daoSession.load(SimpleEntity.class, entity.getId());
        assertSame(entity, entity2);
        assertSame(entity, entity3);
    }

    public void testIdentityPerSession() {
        SimpleEntity entity = new SimpleEntity();
        daoSession.insert(entity);
        DaoSession session2 = daoMaster.newSession();
        SimpleEntity entity2 = session2.load(SimpleEntity.class, entity.getId());
        assertNotSame(entity, entity2);
    }

    public void testSessionReset() {
        SimpleEntity entity = new SimpleEntity();
        daoSession.insert(entity);
        daoSession.clear();
        SimpleEntity entity2 = daoSession.load(SimpleEntity.class, entity.getId());
        assertNotSame(entity, entity2);
    }
}