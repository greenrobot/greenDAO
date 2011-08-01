package de.greenrobot.daogenerator.gentest;

import java.io.File;
import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class TestDaoGenerator {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String outDir = "../DaoTest/src-gen";
        if (!new File(outDir).exists()) {
            throw new IOException(outDir + " does not exist. Project DaoTest must be available.");
        }
        Schema schema = new Schema(1, "de.greenrobot.testdao");
        
        Entity simple = schema.addEntity("TestEntitySimple");
        simple.addIdProperty();
        simple.addIntProperty("simpleInt");
        simple.addIntProperty("simpleIntNotNull").notNull();
        simple.addLongProperty("simpleLong");
        simple.addLongProperty("simpleLongNotNull").notNull();
        simple.addStringProperty("simpleString");
        simple.addStringProperty("simpleStringNotNull").notNull();

        new DaoGenerator().createDaos(outDir, schema);
    }

}
