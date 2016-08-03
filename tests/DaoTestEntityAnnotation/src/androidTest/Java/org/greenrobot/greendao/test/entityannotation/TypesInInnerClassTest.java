package org.greenrobot.greendao.test.entityannotation;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;
import org.greenrobot.greendao.test.entityannotation.TypesInInnerClass.MyInnerType;

public class TypesInInnerClassTest extends AbstractDaoTestLongPk<TypesInInnerClassDao, TypesInInnerClass> {

    public TypesInInnerClassTest() {
        super(TypesInInnerClassDao.class);
    }

    @Override
    protected TypesInInnerClass createEntity(Long key) {
        TypesInInnerClass entity = new TypesInInnerClass();
        entity.setId(key);
        entity.setType(new MyInnerType("cafe"));
        return entity;
    }

    public void testType() {
        TypesInInnerClass entity = createEntity(1L);
        dao.insert(entity);
        TypesInInnerClass entity2 = dao.load(1L);
        assertNotSame(entity, entity2);
        assertEquals("cafe", entity2.getType().value);
    }
}
