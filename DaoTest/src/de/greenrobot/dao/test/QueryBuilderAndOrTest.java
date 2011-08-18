package de.greenrobot.dao.test;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.Property;
import de.greenrobot.dao.QueryBuilder;
import de.greenrobot.dao.test.AbcdefEntityDao.Properties;

public class QueryBuilderAndOrTest extends AbstractDaoTest<AbcdefEntityDao, AbcdefEntity, Long> {

    public QueryBuilderAndOrTest() {
        super(AbcdefEntityDao.class);
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

        List<AbcdefEntity> result = dao.queryBuilder().eq(Properties.A, 1).orderAsc(Properties.A).build().list();
        assertEquals(1, result.size());

        AbcdefEntity resultEntity = result.get(0);
        assertEquals(1, (int) resultEntity.getA());
    }

    @SuppressWarnings("unchecked")
    public void testOr() {
        insert(3);

        QueryBuilder<AbcdefEntity> qb = dao.queryBuilder();
        qb.or(qb.eq(Properties.A, 1), qb.eq(Properties.A, 101));
        List<AbcdefEntity> result = qb.orderAsc(Properties.A).build().list();
        assertEquals(2, result.size());

        assertEquals(1, (int) result.get(0).getA());
        assertEquals(101, (int) result.get(1).getA());
    }

    @SuppressWarnings("unchecked")
    public void testOr3() {
        insert(5);

        QueryBuilder<AbcdefEntity> qb = dao.queryBuilder();
        qb.or(qb.eq(Properties.A, 1), qb.eq(Properties.A, 101), qb.eq(Properties.B, 302));
        List<AbcdefEntity> result = qb.orderAsc(Properties.A).build().list();
        assertEquals(3, result.size());

        assertEquals(1, (int) result.get(0).getA());
        assertEquals(101, (int) result.get(1).getA());
        assertEquals(301, (int) result.get(2).getA());

    }

    @SuppressWarnings("unchecked")
    public void testOrNested() {
        insert(10);

        QueryBuilder<AbcdefEntity> qb = dao.queryBuilder();
        qb.or(qb.eq(Properties.A, 101), //
                qb.or(qb.eq(Properties.B, 302), qb.eq(Properties.C, 603)));
        List<AbcdefEntity> result = qb.orderAsc(Properties.A).build().list();
        assertEquals(3, result.size());

        assertEquals(101, (int) result.get(0).getA());
        assertEquals(301, (int) result.get(1).getA());
        assertEquals(601, (int) result.get(2).getA());
    }

    @SuppressWarnings("unchecked")
    public void testOrNestedNested() {
        insert(10);

        QueryBuilder<AbcdefEntity> qb = dao.queryBuilder();
        qb.or(qb.eq(Properties.A, 101), //
                qb.or(qb.eq(Properties.B, 302), //
                        qb.or(qb.eq(Properties.C, 503), qb.eq(Properties.D, 804))));
        List<AbcdefEntity> result = qb.orderAsc(Properties.A).build().list();
        assertEquals(4, result.size());

        assertEquals(101, (int) result.get(0).getA());
        assertEquals(301, (int) result.get(1).getA());
        assertEquals(501, (int) result.get(2).getA());
        assertEquals(801, (int) result.get(3).getA());
    }

    @SuppressWarnings("unchecked")
    public void testAnd() {
        insert(5);

        QueryBuilder<AbcdefEntity> qb = dao.queryBuilder();
        qb.and(qb.eq(Properties.A, 201), qb.eq(Properties.B, 202));
        List<AbcdefEntity> result = qb.orderAsc(Properties.A).build().list();
        assertEquals(1, result.size());

        assertEquals(201, (int) result.get(0).getA());
    }

    @SuppressWarnings("unchecked")
    public void testOrAnd() {
        insert(10);

        QueryBuilder<AbcdefEntity> qb = dao.queryBuilder();
        qb.or(qb.eq(Properties.A, 201), //
                qb.and(qb.gt(Properties.B, 402), qb.lt(Properties.C, 703)));
        List<AbcdefEntity> result = qb.orderAsc(Properties.A).build().list();
        assertEquals(3, result.size());

        assertEquals(201, (int) result.get(0).getA());
        assertEquals(501, (int) result.get(1).getA());
        assertEquals(601, (int) result.get(2).getA());
    }

}
