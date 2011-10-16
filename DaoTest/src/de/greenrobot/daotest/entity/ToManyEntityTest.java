package de.greenrobot.daotest.entity;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.test.AbstractDaoSessionTest;
import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.DaoMaster;
import de.greenrobot.daotest.DaoSession;
import de.greenrobot.daotest.ToManyEntity;
import de.greenrobot.daotest.ToManyEntityDao;
import de.greenrobot.daotest.ToManyTargetEntity;
import de.greenrobot.daotest.ToManyTargetEntityDao;

public class ToManyEntityTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    public ToManyEntityTest() {
        super(DaoMaster.class);
    }

    public void testToManyBasics() {
        ToManyEntity entity = new ToManyEntity(1l);

        daoSession.insert(entity);

        ToManyTargetEntity target1 = new ToManyTargetEntity(1l);
        ToManyTargetEntity target2 = new ToManyTargetEntity(2l);
        ToManyTargetEntity target3 = new ToManyTargetEntity(3l);

        target1.setToManyId(1l);
        target2.setToManyId(1l);
        target3.setToManyId(1l);

        ToManyTargetEntityDao targetDao = (ToManyTargetEntityDao) daoSession.getDao(ToManyTargetEntity.class);
        targetDao.insertInTx(target1, target2, target3);

        ToManyEntity testEntity = daoSession.load(ToManyEntity.class, 1l);
        List<ToManyTargetEntity> resolvedToMany = testEntity.getToManyTargetEntity();
        assertEquals(3, resolvedToMany.size());
    }
}
