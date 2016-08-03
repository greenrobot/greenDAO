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

import org.greenrobot.greendao.test.AbstractDaoSessionTest;
import org.greenrobot.greendao.daotest.AutoincrementEntity;
import org.greenrobot.greendao.daotest.DaoMaster;
import org.greenrobot.greendao.daotest.DaoSession;
import org.greenrobot.greendao.daotest.SimpleEntity;

public class AutoincrementEntityTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    public AutoincrementEntityTest() {
        super(DaoMaster.class);
    }

    public void testAutoincrement() {
        AutoincrementEntity entity = new AutoincrementEntity();
        daoSession.insert(entity);
        Long id1 = entity.getId();
        assertNotNull(id1);
        daoSession.delete(entity);

        AutoincrementEntity entity2 = new AutoincrementEntity();
        daoSession.insert(entity2);
        assertEquals(id1 + 1, (long) entity2.getId());
    }

    public void testNoAutoincrement() {
        SimpleEntity entity = new SimpleEntity();
        daoSession.insert(entity);
        Long id1 = entity.getId();
        assertNotNull(id1);
        daoSession.delete(entity);

        SimpleEntity entity2 = new SimpleEntity();
        daoSession.insert(entity2);
        assertEquals(id1, entity2.getId());
    }

}
