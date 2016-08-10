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

package org.greenrobot.greendao.generator;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimpleDaoGeneratorTest {

    @Test
    public void testMinimalSchema() throws Exception {
        Schema schema = new Schema(1, "org.greenrobot.testdao");
        Entity addressEntity = schema.addEntity("Addresse");
        Property idProperty = addressEntity.addIdProperty().getProperty();
        addressEntity.addIntProperty("count").index();
        addressEntity.addIntProperty("dummy").notNull();
        assertEquals(1, schema.getEntities().size());
        assertEquals(3, addressEntity.getProperties().size());

        File outputDir = new File("build/test-out");
        outputDir.mkdirs();

        File daoFile = new File(outputDir, "org/greenrobot/testdao/" + addressEntity.getClassName() + "Dao.java");
        daoFile.delete();
        assertFalse(daoFile.exists());

        new DaoGenerator().generateAll(schema, outputDir.getPath());

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

    @Test(expected = RuntimeException.class)
    public void testInterfacesError() throws Exception {
        Schema schema = new Schema(1, "org.greenrobot.testdao");
        Entity addressTable = schema.addEntity("Addresse");
        addressTable.implementsInterface("Dummy");
        addressTable.implementsInterface("Dummy");
    }
}
