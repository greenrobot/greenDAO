package de.greenrobot.daotest.entity;

import java.util.List;

import de.greenrobot.dao.test.AbstractDaoSessionTest;
import de.greenrobot.daotest.DaoMaster;
import de.greenrobot.daotest.DaoSession;
import de.greenrobot.daotest.ToManyEntity;
import de.greenrobot.daotest.ToManyTargetEntity;
import de.greenrobot.daotest.ToManyTargetEntityDao;

public class ToManyEntityTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    public ToManyEntityTest() {
        super(DaoMaster.class);
    }

    public void testToManyBasics() {
        int count = 3;
        ToManyTargetEntity[] targetEntities = prepareToMany(1, count);

        ToManyEntity testEntity = daoSession.load(ToManyEntity.class, 1l);
        List<ToManyTargetEntity> resolvedToMany = testEntity.getToManyTargetEntity();
        assertEquals(count, resolvedToMany.size());

        ToManyTargetEntity[] ordered = new ToManyTargetEntity[count];
        for (ToManyTargetEntity resolvedEntity : resolvedToMany) {
            ordered[resolvedEntity.getId().intValue() - 1] = resolvedEntity;
        }
        for (int i = 0; i < count; i++) {
            assertEquals(i+1, ordered[i].getId().intValue());
            assertSame(targetEntities[i], ordered[i]);
        }
    }

    private ToManyTargetEntity[] prepareToMany(long id, int count) {
        ToManyEntity entity = new ToManyEntity(id);
        daoSession.insert(entity);
        ToManyTargetEntity[] targetEntities = new ToManyTargetEntity[count];
        for (int i = 0; i < count; i++) {
            ToManyTargetEntity target = new ToManyTargetEntity((long) i + 1);
            target.setToManyId(id);
            targetEntities[i] = target;
        }

        ToManyTargetEntityDao targetDao = (ToManyTargetEntityDao) daoSession.getDao(ToManyTargetEntity.class);
        targetDao.insertInTx(targetEntities);
        return targetEntities;
    }
}
