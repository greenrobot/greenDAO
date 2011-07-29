package de.greenrobot.daogenerator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.greenrobot.daogenerator.Column;
import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.DaoUtil;
import de.greenrobot.daogenerator.PropertyType;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.Entity;

public class SimpleTest {

    @Test
    public void testMinimalSchema() throws Exception {
        Schema schema = new Schema(1, "de.greenrobot.testdao");
        Entity adressTable = schema.addTable("Adresse");
        Column idColumn = adressTable.addIdColumn().asc().build();
        adressTable.addColumn(PropertyType.Int, "count");
        adressTable.addColumn(PropertyType.Int, "dummy").notNull();
        assertEquals(1, schema.getTables().size());
        assertEquals(3, adressTable.getColumns().size());

        File daoFile = new File("test-out/de/greenrobot/testdao/" + adressTable.getClassName() + "Dao.java");
        daoFile.delete();
        assertFalse(daoFile.exists());
        
        new DaoGenerator().createDaos("test-out", schema);
        
        assertEquals("PRIMARY KEY ASC", idColumn.getConstraints());
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

}
