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
package de.greenrobot.daogenerator.gentest;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

/**
 * Generates test entities for test project DaoTest.
 * 
 * @author Markus
 */
public class TestDaoGenerator {

    public static void main(String[] args) throws Exception {
        TestDaoGenerator testDaoGenerator = new TestDaoGenerator();
        testDaoGenerator.generate();
    }

    private Schema schema;
    private Entity testEntity;

    public TestDaoGenerator() {
        schema = new Schema(1, "de.greenrobot.dao.test");

        createSimple();
        createSimpleNotNull();
        testEntity = createTest();
        createRelation();
        createDate();
        createSpecialNames();
    }

    public void generate() throws Exception {
        new DaoGenerator().generateAll("../DaoTest/src-gen", "../DaoTest/src", schema);
    }

    protected void createSimple() {
        Entity simple = schema.addEntity("SimpleEntity");
        simple.addIdProperty();
        simple.addBooleanProperty("simpleBoolean");
        simple.addByteProperty("simpleByte");
        simple.addShortProperty("simpleShort");
        simple.addIntProperty("simpleInt");
        simple.addLongProperty("simpleLong");
        simple.addFloatProperty("simpleFloat");
        simple.addDoubleProperty("simpleDouble");
        simple.addStringProperty("simpleString");
        simple.addByteArrayProperty("simpleByteArray");
    }

    protected void createSimpleNotNull() {
        Entity notNull = schema.addEntity("SimpleEntityNotNull");
        notNull.addIdProperty().notNull();
        notNull.addBooleanProperty("simpleBoolean").notNull();
        notNull.addByteProperty("simpleByte").notNull();
        notNull.addShortProperty("simpleShort").notNull();
        notNull.addIntProperty("simpleInt").notNull();
        notNull.addLongProperty("simpleLong").notNull();
        notNull.addFloatProperty("simpleFloat").notNull();
        notNull.addDoubleProperty("simpleDouble").notNull();
        notNull.addStringProperty("simpleString").notNull();
        notNull.addByteArrayProperty("simpleByteArray").notNull();
    }

    protected Entity createTest() {
        Entity testEntity = schema.addEntity("TestEntity");
        testEntity.addIdProperty();
        testEntity.addIntProperty("simpleInt").notNull();
        testEntity.addIntProperty("simpleInteger");
        testEntity.addStringProperty("simpleStringNotNull").notNull();
        testEntity.addStringProperty("simpleString");
        testEntity.addStringProperty("indexedString").index();
        testEntity.addStringProperty("indexedStringAscUnique").indexAsc(null, true);
        return testEntity;
    }

    protected void createRelation() {
        Entity relationEntity = schema.addEntity("RelationEntity");
        relationEntity.addIdProperty();
        Property parentIdProperty = relationEntity.addLongProperty("parentId").getProperty();
        relationEntity.addToOne(relationEntity, parentIdProperty).setName("parent");
        Property testIdProperty = relationEntity.addLongProperty("testId").getProperty();
        relationEntity.addToOne(testEntity, testIdProperty);
        Property testIdNotNullProperty = relationEntity.addLongProperty("testIdNotNull").notNull().getProperty();
        relationEntity.addToOne(testEntity, testIdNotNullProperty).setName("testNotNull");
        relationEntity.addStringProperty("simpleString");
    }

    protected void createDate() {
        Entity dateEntity = schema.addEntity("DateEntity");
        dateEntity.addIdProperty();
        dateEntity.addDateProperty("date");
        dateEntity.addDateProperty("dateNotNull").notNull();
    }

    protected void createSpecialNames() {
        Entity specialNamesEntity = schema.addEntity("SpecialNamesEntity");
        specialNamesEntity.addIdProperty();
        specialNamesEntity.addStringProperty("count");
        specialNamesEntity.addStringProperty("select");
        specialNamesEntity.addStringProperty("sum");
        specialNamesEntity.addStringProperty("avg");
        specialNamesEntity.addStringProperty("join");
        specialNamesEntity.addStringProperty("distinct");
        specialNamesEntity.addStringProperty("on");
        specialNamesEntity.addStringProperty("index");
    }

}
