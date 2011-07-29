package de.greenrobot.daogenerator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.DaoUtil;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class SimpleTest {

    @Test
    public void testMinimalSchema() throws Exception {
        Schema schema = new Schema(1, "de.greenrobot.testdao");
        Entity adressTable = schema.addEntity("Adresse");
        Property idColumn = adressTable.addIdProperty().asc().build();
        adressTable.addIntProperty( "count");
        adressTable.addIntProperty("dummy").notNull();
        assertEquals(1, schema.getEntities().size());
        assertEquals(3, adressTable.getProperties().size());

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
