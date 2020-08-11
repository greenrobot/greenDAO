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

import org.greenrobot.greendao.daotest.CustomPkTypeEntity;
import org.greenrobot.greendao.daotest.CustomPkTypeEntityDao;
import org.greenrobot.greendao.test.AbstractDaoTestSinglePk;

import java.util.List;
import java.util.UUID;

public class CustomPkTypeEntityTest extends AbstractDaoTestSinglePk<CustomPkTypeEntityDao, CustomPkTypeEntity, UUID> {

    public CustomPkTypeEntityTest() {
        super(CustomPkTypeEntityDao.class);
    }

    @Override
    protected CustomPkTypeEntity createEntity(UUID key) {
        CustomPkTypeEntity entity = new CustomPkTypeEntity();
        entity.setId(key);
        return entity;
    }

    @Override
    protected UUID createRandomPk() {
        return UUID.randomUUID();
    }

    public void testCustomPkTypeValue() {
        CustomPkTypeEntity entity = createEntityWithRandomPk();
        UUID id = entity.getId();
        dao.insert(entity);

        List<CustomPkTypeEntity> all = dao.loadAll();
        assertEquals(1, all.size());
        assertEquals(id, all.get(0).getId());
    }

    public void testCustomPkTypeValueNull() {
        CustomPkTypeEntity entity = createEntityWithRandomPk();
        entity.setId(null);
        dao.insert(entity);

        List<CustomPkTypeEntity> all = dao.loadAll();
        assertEquals(1, all.size());
        assertNull(all.get(0).getId());
    }
}
