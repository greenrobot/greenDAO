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

package org.greenrobot.greendao.generator.gentest;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;
import org.greenrobot.greendao.generator.ToMany;

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

    private final Schema schema;
    private final Entity testEntity;
    private final Entity dateEntity;
    private final Schema schema2;
    private final Schema schemaUnitTest;

    public TestDaoGenerator() {
        schema = new Schema(1, "org.greenrobot.greendao.daotest");
        schema.setDefaultJavaPackageTest("org.greenrobot.greendao.daotest.entity");

        createSimple();
        createSimpleNotNull();
        testEntity = createTest();
        createRelation();
        dateEntity = createDate();
        createSpecialNames();
        createAbcdef();
        createToMany();
        createTreeEntity();
        createActive();
        createExtendsImplements();
        createStringKeyValue();
        createAutoincrement();
        createSqliteMaster();
        createCustomType();
        createIndexedString();

        schema2 = createSchema2();
        schemaUnitTest = createSchemaUnitTest();
    }

    public void generate() throws Exception {
        DaoGenerator daoGenerator = new DaoGenerator();
        daoGenerator.generateAll(schema, "../DaoTestBase/src/main/java");
        daoGenerator.generateAll(schema2, "../DaoTestBase/src/main/java");
        daoGenerator.generateAll(schemaUnitTest, "../DaoTest/src/test/java");
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
        testEntity.setJavaDoc("This entity is used by internal tests of greenDAO.\n" +
                "(This JavaDoc is defined in the generator project.)");
        testEntity.setCodeBeforeClass("// This is another test comment, you could also apply annotations like this");
        testEntity.addIdProperty().javaDocField("JavaDoc test field");
        testEntity.addIntProperty("simpleInt").notNull().javaDocGetter("JavaDoc test getter");
        testEntity.addIntProperty("simpleInteger").javaDocSetter("JavaDoc test setter");
        testEntity.addStringProperty("simpleStringNotNull").notNull().javaDocGetterAndSetter("JavaDoc test getter and setter");
        testEntity.addStringProperty("simpleString");
        testEntity.addStringProperty("indexedString").index();
        testEntity.addStringProperty("indexedStringAscUnique").indexAsc(null, true);
        testEntity.addDateProperty("simpleDate");
        testEntity.addBooleanProperty("simpleBoolean");
        testEntity.addByteArrayProperty("simpleByteArray");
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

        Property[] sourceProperties = {sourceIdProperty, sourceJoinProperty};
        Property[] targetProperties = {toManyIdProperty, targetJoinProperty};
        ToMany toManyJoinTwo = toManyEntity.addToMany(sourceProperties, toManyTargetEntity, targetProperties);
        toManyJoinTwo.setName("toManyJoinTwo");
        toManyJoinTwo.orderDesc(targetJoinProperty);
        toManyJoinTwo.orderDesc(targetIdProperty);

        Entity toManyJoinEntity = schema.addEntity("JoinManyToDateEntity");
        toManyJoinEntity.addIdProperty();
        Property id1 = toManyJoinEntity.addLongProperty("idToMany").getProperty();
        Property id2 = toManyJoinEntity.addLongProperty("idDate").getProperty();

        toManyEntity.addToMany(dateEntity, toManyJoinEntity, id1, id2);
    }

    protected void createTreeEntity() {
        Entity treeEntity = schema.addEntity("TreeEntity");
        treeEntity.addIdProperty();
        Property parentIdProperty = treeEntity.addLongProperty("parentId").getProperty();
        treeEntity.addToOne(treeEntity, parentIdProperty).setName("parent");
        treeEntity.addToMany(treeEntity, parentIdProperty).setName("children");
    }

    protected Entity createDate() {
        Entity dateEntity = schema.addEntity("DateEntity");
        dateEntity.addIdProperty();
        dateEntity.addDateProperty("date").codeBeforeField("// Test code for\n    // field")
        .codeBeforeGetter("// Test code for\n    // getter").codeBeforeSetter("// Test code for\n    // setter");
        dateEntity.addImport("java.lang.String");
        dateEntity.addDateProperty("dateNotNull").notNull();
        return dateEntity;
    }

    protected void createSpecialNames() {
        Entity specialNamesEntity = schema.addEntity("SpecialNamesEntity");
        specialNamesEntity.setDbName("ORDER TRANSACTION GROUP BY");
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
        entity.setSkipCreationInDb(true);
        entity.setHasKeepSections(true);
        entity.addStringProperty("type");
        entity.addStringProperty("name");
        entity.addStringProperty("tableName").dbName("tbl_name");
        entity.addLongProperty("rootpage");
        entity.addStringProperty("sql");
    }

    protected void createCustomType() {
        Entity entity = schema.addEntity("CustomTypeEntity");
        entity.addIdProperty();
        entity.addLongProperty("myCustomTimestamp").customType("org.greenrobot.greendao.daotest.customtype.MyTimestamp",
                "org.greenrobot.greendao.daotest.customtype.MyTimestampConverter");
    }

    protected void createIndexedString() {
        Entity entity = schema.addEntity("IndexedStringEntity");
        entity.addIdProperty();
        entity.addStringProperty("indexedString").index();
    }

    private Schema createSchema2() {
        Schema schema2 = new Schema(1, "org.greenrobot.greendao.daotest2");
        schema2.setDefaultJavaPackageTest("org.greenrobot.greendao.daotest2.entity");
        schema2.setDefaultJavaPackageDao("org.greenrobot.greendao.daotest2.dao");
        schema2.enableKeepSectionsByDefault();

        Entity keepEntity = schema2.addEntity("KeepEntity");
        keepEntity.addIdProperty();

        Entity toManyTarget2 = schema2.addEntity("ToManyTarget2");
        toManyTarget2.addIdProperty();
        Property toManyTarget2FkId = toManyTarget2.addLongProperty("fkId").getProperty();
        toManyTarget2.setSkipGenerationTest(true);

        Entity toOneTarget2 = schema2.addEntity("ToOneTarget2");
        toOneTarget2.addIdProperty();
        toOneTarget2.setJavaPackage("org.greenrobot.greendao.daotest2.to1_specialentity");
        toOneTarget2.setJavaPackageDao("org.greenrobot.greendao.daotest2.to1_specialdao");
        toOneTarget2.setJavaPackageTest("org.greenrobot.greendao.daotest2.to1_specialtest");
        toOneTarget2.setSkipGenerationTest(true);

        Entity relationSource2 = schema2.addEntity("RelationSource2");
        relationSource2.addIdProperty();
        relationSource2.addToMany(toManyTarget2, toManyTarget2FkId);
        Property toOneId = relationSource2.addLongProperty("toOneId").getProperty();
        relationSource2.addToOne(toOneTarget2, toOneId);
        relationSource2.setJavaPackage("org.greenrobot.greendao.daotest2.specialentity");
        relationSource2.setJavaPackageDao("org.greenrobot.greendao.daotest2.specialdao");
        relationSource2.setJavaPackageTest("org.greenrobot.greendao.daotest2.specialtest");
        relationSource2.setSkipGenerationTest(true);
        return schema2;
    }

    private Schema createSchemaUnitTest() {
        Schema schema = new Schema(1, "org.greenrobot.greendao.unittest");

        Entity entity = schema.addEntity("MinimalEntity");
        entity.addIdProperty();
        return schema;
    }

}
