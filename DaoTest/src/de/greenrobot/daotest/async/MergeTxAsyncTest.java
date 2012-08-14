package de.greenrobot.daotest.async;

import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.daotest.SimpleEntity;

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
