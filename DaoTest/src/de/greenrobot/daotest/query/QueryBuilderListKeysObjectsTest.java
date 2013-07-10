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

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.test.AbstractDaoTestStringPk;
import de.greenrobot.daotest.StringKeyValueEntity;
import de.greenrobot.daotest.StringKeyValueEntityDao;

public class QueryBuilderListKeysObjectsTest extends AbstractDaoTestStringPk<StringKeyValueEntityDao, StringKeyValueEntity> {

    public QueryBuilderListKeysObjectsTest() {
        super(StringKeyValueEntityDao.class);
    }
	
    @Override
    protected void setUp() {
        super.setUp();
        
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }
    
    public void testSize() {
    	insert(3);
    	
    	List<String> keys = dao.queryBuilder().orderAsc(StringKeyValueEntityDao.Properties.Key).listKeys();
    	
    	assertEquals(3, keys.size());
    }
    
    public void testValues() {
    	insert(3);
    	
    	List<String> keys = dao.queryBuilder().orderAsc(StringKeyValueEntityDao.Properties.Key).listKeys();    	
    	
    	assertEquals("key0", keys.get(0));
    	assertEquals("key1", keys.get(1));
    	assertEquals("key2", keys.get(2));
    }
    
    public void testEmpty() {
    	dao.deleteAll();
    	
    	List<String> keys = dao.queryBuilder().listKeys();
    	assertTrue(keys.isEmpty());
    }

	@Override
	protected StringKeyValueEntity createEntity(String key) {
		return createEntity(key, "default");
	}
	
	protected StringKeyValueEntity createEntity(String key, String value) {
		StringKeyValueEntity entity = new StringKeyValueEntity();
		entity.setKey(key);
		entity.setValue(value);
		return entity;
	}
	
	protected void insert(int num) {
		List<StringKeyValueEntity> entities = new ArrayList<StringKeyValueEntity>();
		for(int i = 0; i < num; i++) {
			entities.add(createEntity("key" + i, "value" + i));
		}
		dao.insertInTx(entities);
	}

}
