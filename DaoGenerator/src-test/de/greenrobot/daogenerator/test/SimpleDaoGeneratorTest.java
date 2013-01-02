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
package de.greenrobot.daogenerator.test;

import java.io.File;

import de.greenrobot.daogenerator.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleDaoGeneratorTest {

    @Test
    public void testMinimalSchema() throws Exception {
        Schema schema = new Schema(1, "de.greenrobot.testdao");
        Entity adressTable = schema.addEntity("Adresse");
        Property idProperty = adressTable.addIdProperty().getProperty();
        adressTable.addIntProperty("count").index();
        adressTable.addIntProperty("dummy").notNull();
        assertEquals(1, schema.getEntities().size());
        assertEquals(3, adressTable.getProperties().size());

        File daoFile = new File("test-out/de/greenrobot/testdao/" + adressTable.getClassName() + "Dao.java");
        daoFile.delete();
        assertFalse(daoFile.exists());

        new DaoGenerator().generateAll(schema, "test-out");

        assertEquals("PRIMARY KEY", idProperty.getConstraints());
        assertTrue(daoFile.toString(), daoFile.exists());
    }

    @Test
    public void testDbName() {
        assertEquals("NORMAL", DaoUtil.dbName("normal"));
        assertEquals("NORMAL", DaoUtil.dbName("Normal"));
        assertEquals("CAMEL_CASE", DaoUtil.dbName("CamelCase"));
        assertEquals("CAMEL_CASE_THREE", DaoUtil.dbName("CamelCaseThree"));
        assertEquals("CAMEL_CASE_XXXX", DaoUtil.dbName("CamelCaseXXXX"));
    }


	@Test
	public void testAnnotations() throws Exception {
		String annotationPackage = "de.greenrobot.testdao.annotations";
		Annotation packagedAnnotation = new Annotation(annotationPackage + ".TestAnnotation");
		assertEquals("TestAnnotation", packagedAnnotation.getName());
		assertEquals(annotationPackage, packagedAnnotation.getPackage());
		Annotation simpleAnnotation = new Annotation("SimpleAnnotation");
		assertEquals("SimpleAnnotation", simpleAnnotation.getName());
		assertNull(simpleAnnotation.getPackage());

		Schema schema = new Schema(1, "de.greenrobot.testdao");
		Entity userEntity = schema.addEntity("TestUser");
		userEntity.addFullConstructorAnnotation(new CustomConstructorAnnotation("\"myName\"", 25));

		Property idProperty = userEntity.addIdProperty().getProperty();
		userEntity.addClassAnnotation(new Annotation(annotationPackage + ".ClassAnnotation"));
		assertEquals(1, userEntity.getClassAnnotations().size());
		userEntity.addEmptyConstructorAnnotation(new Annotation(annotationPackage + ".ConstructorAnnotation"));
		assertEquals(1, userEntity.getEmptyConstructorAnnotations().size());
		assertEquals(1, userEntity.getFullConstructorAnnotations().size());
		Property nameProperty = userEntity.addStringProperty("name").addFieldAnnotation(new Annotation(annotationPackage + ".FieldAnnotation")).getProperty();
		assertEquals(1, nameProperty.getFieldAnnotations().size());
		JSonProperty ageAnnotation = new JSonProperty("years");
		Property ageProperty = userEntity.addIntProperty("age")
				.addSetterGetterAnnotation(new Annotation(annotationPackage + ".SetterGetterAnnotation"))
				.addSetterAnnotation(ageAnnotation)
				.getProperty();
		assertEquals(2, ageProperty.getSetterAnnotations().size());
		assertEquals(1, ageProperty.getGetterAnnotations().size());
		new DaoGenerator().generateAll(schema, "test-out2");
		assertTrue(userEntity.getAdditionalImportsEntity().contains(annotationPackage + ".ClassAnnotation"));
		assertTrue(userEntity.getAdditionalImportsEntity().contains(annotationPackage + ".ConstructorAnnotation"));
		System.out.println(userEntity.getAdditionalImportsEntity());
		assertTrue(userEntity.getAdditionalImportsEntity().contains(ageAnnotation.getPackage() + ".JsonProperty"));
		assertTrue(userEntity.getAdditionalImportsEntity().contains(CustomConstructorAnnotation.PACKAGE + ".CustomConstructorAnnotation"));
	}

	private static class JSonProperty extends Annotation {

		public JSonProperty(String params) {
			super("JsonProperty", "\"" + params + "\"");
		}

		@Override
		public String getPackage() {
			return "com.fasterxml.jackson.annotation";
		}
	}

	private static class CustomConstructorAnnotation extends Annotation {
		public static final String PACKAGE = "de.greenrobot.testdao.annotation2";
		public CustomConstructorAnnotation(String name, int age) {
			super("CustomConstructorAnnotation", "name", name, "age", Integer.toString(age));
		}

		@Override
		public String getPackage() {
			return PACKAGE;
		}
	}

}
