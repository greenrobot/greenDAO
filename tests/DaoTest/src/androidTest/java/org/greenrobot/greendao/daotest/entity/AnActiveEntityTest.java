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

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.test.AbstractDaoSessionTest;
import org.greenrobot.greendao.daotest.AnActiveEntity;
import org.greenrobot.greendao.daotest.AnActiveEntityDao;
import org.greenrobot.greendao.daotest.DaoMaster;
import org.greenrobot.greendao.daotest.DaoSession;

public class AnActiveEntityTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    private AnActiveEntityDao dao;

    public AnActiveEntityTest() {
        super(DaoMaster.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dao = daoSession.getAnActiveEntityDao();
    }

    public void testThrowWhenDetached() {
        AnActiveEntity entity = new AnActiveEntity();
        try {
            entity.delete();
            fail("Should fail for detached entity");
        } catch (DaoException e) {
            // OK, expected
        }
        try {
            entity.refresh();
            fail("Should fail for detached entity");
        } catch (DaoException e) {
            // OK, expected
        }
        try {
            entity.update();
            fail("Should fail for detached entity");
        } catch (DaoException e) {
            // OK, expected
        }
    }

    public void testActiveUpdate() {
        AnActiveEntity entity = new AnActiveEntity(1l);
        long rowId = dao.insert(entity);

        entity.setText("NEW");
        entity.update();

        daoSession.clear();
        AnActiveEntity entity2 = dao.load(rowId);
        assertNotSame(entity, entity2);
        assertEquals("NEW", entity2.getText());
    }

    public void testActiveRefresh() {
        AnActiveEntity entity = new AnActiveEntity(1l);
        dao.insert(entity);

        AnActiveEntity entity2 = new AnActiveEntity(1l);
        entity2.setText("NEW");
        dao.update(entity2);

        entity.refresh();
        assertEquals("NEW", entity.getText());
    }

    public void testActiveDelete() {
        AnActiveEntity entity = new AnActiveEntity(1l);
        dao.insert(entity);

        entity.delete();
        assertNull(dao.load(1l));
    }

}
