package de.greenrobot.dao.test;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.test.AbstractDaoTestSinglePk;

/**
 * Base class for DAOs having a long/Long as a PK, which is quite common.
 * 
 * @author Markus
 * 
 * @param <D>
 *            DAO class
 * @param <T>
 *            Entity type of the DAO
 */
public abstract class AbstractDaoTestLongPk<D extends AbstractDao<T, Long>, T> extends AbstractDaoTestSinglePk<D, T, Long> {

    public AbstractDaoTestLongPk(Class<D> daoClass) {
        super(daoClass);
    }

    /** @inheritdoc */
    protected Long createRandomPk() {
        return random.nextLong();
    }

}