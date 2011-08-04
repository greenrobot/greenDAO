package de.greenrobot.daogenerator.gentest;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Generates enities and DAOs for the example project DaoExample;
 * 
 * @author Markus
 */
public class ExampleDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "de.greenrobot.daoexample");

        Entity simple = schema.addEntity("Note");
        simple.addIdProperty();
        simple.addStringProperty("text").notNull();
        simple.addStringProperty("date");

        new DaoGenerator().generateAll("../DaoExample/src-gen", schema);
    }

}
