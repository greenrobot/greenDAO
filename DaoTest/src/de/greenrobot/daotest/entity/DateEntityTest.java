package de.greenrobot.daotest.entity;

import java.util.Date;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.dao.test.DateEntity;
import de.greenrobot.dao.test.DateEntityDao;


public class DateEntityTest extends AbstractDaoTestLongPk<DateEntityDao, DateEntity> {

    public DateEntityTest() {
        super(DateEntityDao.class);
    }

    @Override
    protected DateEntity createEntity(Long key) {
        DateEntity entity = new DateEntity();
        entity.setId(key);
        entity.setDateNotNull(new Date());
        return entity;
    }
    
    public void testValues() {
        DateEntity entity = createEntity(1l);
        dao.insert(entity);
        
        DateEntity reloaded = dao.load(entity.getId());
        assertNull(reloaded.getDate());
        assertNotNull(reloaded.getDateNotNull());
        assertEquals(entity.getDateNotNull(), reloaded.getDateNotNull());
    }

    public void testValues2() {
        DateEntity entity = createEntity(1l);
        long t1=32479875;
        long t2=976345942443435235l;
        entity.setDate(new Date(t1));
        entity.setDateNotNull(new Date(t2));
        dao.insert(entity);
        
        DateEntity reloaded = dao.load(entity.getId());
        assertNotNull(reloaded.getDate());
        assertNotNull(reloaded.getDateNotNull());
        assertEquals(t1, reloaded.getDate().getTime());
        assertEquals(t2, reloaded.getDateNotNull().getTime());
    }

}
