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

import java.util.Date;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.DateEntity;
import de.greenrobot.daotest.DateEntityDao;

public class DateEntityTest extends AbstractDaoTestLongPk<DateEntityDao, DateEntity> {

    public DateEntityTest() {
        super(DateEntityDao.class);
    }

    @Override
    protected DateEntity createEntity(Long key) {
        DateEntity entity = new DateEntity();
        entity.setId(key);
        entity.setDateNotNull(new Date());
        return entity;
    }
    
    public void testValues() {
        DateEntity entity = createEntity(1l);
        dao.insert(entity);
        
        DateEntity reloaded = dao.load(entity.getId());
        assertNull(reloaded.getDate());
        assertNotNull(reloaded.getDateNotNull());
        assertEquals(entity.getDateNotNull(), reloaded.getDateNotNull());
    }

    public void testValues2() {
        DateEntity entity = createEntity(1l);
        long t1=32479875;
        long t2=976345942443435235l;
        entity.setDate(new Date(t1));
        entity.setDateNotNull(new Date(t2));
        dao.insert(entity);
        
        DateEntity reloaded = dao.load(entity.getId());
        assertNotNull(reloaded.getDate());
        assertNotNull(reloaded.getDateNotNull());
        assertEquals(t1, reloaded.getDate().getTime());
        assertEquals(t2, reloaded.getDateNotNull().getTime());
    }

}
