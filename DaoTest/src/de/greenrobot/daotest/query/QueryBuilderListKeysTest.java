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
package de.greenrobot.daotest.query;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daotest.TestEntity;
import de.greenrobot.daotest.TestEntityDao;
import de.greenrobot.daotest.entity.TestEntityTestBase;

public class QueryBuilderListKeysTest extends TestEntityTestBase {
	
    @Override
    protected void setUp() {
        super.setUp();
        
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public void testSize() {
    	insert(3);
    	
    	List<Long> keys = dao.queryBuilder().listKeys();
    	assertEquals(3, keys.size());
    }
    public void testValues() {        
        List<TestEntity> entities = new ArrayList<TestEntity>();
        entities.add(createEntity(1));
        entities.add(createEntity(9));
        entities.add(createEntity(12));
        dao.insertInTx(entities);
        
        List<Long> keys = dao.queryBuilder().orderAsc(TestEntityDao.Properties.Id).listKeys();
        assertEquals(3, keys.size());
        assertEquals(Long.valueOf(1), (Long) keys.get(0));
        assertEquals(Long.valueOf(9), (Long) keys.get(1));
        assertEquals(Long.valueOf(12), (Long) keys.get(2));
    }
    
    private TestEntity createEntity(long id) {
    	TestEntity entity = new TestEntity();
    	entity.setId(id);
    	entity.setSimpleStringNotNull("green");
    	return entity;
    }

    public void testBuildTwice() {
        insert(3);

        QueryBuilder<TestEntity> builder = dao.queryBuilder();
        Query<TestEntity> query1 = builder.build();
        Query<TestEntity> query2 = builder.build();
        List<Long> keys1 = query1.listKeys();
        List<Long> keys2 = query2.listKeys();
        assertEquals(3, keys1.size());
        assertEquals(3, keys2.size());
        assertSame(keys1.get(1), keys2.get(1));
    }
    
    public void testEmpty() {
    	dao.deleteAll();
    	
    	List<Long> keys = dao.queryBuilder().listKeys();
    	assertTrue(keys.isEmpty());
    }

}
