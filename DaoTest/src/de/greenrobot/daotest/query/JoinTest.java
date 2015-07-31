/*
 * Copyright (C) 2011-2015 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.greenrobot.daotest.query;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.test.AbstractDaoSessionTest;
import de.greenrobot.daotest.DaoMaster;
import de.greenrobot.daotest.DaoSession;
import de.greenrobot.daotest.RelationEntity;
import de.greenrobot.daotest.RelationEntityDao;
import de.greenrobot.daotest.TestEntity;
import de.greenrobot.daotest.TestEntityDao;
import de.greenrobot.daotest.TestEntityDao.Properties;

import java.util.ArrayList;
import java.util.List;

public class JoinTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    private TestEntityDao testEntityDao;
    private RelationEntityDao relationEntityDao;

    public JoinTest() {
        super(DaoMaster.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        testEntityDao = daoSession.getTestEntityDao();
        relationEntityDao = daoSession.getRelationEntityDao();
    }

    public void testJoinSimple() {
        prepareData();
        QueryBuilder<RelationEntity> queryBuilder = createQueryBuilder(5);
        RelationEntity unique = queryBuilder.uniqueOrThrow();
        assertEquals("entity-5", unique.getSimpleString());
    }

    public void testJoinSimpleParameterValue() {
        prepareData();
        QueryBuilder<RelationEntity> queryBuilder = createQueryBuilder(-1);
        Query<RelationEntity> query = queryBuilder.build();
        for (int i = 0; i < 10; i++) {
            query.setParameter(0, i + 1);
            RelationEntity unique = query.uniqueOrThrow();
            assertEquals("entity-" + (i + 1), unique.getSimpleString());
        }
    }

    public void testJoinMixedParameterValues() {
        prepareData();
        QueryBuilder<RelationEntity> queryBuilder = relationEntityDao.queryBuilder();
        queryBuilder.where(RelationEntityDao.Properties.SimpleString.like(""), RelationEntityDao.Properties.SimpleString.ge(""));
        Join<RelationEntity, TestEntity> join = queryBuilder.join(RelationEntityDao.Properties.TestIdNotNull,
                TestEntity.class);
        join.where(Properties.SimpleInt.le(0));
        queryBuilder.offset(0).limit(0);
        Query<RelationEntity> query = queryBuilder.build();
        query.setParameter(0, "entity-%");
        query.setParameter(1, "entity-4");
        query.setParameter(2, 6);
        query.setOffset(1);
        query.setLimit(99);
        List<RelationEntity> entities = query.list();
        assertEquals(2, entities.size());
        assertEquals("entity-5", entities.get(0).getSimpleString());
        assertEquals("entity-6", entities.get(1).getSimpleString());
    }

    public void testJoinOfJoin() {
        prepareData();
        List<RelationEntity> relationEntities = relationEntityDao.loadAll();
        relationEntities.get(2).setParent(relationEntities.get(4));
        relationEntities.get(3).setParent(relationEntities.get(5));
        relationEntities.get(7).setParent(relationEntities.get(5));
        relationEntityDao.updateInTx(relationEntities);

        QueryBuilder<RelationEntity> queryBuilder = relationEntityDao.queryBuilder();
        Join<RelationEntity, RelationEntity> join1 =
                queryBuilder.join(RelationEntityDao.Properties.ParentId, RelationEntity.class);
        queryBuilder.join(join1, RelationEntityDao.Properties.TestIdNotNull, TestEntity.class, Properties.Id)
                .where(Properties.SimpleInt.lt(6));

        Query<RelationEntity> query = queryBuilder.build();
        RelationEntity entity = query.uniqueOrThrow();
        assertEquals(relationEntities.get(2).getSimpleString(), entity.getSimpleString());

        query.setParameter(0, 99);
        assertEquals(3, query.list().size());
    }

    public void testJoinDelete() {
        prepareData();
        QueryBuilder<RelationEntity> queryBuilder = createQueryBuilder(5);
        try {
            queryBuilder.buildDelete().executeDeleteWithoutDetachingEntities();
        } catch (DaoException e) {
            assertEquals("JOINs are not supported for DELETE queries", e.getMessage());
            return;
        }
        // Never executed, unsupported by SQLite
        assertEquals(9, relationEntityDao.count());
        assertEquals(10, testEntityDao.count());
        assertNull(relationEntityDao.queryBuilder().where(Properties.SimpleString.eq("entity-5")).unique());
    }

    public void testJoinCount() {
        prepareData();
        QueryBuilder<RelationEntity> queryBuilder = relationEntityDao.queryBuilder();
        Join<RelationEntity, TestEntity> join = queryBuilder.join(RelationEntityDao.Properties.TestIdNotNull,
                TestEntity.class);
        join.where(Properties.SimpleInt.gt(6));
        queryBuilder.count();
        assertEquals(4, queryBuilder.count());
    }

    private void prepareData() {
        List<TestEntity> targetEntities = new ArrayList<TestEntity>();
        for (int i = 0; i < 10; i++) {
            TestEntity testEntity = new TestEntity();
            testEntity.setSimpleInt(i + 1);
            testEntity.setSimpleStringNotNull("string-" + (i + 1));
            targetEntities.add(testEntity);
        }
        testEntityDao.insertInTx(targetEntities);

        List<RelationEntity> entities = new ArrayList<RelationEntity>();
        for (int i = 0; i < 10; i++) {
            RelationEntity entity = new RelationEntity();
            entity.setSimpleString("entity-" + (i + 1));
            entity.setTestNotNull(targetEntities.get(i));
            entities.add(entity);
        }
        relationEntityDao.insertInTx(entities);
    }

    private QueryBuilder<RelationEntity> createQueryBuilder(int simpleIntWhereValue) {
        QueryBuilder<RelationEntity> queryBuilder = relationEntityDao.queryBuilder();
        Join<RelationEntity, TestEntity> join = queryBuilder.join(RelationEntityDao.Properties.TestIdNotNull,
                TestEntity.class);
        join.where(Properties.SimpleInt.eq(simpleIntWhereValue));
        return queryBuilder;
    }


}
