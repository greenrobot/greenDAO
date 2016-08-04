/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * This file is part of greenDAO Generator.
 *
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.greenrobot.greendao.daotest.async;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.test.AbstractDaoSessionTest;
import org.greenrobot.greendao.daotest.DaoMaster;
import org.greenrobot.greendao.daotest.DaoSession;

public abstract class AbstractAsyncTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> implements
        AsyncOperationListener {

    protected AsyncSession asyncSession;
    protected List<AsyncOperation> completedOperations;

    public AbstractAsyncTest() {
        super(DaoMaster.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        asyncSession = daoSession.startAsyncSession();
        asyncSession.setListener(this);
        completedOperations = new CopyOnWriteArrayList<AsyncOperation>();
    }

    public void assertWaitForCompletion1Sec() {
        assertTrue(asyncSession.waitForCompletion(1000));
        assertTrue(asyncSession.isCompleted());
    }

    @Override
    public void onAsyncOperationCompleted(AsyncOperation operation) {
        completedOperations.add(operation);
    }

    protected void assertSingleOperationCompleted(AsyncOperation operation) {
        assertSame(operation, completedOperations.get(0));
        assertEquals(1, completedOperations.size());
        assertTrue(operation.isCompleted());
    }

}
