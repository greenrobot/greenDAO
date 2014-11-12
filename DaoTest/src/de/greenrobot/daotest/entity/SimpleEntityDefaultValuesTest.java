package de.greenrobot.daotest.entity;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.SimpleEntityDefaultValues;
import de.greenrobot.daotest.SimpleEntityDefaultValuesDao;

public class SimpleEntityDefaultValuesTest extends AbstractDaoTestLongPk<SimpleEntityDefaultValuesDao, SimpleEntityDefaultValues> {

    public SimpleEntityDefaultValuesTest() {
        super(SimpleEntityDefaultValuesDao.class);
    }

    @Override
    protected SimpleEntityDefaultValues createEntity(Long key) {
         return SimpleEntityDefaultValuesHelper.createEntity(key);
    }

    public void testValues() {
        SimpleEntityDefaultValues entity = createEntity(1l);
        dao.insert(entity);
        SimpleEntityDefaultValues reloaded = dao.load(1l);
        assertEqualProperties(entity, reloaded);
        assertEqualDefaults(entity, reloaded);
        System.out.println("HELLO?");
    }

    protected static void assertEqualDefaults(SimpleEntityDefaultValues entity,
                                                SimpleEntityDefaultValues reloaded) {
        assertNotSame(reloaded, entity);

        assertEquals(entity.getId(), reloaded.getId());
        assertEquals(entity.getSimpleBooleanTrue(), SimpleEntityDefaultValues.getDefaultSimpleBooleanTrue());
        assertEquals(entity.getSimpleByteMin(), SimpleEntityDefaultValues.getDefaultSimpleByteMin());
        assertEquals(entity.getSimpleByteMax(), SimpleEntityDefaultValues.getDefaultSimpleByteMax());
        assertEquals(entity.getSimpleShortMin(), SimpleEntityDefaultValues.getDefaultSimpleShortMin());
        assertEquals(entity.getSimpleShortMax(), SimpleEntityDefaultValues.getDefaultSimpleShortMax());
        assertEquals(entity.getSimpleIntMin(), SimpleEntityDefaultValues.getDefaultSimpleIntMin());
        assertEquals(entity.getSimpleIntMax(), SimpleEntityDefaultValues.getDefaultSimpleIntMax());
        assertEquals(entity.getSimpleLongMin(), SimpleEntityDefaultValues.getDefaultSimpleLongMin());
        assertEquals(entity.getSimpleLongMax(), SimpleEntityDefaultValues.getDefaultSimpleLongMax());
        assertEquals(entity.getSimpleFloatMin(), SimpleEntityDefaultValues.getDefaultSimpleFloatMin());
        assertEquals(entity.getSimpleFloatMax(), SimpleEntityDefaultValues.getDefaultSimpleFloatMax());
        assertEquals(entity.getSimpleDoubleMin(), SimpleEntityDefaultValues.getDefaultSimpleDoubleMin());
        assertEquals(entity.getSimpleDoubleMax(), SimpleEntityDefaultValues.getDefaultSimpleDoubleMax());
        assertEquals(entity.getSimpleString(), SimpleEntityDefaultValues.getDefaultSimpleString());
        assertEquals(entity.getSimpleStringNotNull(), SimpleEntityDefaultValues.getDefaultSimpleStringNotNull());
        assertEquals(entity.getSimpleDate(), SimpleEntityDefaultValues.getDefaultSimpleDate());
        assertEquals(entity.getSimpleDateNotNull(), SimpleEntityDefaultValues.getDefaultSimpleDateNotNull());

    }

    protected static void assertEqualProperties(SimpleEntityDefaultValues entity,
                                                SimpleEntityDefaultValues reloaded) {
        assertNotSame(reloaded, entity);

        assertEquals(entity.getId(), reloaded.getId());
        assertEquals(entity.getSimpleBooleanTrue(), reloaded.getSimpleBooleanTrue());
        assertEquals(entity.getSimpleByteMin(), reloaded.getSimpleByteMin());
        assertEquals(entity.getSimpleByteMax(), reloaded.getSimpleByteMax());
        assertEquals(entity.getSimpleShortMin(), reloaded.getSimpleShortMin());
        assertEquals(entity.getSimpleShortMax(), reloaded.getSimpleShortMax());
        assertEquals(entity.getSimpleIntMin(), reloaded.getSimpleIntMin());
        assertEquals(entity.getSimpleIntMax(), reloaded.getSimpleIntMax());
        assertEquals(entity.getSimpleLongMin(), reloaded.getSimpleLongMin());
        assertEquals(entity.getSimpleLongMax(), reloaded.getSimpleLongMax());
        assertEquals(entity.getSimpleFloatMin(), reloaded.getSimpleFloatMin());
        assertEquals(entity.getSimpleFloatMax(), reloaded.getSimpleFloatMax());
        assertEquals(entity.getSimpleDoubleMin(), reloaded.getSimpleDoubleMin());
        assertEquals(entity.getSimpleDoubleMax(), reloaded.getSimpleDoubleMax());
        assertEquals(entity.getSimpleString(), reloaded.getSimpleString());
        assertEquals(entity.getSimpleStringNotNull(), reloaded.getSimpleStringNotNull());
        assertEquals(entity.getSimpleDate(), reloaded.getSimpleDate());
        assertEquals(entity.getSimpleDateNotNull(), reloaded.getSimpleDateNotNull());
    }

}
