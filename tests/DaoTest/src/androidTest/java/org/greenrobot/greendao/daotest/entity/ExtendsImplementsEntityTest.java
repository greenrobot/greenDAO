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

import java.io.Serializable;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;
import org.greenrobot.greendao.daotest.ExtendsImplementsEntity;
import org.greenrobot.greendao.daotest.ExtendsImplementsEntityDao;
import org.greenrobot.greendao.daotest.TestInterface;
import org.greenrobot.greendao.daotest.TestSuperclass;

public class ExtendsImplementsEntityTest extends
        AbstractDaoTestLongPk<ExtendsImplementsEntityDao, ExtendsImplementsEntity> {

    public ExtendsImplementsEntityTest() {
        super(ExtendsImplementsEntityDao.class);
    }

    @Override
    protected ExtendsImplementsEntity createEntity(Long key) {
        ExtendsImplementsEntity entity = new ExtendsImplementsEntity();
        entity.setId(key);
        return entity;
    }

    public void testInheritance() {
        ExtendsImplementsEntity entity = createEntityWithRandomPk();
        assertTrue(entity instanceof TestSuperclass);
        assertTrue(entity instanceof TestInterface);
        assertTrue(entity instanceof Serializable);
    }

}
