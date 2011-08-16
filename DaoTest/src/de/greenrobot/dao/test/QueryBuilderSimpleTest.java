package de.greenrobot.dao.test;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.test.TestEntityDao.Properties;

public class QueryBuilderSimpleTest extends TestEntityTestBase {

    public void testEqInteger() {
        ArrayList<TestEntity> inserted = insert(3);
        int value = getSimpleInteger(1);
        
        List<TestEntity> result = dao.queryBuilder().eq(Properties.SimpleInteger, value).build().list();
        assertEquals(1, result.size());
        
        TestEntity resultEntity = result.get(0);
        assertEquals(value, (int) resultEntity.getSimpleInteger());
        assertEquals(inserted.get(1).getId(), resultEntity.getId());
    }

}
