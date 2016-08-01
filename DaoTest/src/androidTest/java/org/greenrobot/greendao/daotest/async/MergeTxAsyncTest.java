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

import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.daotest.SimpleEntity;

public class MergeTxAsyncTest extends AbstractAsyncTest {

    public void testMergeInsertAndUpdate() {
        SimpleEntity entity = new SimpleEntity();
        entity.setId(42l);
        entity.setSimpleString("heho");
        
        SimpleEntity entity2 = new SimpleEntity();
        entity2.setId(42l);
        entity2.setSimpleString("updated");
        
        AsyncOperation op1 = asyncSession.insert(entity, AsyncOperation.FLAG_MERGE_TX);
        AsyncOperation op2 = asyncSession.update(entity2, AsyncOperation.FLAG_MERGE_TX);
        
        assertWaitForCompletion1Sec();
        daoSession.clear();
        SimpleEntity entity3 = daoSession.load(SimpleEntity.class, 42l);
        assertNotNull(entity3);
        assertEquals(entity2.getSimpleString(), entity3.getSimpleString());
        
        assertEquals(2, op1.getMergedOperationsCount());
        assertEquals(2, op2.getMergedOperationsCount());
    }

}
