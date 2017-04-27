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

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import org.greenrobot.greendao.daotest.JoinManyToDateEntity;
import org.greenrobot.greendao.daotest.JoinManyToDateEntityDao;

public class JoinManyToDateEntityTest extends AbstractDaoTestLongPk<JoinManyToDateEntityDao, JoinManyToDateEntity> {

    public JoinManyToDateEntityTest() {
        super(JoinManyToDateEntityDao.class);
    }

    @Override
    protected JoinManyToDateEntity createEntity(Long key) {
        JoinManyToDateEntity entity = new JoinManyToDateEntity();
        entity.setId(key);
        return entity;
    }

}
