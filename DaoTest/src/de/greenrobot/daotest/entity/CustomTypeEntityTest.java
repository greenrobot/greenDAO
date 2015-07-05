package de.greenrobot.daotest.entity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.CustomTypeEntity;
import de.greenrobot.daotest.CustomTypeEntityDao;
import de.greenrobot.daotest.customtype.MyTimestamp;

import java.util.List;

public class CustomTypeEntityTest extends AbstractDaoTestLongPk<CustomTypeEntityDao, CustomTypeEntity> {

    public CustomTypeEntityTest() {
        super(CustomTypeEntityDao.class);
    }

    @Override
    protected CustomTypeEntity createEntity(Long key) {
        CustomTypeEntity entity = new CustomTypeEntity();
        entity.setId(key);
        MyTimestamp myCustomTimestamp = new MyTimestamp();
        myCustomTimestamp.timestamp = System.currentTimeMillis();
        entity.setMyCustomTimestamp(myCustomTimestamp);
        return entity;
    }

    public void testCustomTypeValue() {
        CustomTypeEntity entity = createEntityWithRandomPk();
        long timestamp = entity.getMyCustomTimestamp().timestamp;
        dao.insert(entity);

        List<CustomTypeEntity> all = dao.loadAll();
        assertEquals(1, all.size());
        assertEquals(timestamp, all.get(0).getMyCustomTimestamp().timestamp);
    }

    public void testCustomTypeValueNull() {
        CustomTypeEntity entity = createEntityWithRandomPk();
        entity.setMyCustomTimestamp(null);
        dao.insert(entity);

        List<CustomTypeEntity> all = dao.loadAll();
        assertEquals(1, all.size());
        assertNull(all.get(0).getMyCustomTimestamp());
    }

}
