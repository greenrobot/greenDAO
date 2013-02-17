/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.greenrobot.dao.test;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoLog;

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

    /** {@inheritDoc} */
    protected Long createRandomPk() {
        return random.nextLong();
    }
    
    public void testAssignPk() {
        if (daoAccess.isEntityUpdateable()) {
            T entity1 = createEntity(null);
            if (entity1 != null) {
                T entity2 = createEntity(null);

                dao.insert(entity1);
                dao.insert(entity2);

                Long pk1 = daoAccess.getKey(entity1);
                assertNotNull(pk1);
                Long pk2 = daoAccess.getKey(entity2);
                assertNotNull(pk2);

                assertFalse(pk1.equals(pk2));

                assertNotNull(dao.load(pk1));
                assertNotNull(dao.load(pk2));
            } else {
                DaoLog.d("Skipping testAssignPk for " + daoClass + " (createEntity returned null for null key)");
            }
        } else {
            DaoLog.d("Skipping testAssignPk for not updateable " + daoClass);
        }
    }


}