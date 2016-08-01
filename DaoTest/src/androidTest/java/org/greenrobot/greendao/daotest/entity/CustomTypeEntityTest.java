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

import org.greenrobot.greendao.daotest.CustomTypeEntity;
import org.greenrobot.greendao.daotest.CustomTypeEntityDao;
import org.greenrobot.greendao.daotest.customtype.MyTimestamp;
import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import java.util.List;

public class CustomTypeEntityTest extends AbstractDaoTestLongPk<CustomTypeEntityDao, CustomTypeEntity> {

    public CustomTypeEntityTest() {
        super(CustomTypeEntityDao.class);
    }

    @Override
    protected CustomTypeEntity createEntity(Long key) {
        CustomTypeEntity entity = new CustomTypeEntity();
        entity.setId(key);
        MyTimestamp myCustomTimestamp = new MyTimestamp();
        myCustomTimestamp.timestamp = System.currentTimeMillis();
        entity.setMyCustomTimestamp(myCustomTimestamp);
        return entity;
    }

    public void testCustomTypeValue() {
        CustomTypeEntity entity = createEntityWithRandomPk();
        long timestamp = entity.getMyCustomTimestamp().timestamp;
        dao.insert(entity);

        List<CustomTypeEntity> all = dao.loadAll();
        assertEquals(1, all.size());
        assertEquals(timestamp, all.get(0).getMyCustomTimestamp().timestamp);
    }

    public void testCustomTypeValueNull() {
        CustomTypeEntity entity = createEntityWithRandomPk();
        entity.setMyCustomTimestamp(null);
        dao.insert(entity);

        List<CustomTypeEntity> all = dao.loadAll();
        assertEquals(1, all.size());
        assertNull(all.get(0).getMyCustomTimestamp());
    }

}
