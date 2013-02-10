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

/*
 * @author Jeremie Martinez (jeremiemartinez@gmail.com)
 */

public class BuilderEntityTest extends AbstractDaoTestLongPk<SimpleEntityDao, SimpleEntity> {

	public BuilderEntityTest() {
		super(SimpleEntityDao.class);
	}

	@Override
	protected SimpleEntity createEntity(Long key) {
		return SimpleEntity.builder().id(key).build();
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
		SimpleEntity entity = setValues(createEntity(1l));
		dao.insert(entity);
		SimpleEntity reloaded = dao.load(1l);
		assertNotSame(entity, reloaded);
		assertValues(reloaded);
	}

	public void testUpdateValues() {
		SimpleEntity entity = createEntity(1l);
		dao.insert(entity);
		entity = setValues(dao.load(1l));
		dao.update(entity);
		SimpleEntity reloaded = dao.load(1l);
		assertNotSame(entity, reloaded);
		assertValues(reloaded);
	}

	public void testUpdateValuesToNull() {
		SimpleEntity entity = setValues(createEntity(1l));
		dao.insert(entity);
		entity = dao.load(1l);
		assertValues(entity);
		entity = setValuesToNull(entity);
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

	protected SimpleEntity setValues(SimpleEntity entity) {
		byte[] bytes = { 42, -17, 23, 0, 127, -128 };
		return SimpleEntity.builder(entity).simpleBoolean(true) //
				.simpleByte(Byte.MAX_VALUE) //
				.simpleShort(Short.MAX_VALUE) //
				.simpleInt(Integer.MAX_VALUE) //
				.simpleLong(Long.MAX_VALUE) //
				.simpleFloat(Float.MAX_VALUE) //
				.simpleDouble(Double.MAX_VALUE) //
				.simpleString("greenrobot greenDAO") //
				.simpleByteArray(bytes).build(); //
	}

	protected SimpleEntity setValuesToNull(SimpleEntity entity) {
		return SimpleEntity.builder(entity).simpleBoolean(null) //
				.simpleByte(null) //
				.simpleShort(null) //
				.simpleInt(null) //
				.simpleLong(null) //
				.simpleFloat(null) //
				.simpleDouble(null) //
				.simpleString(null) //
				.simpleByteArray(null).build();
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
