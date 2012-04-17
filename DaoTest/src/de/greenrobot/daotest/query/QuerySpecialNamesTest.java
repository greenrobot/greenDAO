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

import de.greenrobot.dao.QueryBuilder;
import de.greenrobot.dao.test.AbstractDaoTest;
import de.greenrobot.daotest.SpecialNamesEntity;
import de.greenrobot.daotest.SpecialNamesEntityDao;
import de.greenrobot.daotest.SpecialNamesEntityDao.Properties;

public class QuerySpecialNamesTest extends  AbstractDaoTest<SpecialNamesEntityDao, SpecialNamesEntity, Long> {
    
    public QuerySpecialNamesTest() {
        super(SpecialNamesEntityDao.class);
    }

    @Override
    protected void setUp() {
        super.setUp();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public void testWhereWithSpecialNames() {
        QueryBuilder<SpecialNamesEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(Properties.Avg.isNotNull());
        queryBuilder.where(Properties.Count.isNotNull());
        queryBuilder.where(Properties.Distinct.isNotNull());
        queryBuilder.where(Properties.Index.isNotNull());
        queryBuilder.where(Properties.Join.isNotNull());
        queryBuilder.where(Properties.On.isNotNull());
        queryBuilder.where(Properties.Select.isNotNull());
        queryBuilder.where(Properties.Sum.isNotNull());
        queryBuilder.list();
    } 

    public void testOrderWithSpecialNames() {
        QueryBuilder<SpecialNamesEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.orderAsc(Properties.Avg);
        queryBuilder.orderAsc(Properties.Count);
        queryBuilder.orderAsc(Properties.Distinct);
        queryBuilder.orderAsc(Properties.Index);
        queryBuilder.orderAsc(Properties.Join);
        queryBuilder.orderAsc(Properties.On);
        queryBuilder.orderAsc(Properties.Select);
        queryBuilder.orderAsc(Properties.Sum);
        queryBuilder.list();
    } 

}
