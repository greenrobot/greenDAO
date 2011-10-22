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
import de.greenrobot.daogenerator.ToMany;

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
    private Schema schema2;

    public TestDaoGenerator() {
        schema = new Schema(1, "de.greenrobot.daotest");
        schema.setDefaultJavaPackageTest("de.greenrobot.daotest.entity");

        createSimple();
        createSimpleNotNull();
        testEntity = createTest();
        createRelation();
        createDate();
        createSpecialNames();
        createAbcdef();
        createToMany();
        createActive();

        schema2 = new Schema(1, "de.greenrobot.daotest2");
        schema2.setDefaultJavaPackageTest("de.greenrobot.daotest2.entity");
        schema2.enableKeepSections();

        schema2.addEntity("KeepEntity").addIdProperty();
    }

    public void generate() throws Exception {
        DaoGenerator daoGenerator = new DaoGenerator();
        daoGenerator.generateAll("../DaoTest/src-gen", "../DaoTest/src", schema);
        daoGenerator.generateAll("../DaoTest/src-gen", "../DaoTest/src", schema2);
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
        testEntity.addDateProperty("simpleDate");
        testEntity.addBooleanProperty("simpleBoolean");
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
        relationEntity.addToOneWithoutProperty("testWithoutProperty", testEntity, "WITHOUT_PROPERTY_TEST_ID");
    }

    protected void createToMany() {
        Entity toManyTargetEntity = schema.addEntity("ToManyTargetEntity");
        Property toManyIdProperty = toManyTargetEntity.addLongProperty("toManyId").getProperty();
        Property toManyIdDescProperty = toManyTargetEntity.addLongProperty("toManyIdDesc").getProperty();
        Property targetIdProperty = toManyTargetEntity.addIdProperty().getProperty();
        Property targetJoinProperty = toManyTargetEntity.addStringProperty("targetJoinProperty").getProperty();

        Entity toManyEntity = schema.addEntity("ToManyEntity");
        Property sourceIdProperty = toManyEntity.addIdProperty().getProperty();
        Property sourceJoinProperty = toManyEntity.addStringProperty("sourceJoinProperty").getProperty();
        
        ToMany toMany = toManyEntity.addToMany(toManyTargetEntity, toManyIdProperty);
        toMany.orderAsc(targetIdProperty);
        
        ToMany toManyDesc = toManyEntity.addToMany(toManyTargetEntity, toManyIdDescProperty);
        toManyDesc.setName("ToManyDescList");
        toManyDesc.orderDesc(targetIdProperty);
        
        ToMany toManyByJoinProperty = toManyEntity.addToMany(sourceJoinProperty, toManyTargetEntity, targetJoinProperty);
        toManyByJoinProperty.setName("ToManyByJoinProperty");
        toManyByJoinProperty.orderAsc(targetIdProperty);

        Property[] sourceProperties = {sourceIdProperty, sourceJoinProperty};
        Property[] targetProperties = {toManyIdProperty, targetJoinProperty};
        ToMany toManyJoinTwo = toManyEntity.addToMany(sourceProperties, toManyTargetEntity, targetProperties);
        toManyJoinTwo.setName("ToManyJoinTwo");
        toManyJoinTwo.orderDesc(targetJoinProperty);
        toManyJoinTwo.orderDesc(targetIdProperty);
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

    private void createAbcdef() {
        Entity entity = schema.addEntity("AbcdefEntity");
        entity.addIdProperty();
        entity.addIntProperty("a");
        entity.addIntProperty("b");
        entity.addIntProperty("c");
        entity.addIntProperty("d");
        entity.addIntProperty("e");
        entity.addIntProperty("f");
        entity.addIntProperty("g");
        entity.addIntProperty("h");
        entity.addIntProperty("j");
        entity.addIntProperty("i");
        entity.addIntProperty("k");
    }
    
    protected void createActive() {
        Entity activeEntity = schema.addEntity("AnActiveEntity");
        activeEntity.addIdProperty();
        activeEntity.addStringProperty("text");
        activeEntity.setActive();
    }

}
