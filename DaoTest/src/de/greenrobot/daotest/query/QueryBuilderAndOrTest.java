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

import de.greenrobot.dao.QueryBuilder;
import de.greenrobot.dao.test.AbstractDaoTest;
import de.greenrobot.daotest.AbcdefEntity;
import de.greenrobot.daotest.AbcdefEntityDao;
import de.greenrobot.daotest.AbcdefEntityDao.Properties;

public class QueryBuilderAndOrTest extends AbstractDaoTest<AbcdefEntityDao, AbcdefEntity, Long> {

    public QueryBuilderAndOrTest() {
        super(AbcdefEntityDao.class);
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    protected ArrayList<AbcdefEntity> insert(int count) {
        ArrayList<AbcdefEntity> list = new ArrayList<AbcdefEntity>();
        for (int i = 0; i < count; i++) {
            int base = i * 100;
            AbcdefEntity entity = new AbcdefEntity(null, base + 1, base + 2, base + 3, base + 4, base + 5, base + 6,
                    base + 7, base + 8, base + 9, base + 10, base + 11);
            list.add(entity);
        }
        dao.insertInTx(list);
        return list;
    }

    public void testSimpleQuery() {
        insert(3);

        List<AbcdefEntity> result = dao.queryBuilder().where(Properties.A.eq(1)).orderAsc(Properties.A).list();
        assertEquals(1, result.size());

        AbcdefEntity resultEntity = result.get(0);
        assertEquals(1, (int) resultEntity.getA());
    }

    public void testOr() {
        insert(3);

        QueryBuilder<AbcdefEntity> qb = dao.queryBuilder();
        qb.whereOr(Properties.A.eq(1), Properties.A.eq(101));
        List<AbcdefEntity> result = qb.orderAsc(Properties.A).list();
        assertEquals(2, result.size());

        assertEquals(1, (int) result.get(0).getA());
        assertEquals(101, (int) result.get(1).getA());
    }

    public void testOr3() {
        insert(5);

        QueryBuilder<AbcdefEntity> qb = dao.queryBuilder();
        qb.whereOr(Properties.A.eq(1), Properties.A.eq(101), Properties.B.eq(302));
        List<AbcdefEntity> result = qb.orderAsc(Properties.A).list();
        assertEquals(3, result.size());

        assertEquals(1, (int) result.get(0).getA());
        assertEquals(101, (int) result.get(1).getA());
        assertEquals(301, (int) result.get(2).getA());
    }

    public void testOrNested() {
        insert(10);

        QueryBuilder<AbcdefEntity> qb = dao.queryBuilder();
        qb.whereOr(Properties.A.eq(101), //
                Properties.B.eq(302), Properties.C.eq(603));
        List<AbcdefEntity> result = qb.orderAsc(Properties.A).list();
        assertEquals(3, result.size());

        assertEquals(101, (int) result.get(0).getA());
        assertEquals(301, (int) result.get(1).getA());
        assertEquals(601, (int) result.get(2).getA());
    }

    public void testOrNestedNested() {
        insert(10);

        QueryBuilder<AbcdefEntity> qb = dao.queryBuilder();
        qb.whereOr(Properties.A.eq(101), //
                qb.or(Properties.B.eq(302), //
                        qb.or(Properties.C.eq(503), Properties.D.eq(804))));
        List<AbcdefEntity> result = qb.orderAsc(Properties.A).list();
        assertEquals(4, result.size());

        assertEquals(101, (int) result.get(0).getA());
        assertEquals(301, (int) result.get(1).getA());
        assertEquals(501, (int) result.get(2).getA());
        assertEquals(801, (int) result.get(3).getA());
    }

    public void testAnd() {
        insert(5);

        QueryBuilder<AbcdefEntity> qb = dao.queryBuilder();
        qb.where(Properties.A.eq(201), Properties.B.eq(202));
        List<AbcdefEntity> result = qb.orderAsc(Properties.A).list();
        assertEquals(1, result.size());

        assertEquals(201, (int) result.get(0).getA());
    }

    public void testOrAnd() {
        insert(10);

        QueryBuilder<AbcdefEntity> qb = dao.queryBuilder();
        qb.whereOr(Properties.A.eq(201), //
                qb.and(Properties.B.gt(402), Properties.C.lt(703)));
        List<AbcdefEntity> result = qb.orderAsc(Properties.A).list();
        assertEquals(3, result.size());

        assertEquals(201, (int) result.get(0).getA());
        assertEquals(501, (int) result.get(1).getA());
        assertEquals(601, (int) result.get(2).getA());
    }

}
