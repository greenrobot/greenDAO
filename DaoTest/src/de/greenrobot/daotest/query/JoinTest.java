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

import de.greenrobot.dao.query.Join;
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

    public void testEqInteger() {
        List<TestEntity> targetEntities = new ArrayList<TestEntity>();
        List<RelationEntity> entities = new ArrayList<RelationEntity>();
        for (int i = 0; i < 10; i++) {
            TestEntity testEntity = new TestEntity();
            testEntity.setSimpleInt(i + 1);
            testEntity.setSimpleStringNotNull("string-" + (i + 1));
            targetEntities.add(testEntity);
        }
        testEntityDao.insertInTx(targetEntities);
        for (int i = 0; i < 10; i++) {
            RelationEntity entity = new RelationEntity();
            entity.setSimpleString("entity-" + (i + 1));
            entity.setTestNotNull(targetEntities.get(i));
            entities.add(entity);
        }
        relationEntityDao.insertInTx(entities);

        QueryBuilder<RelationEntity> queryBuilder = relationEntityDao.queryBuilder();
        Join<RelationEntity, TestEntity> join = queryBuilder.join(RelationEntityDao.Properties.TestIdNotNull,
                TestEntity.class);
        join.where(Properties.SimpleInt.eq(5));
        RelationEntity unique = queryBuilder.unique();
        assertEquals("entity-5", unique.getSimpleString());
    }
}
