package de.greenrobot.daotest.async;

import de.greenrobot.dao.AsyncOperation;
import de.greenrobot.daotest.SimpleEntity;

public class BasicAsyncTest extends AbstractAsyncTest {

    public void testWaitForCompletionNoOps() {
        assertTrue(asyncSession.isCompleted());
        assertTrue(asyncSession.waitForCompletion(1));
        asyncSession.waitForCompletion();
    }

    public void testAsyncInsert() {
        SimpleEntity entity = new SimpleEntity();
        entity.setSimpleString("heho");
        AsyncOperation operation = asyncSession.insert(entity);
        assertWaitForCompletion1Sec();
        SimpleEntity entity2 = daoSession.load(SimpleEntity.class, entity.getId());
        assertNotNull(entity2);
        assertEquals("heho", entity2.getSimpleString());
        assertSame(operation, completedOperations.get(0));
        assertEquals(1, completedOperations.size());
        assertTrue(operation.isCompleted());
    }

}
