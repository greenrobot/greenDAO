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

import junit.framework.Assert;
import org.greenrobot.greendao.test.AbstractDaoTestStringPk;
import org.greenrobot.greendao.daotest.StringKeyValueEntity;
import org.greenrobot.greendao.daotest.StringKeyValueEntityDao;

public class StringKeyValueEntityTest extends AbstractDaoTestStringPk<StringKeyValueEntityDao, StringKeyValueEntity> {

    public StringKeyValueEntityTest() {
        super(StringKeyValueEntityDao.class);
    }

    @Override
    protected StringKeyValueEntity createEntity(String key) {
        if(key == null) {
            return null;
        }
        StringKeyValueEntity entity = new StringKeyValueEntity();
        entity.setKey(key);
        return entity;
    }

    public void testInsertWithoutPK() {
        StringKeyValueEntity entity = createEntity(null);
        try {
            dao.insert(entity);
            Assert.fail("Insert without pre-set PK succeeded");
        } catch (Exception e) {
            // Expected
        }
    }

}
