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

import java.util.Arrays;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.SimpleEntityNotNull;
import de.greenrobot.daotest.SimpleEntityNotNullDao;

public class SimpleEntityNotNullTest extends AbstractDaoTestLongPk<SimpleEntityNotNullDao, SimpleEntityNotNull> {

    public SimpleEntityNotNullTest() {
        super(SimpleEntityNotNullDao.class);
    }

    @Override
    protected SimpleEntityNotNull createEntity(Long key) {
        return SimpleEntityNotNullHelper.createEntity(key);
    }

    public void testValues() {
        SimpleEntityNotNull entity = createEntity(1l);
        dao.insert(entity);
        SimpleEntityNotNull reloaded = dao.load(1l);
        assertEqualProperties(entity, reloaded);
    }

    protected static void assertEqualProperties(SimpleEntityNotNull entity, SimpleEntityNotNull reloaded) {
        assertNotSame(entity, reloaded);

        assertEquals(entity.getId(), reloaded.getId());
        assertEquals(entity.getSimpleBoolean(), reloaded.getSimpleBoolean());
        assertEquals(entity.getSimpleDouble(), reloaded.getSimpleDouble());
        assertEquals(entity.getSimpleFloat(), reloaded.getSimpleFloat());
        assertEquals(entity.getSimpleLong(), reloaded.getSimpleLong());
        assertEquals(entity.getSimpleByte(), reloaded.getSimpleByte());
        assertEquals(entity.getSimpleInt(), reloaded.getSimpleInt());
        assertEquals(entity.getSimpleShort(), reloaded.getSimpleShort());
        assertEquals(entity.getSimpleBoolean(), reloaded.getSimpleBoolean());
        assertEquals(entity.getSimpleString(), reloaded.getSimpleString());
        assertTrue(Arrays.equals(entity.getSimpleByteArray(), reloaded.getSimpleByteArray()));
    }

    public void testUpdateValues() {
        SimpleEntityNotNull entity = createEntity(1l);
        dao.insert(entity);
        entity = dao.load(1l);

        entity.setSimpleBoolean(false);
        entity.setSimpleByte(Byte.MIN_VALUE);
        entity.setSimpleShort(Short.MIN_VALUE);
        entity.setSimpleInt(Integer.MIN_VALUE);
        entity.setSimpleLong(Long.MIN_VALUE);
        entity.setSimpleFloat(Float.MIN_VALUE);
        entity.setSimpleDouble(Double.MIN_VALUE);
        entity.setSimpleString("greenDAO");
        byte[] bytes = { -1, 0, 1 };
        entity.setSimpleByteArray(bytes);
        dao.update(entity);

        SimpleEntityNotNull reloaded = dao.load(1l);
        assertEqualProperties(entity, reloaded);
    }

}
