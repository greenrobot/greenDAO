package de.greenrobot.dao.test;

import java.util.Arrays;

public class SimpleEntityNotNullTest extends AbstractDaoTestLongPk<SimpleEntityNotNullDao, SimpleEntityNotNull> {

    public SimpleEntityNotNullTest() {
        super(SimpleEntityNotNullDao.class);
    }

    @Override
    protected SimpleEntityNotNull createEntity(Long key) {
        return SimpleEntityNotNullHelper.createEntity(key);
    }

    public void testValues() {
        SimpleEntityNotNull entity = createEntity(1l);
        dao.insert(entity);
        SimpleEntityNotNull reloaded = dao.load(1l);
        assertEqualProperties(entity, reloaded);
    }

    protected static void assertEqualProperties(SimpleEntityNotNull entity, SimpleEntityNotNull reloaded) {
        assertNotSame(entity, reloaded);

        assertEquals(entity.getId(), reloaded.getId());
        assertEquals(entity.getSimpleBoolean(), reloaded.getSimpleBoolean());
        assertEquals(entity.getSimpleDouble(), reloaded.getSimpleDouble());
        assertEquals(entity.getSimpleFloat(), reloaded.getSimpleFloat());
        assertEquals(entity.getSimpleLong(), reloaded.getSimpleLong());
        assertEquals(entity.getSimpleByte(), reloaded.getSimpleByte());
        assertEquals(entity.getSimpleInt(), reloaded.getSimpleInt());
        assertEquals(entity.getSimpleShort(), reloaded.getSimpleShort());
        assertEquals(entity.getSimpleBoolean(), reloaded.getSimpleBoolean());
        assertEquals(entity.getSimpleString(), reloaded.getSimpleString());
        assertTrue(Arrays.equals(entity.getSimpleByteArray(), reloaded.getSimpleByteArray()));
    }

    public void testUpdateValues() {
        SimpleEntityNotNull entity = createEntity(1l);
        dao.insert(entity);
        entity = dao.load(1l);

        entity.setSimpleBoolean(false);
        entity.setSimpleByte(Byte.MIN_VALUE);
        entity.setSimpleShort(Short.MIN_VALUE);
        entity.setSimpleInt(Integer.MIN_VALUE);
        entity.setSimpleLong(Long.MIN_VALUE);
        entity.setSimpleFloat(Float.MIN_VALUE);
        entity.setSimpleDouble(Double.MIN_VALUE);
        entity.setSimpleString("greenDAO");
        byte[] bytes = { -1, 0, 1 };
        entity.setSimpleByteArray(bytes);
        dao.update(entity);

        SimpleEntityNotNull reloaded = dao.load(1l);
        assertEqualProperties(entity, reloaded);
    }

}
