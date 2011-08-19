package de.greenrobot.daotest;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.QueryBuilder;
import de.greenrobot.dao.test.AbcdefEntity;
import de.greenrobot.dao.test.AbcdefEntityDao;
import de.greenrobot.dao.test.AbstractDaoTest;
import de.greenrobot.dao.test.AbcdefEntityDao.Properties;

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
