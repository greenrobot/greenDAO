package de.greenrobot.daotest.entity;

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
import java.util.Arrays;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.SimpleEntity;
import de.greenrobot.daotest.SimpleEntityDao;

public class SimpleEntityTest extends AbstractDaoTestLongPk<SimpleEntityDao, SimpleEntity> {

    public SimpleEntityTest() {
        super(SimpleEntityDao.class);
    }

    @Override
    protected SimpleEntity createEntity(Long key) {
        SimpleEntity entity = new SimpleEntity();
        entity.setId(key);
        return entity;
    }

    public void testValuesNull() {
        SimpleEntity entity = createEntity(1l);
        dao.insert(entity);
        SimpleEntity reloaded = dao.load(1l);
        assertNotSame(entity, reloaded);

        assertEquals(entity.getId(), reloaded.getId());
        assertValuesNull(reloaded);
    }

    public void testValues() {
        SimpleEntity entity = createEntity(1l);
        setValues(entity);
        dao.insert(entity);
        SimpleEntity reloaded = dao.load(1l);
        assertNotSame(entity, reloaded);
        assertValues(reloaded);
    }

    public void testUpdateValues() {
        SimpleEntity entity = createEntity(1l);
        dao.insert(entity);
        entity = dao.load(1l);
        setValues(entity);
        dao.update(entity);
        SimpleEntity reloaded = dao.load(1l);
        assertNotSame(entity, reloaded);
        assertValues(reloaded);
    }

    public void testUpdateValuesToNull() {
        SimpleEntity entity = createEntity(1l);
        setValues(entity);
        dao.insert(entity);
        entity = dao.load(1l);
        assertValues(entity);
        setValuesToNull(entity);
        dao.update(entity);
        SimpleEntity reloaded = dao.load(1l);
        assertNotSame(entity, reloaded);
        assertValuesNull(reloaded);
    }

    protected void assertValues(SimpleEntity reloaded) {
        assertEquals(1l, (long) reloaded.getId());
        assertEquals(true, (boolean) reloaded.getSimpleBoolean());
        assertEquals(Double.MAX_VALUE, reloaded.getSimpleDouble());
        assertEquals(Float.MAX_VALUE, reloaded.getSimpleFloat());
        assertEquals(Long.MAX_VALUE, (long) reloaded.getSimpleLong());
        assertEquals(Byte.MAX_VALUE, (byte) reloaded.getSimpleByte());
        assertEquals(Integer.MAX_VALUE, (int) reloaded.getSimpleInt());
        assertEquals(Short.MAX_VALUE, (short) reloaded.getSimpleShort());
        assertEquals("greenrobot greenDAO", reloaded.getSimpleString());
        byte[] bytes = { 42, -17, 23, 0, 127, -128 };
        assertTrue(Arrays.equals(bytes, reloaded.getSimpleByteArray()));
    }

    protected void setValues(SimpleEntity entity) {
        entity.setSimpleBoolean(true);
        entity.setSimpleByte(Byte.MAX_VALUE);
        entity.setSimpleShort(Short.MAX_VALUE);
        entity.setSimpleInt(Integer.MAX_VALUE);
        entity.setSimpleLong(Long.MAX_VALUE);
        entity.setSimpleFloat(Float.MAX_VALUE);
        entity.setSimpleDouble(Double.MAX_VALUE);
        entity.setSimpleString("greenrobot greenDAO");
        byte[] bytes = { 42, -17, 23, 0, 127, -128 };
        entity.setSimpleByteArray(bytes);
    }

    protected void setValuesToNull(SimpleEntity entity) {
        entity.setSimpleBoolean(null);
        entity.setSimpleByte(null);
        entity.setSimpleShort(null);
        entity.setSimpleInt(null);
        entity.setSimpleLong(null);
        entity.setSimpleFloat(null);
        entity.setSimpleDouble(null);
        entity.setSimpleString(null);
        entity.setSimpleByteArray(null);
    }

    protected void assertValuesNull(SimpleEntity reloaded) {
        assertNull(reloaded.getSimpleBoolean());
        assertNull(reloaded.getSimpleDouble());
        assertNull(reloaded.getSimpleFloat());
        assertNull(reloaded.getSimpleLong());
        assertNull(reloaded.getSimpleByte());
        assertNull(reloaded.getSimpleInt());
        assertNull(reloaded.getSimpleShort());
        assertNull(reloaded.getSimpleBoolean());
        assertNull(reloaded.getSimpleString());
        assertNull(reloaded.getSimpleByteArray());
    }

}
