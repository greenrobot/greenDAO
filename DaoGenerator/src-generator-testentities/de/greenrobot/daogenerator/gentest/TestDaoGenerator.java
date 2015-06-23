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
        createTreeEntity();
        createActive();
        createExtendsImplements();
        createStringKeyValue();
        createAutoincrement();
        createSqliteMaster();

        createSchema2();
    }

    public void generate() throws Exception {
        DaoGenerator daoGenerator = new DaoGenerator();
        daoGenerator.generateAll(schema, "../DaoTest/src-gen", "../DaoTest/src");
        daoGenerator.generateAll(schema2, "../DaoTest/src-gen", "../DaoTest/src");
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
        
        simple.addContentProvider().readOnly();
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
        toManyDesc.setName("toManyDescList");
        toManyDesc.orderDesc(targetIdProperty);

        ToMany toManyByJoinProperty = toManyEntity
                .addToMany(sourceJoinProperty, toManyTargetEntity, targetJoinProperty);
        toManyByJoinProperty.setName("toManyByJoinProperty");
        toManyByJoinProperty.orderAsc(targetIdProperty);

        Property[] sourceProperties = { sourceIdProperty, sourceJoinProperty };
        Property[] targetProperties = { toManyIdProperty, targetJoinProperty };
        ToMany toManyJoinTwo = toManyEntity.addToMany(sourceProperties, toManyTargetEntity, targetProperties);
        toManyJoinTwo.setName("toManyJoinTwo");
        toManyJoinTwo.orderDesc(targetJoinProperty);
        toManyJoinTwo.orderDesc(targetIdProperty);
    }

    protected void createTreeEntity() {
        Entity treeEntity = schema.addEntity("TreeEntity");
        treeEntity.addIdProperty();
        Property parentIdProperty = treeEntity.addLongProperty("parentId").getProperty();
        treeEntity.addToOne(treeEntity, parentIdProperty).setName("parent");
        treeEntity.addToMany(treeEntity, parentIdProperty).setName("children");
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
        specialNamesEntity.addIntProperty("order");
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
        activeEntity.setActive(true);
    }

    protected void createExtendsImplements() {
        Entity entity = schema.addEntity("ExtendsImplementsEntity");
        entity.addIdProperty();
        entity.addStringProperty("text");
        entity.setSuperclass("TestSuperclass");
        entity.implementsInterface("TestInterface");
        entity.implementsSerializable();
    }

    private void createSchema2() {
        schema2 = new Schema(1, "de.greenrobot.daotest2");
        schema2.setDefaultJavaPackageTest("de.greenrobot.daotest2.entity");
        schema2.setDefaultJavaPackageDao("de.greenrobot.daotest2.dao");
        schema2.enableKeepSectionsByDefault();

        Entity keepEntity = schema2.addEntity("KeepEntity");
        keepEntity.addIdProperty();

        Entity toManyTarget2 = schema2.addEntity("ToManyTarget2");
        toManyTarget2.addIdProperty();
        Property toManyTarget2FkId = toManyTarget2.addLongProperty("fkId").getProperty();
        toManyTarget2.setSkipGenerationTest(true);

        Entity toOneTarget2 = schema2.addEntity("ToOneTarget2");
        toOneTarget2.addIdProperty();
        toOneTarget2.setJavaPackage("de.greenrobot.daotest2.to1_specialentity");
        toOneTarget2.setJavaPackageDao("de.greenrobot.daotest2.to1_specialdao");
        toOneTarget2.setJavaPackageTest("de.greenrobot.daotest2.to1_specialtest");
        toOneTarget2.setSkipGenerationTest(true);

        Entity relationSource2 = schema2.addEntity("RelationSource2");
        relationSource2.addIdProperty();
        relationSource2.addToMany(toManyTarget2, toManyTarget2FkId);
        Property toOneId = relationSource2.addLongProperty("toOneId").getProperty();
        relationSource2.addToOne(toOneTarget2, toOneId);
        relationSource2.setJavaPackage("de.greenrobot.daotest2.specialentity");
        relationSource2.setJavaPackageDao("de.greenrobot.daotest2.specialdao");
        relationSource2.setJavaPackageTest("de.greenrobot.daotest2.specialtest");
        relationSource2.setSkipGenerationTest(true);
    }

    protected void createStringKeyValue() {
        Entity entity = schema.addEntity("StringKeyValueEntity");
        entity.addStringProperty("key").primaryKey();
        entity.addStringProperty("value");
    }

    protected void createAutoincrement() {
        Entity entity = schema.addEntity("AutoincrementEntity");
        entity.addIdProperty().autoincrement();
    }

    protected void createSqliteMaster() {
        Entity entity = schema.addEntity("SqliteMaster");
        entity.setSkipTableCreation(true);
        entity.setHasKeepSections(true);
        entity.addStringProperty("type");
        entity.addStringProperty("name");
        entity.addStringProperty("tableName").columnName("tbl_name");
        entity.addLongProperty("rootpage");
        entity.addStringProperty("sql");
    }

}
