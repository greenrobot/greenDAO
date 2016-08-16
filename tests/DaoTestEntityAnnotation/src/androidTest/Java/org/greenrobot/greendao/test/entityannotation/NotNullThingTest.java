package org.greenrobot.greendao.test.entityannotation;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

public class NotNullThingTest extends AbstractDaoTestLongPk<NotNullThingDao, NotNullThing> {

    public NotNullThingTest() {
        super(NotNullThingDao.class);
    }

    @Override
    protected NotNullThing createEntity(Long key) {
        NotNullThing thing = new NotNullThing();
        thing.setId(key);
        thing.setNotNullBoolean(true);
        thing.setNotNullInteger(42);
        thing.setNullableBoolean(true);
        thing.setNullableInteger(42);
        return thing;
    }

    public void testInsertNotNullProperties() {
        NotNullThing thing = createEntity(1L);
        thing.setNotNullBoolean(null);
        thing.setNotNullInteger(null);
        try {
            dao.insert(thing);
            fail();
        } catch (NullPointerException ignored) {
        }
    }

    public void testInsertNullableProperties() {
        NotNullThing thing = createEntity(1L);
        thing.setNullableBoolean(null);
        thing.setNullableInteger(null);
        dao.insert(thing);

        loadAndAssertNullableProperties(thing);
    }

    public void testUpdateNotNullProperties() {
        NotNullThing thing = insertEntity();

        thing.setNotNullBoolean(null);
        thing.setNotNullInteger(null);
        try {
            dao.update(thing);
            fail();
        } catch (NullPointerException ignored) {
        }
    }

    public void testUpdateNullableProperties() {
        NotNullThing thing = insertEntity();

        thing.setNullableBoolean(null);
        thing.setNullableInteger(null);
        dao.update(thing);

        loadAndAssertNullableProperties(thing);
    }

    private NotNullThing insertEntity() {
        NotNullThing thing = createEntity(1L);
        dao.insert(thing);
        return thing;
    }

    private void loadAndAssertNullableProperties(NotNullThing thing) {
        NotNullThing loaded = dao.load(thing.getId());
        assertNull(loaded.getNullableBoolean());
        assertNull(loaded.getNullableInteger());
    }

}
