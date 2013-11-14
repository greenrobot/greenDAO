package de.greenrobot.daotest;

import android.app.Application;
import de.greenrobot.dao.test.DbTest;
import junit.framework.AssertionFailedError;

public class DbTestTest extends DbTest {
    public static class MyApp extends Application {
        static int onCreateCounterStatic;
        static int onTerminateCounterStatic;

        int onCreateCounter;
        int onTerminateCounter;

        @Override
        public void onCreate() {
            super.onCreate();
            onCreateCounter++;
            onCreateCounterStatic++;
        }

        @Override
        public void onTerminate() {
            super.onTerminate();
            onTerminateCounterStatic++;
            onTerminateCounter++;
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MyApp.onCreateCounterStatic = 0;
        MyApp.onTerminateCounterStatic = 0;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        assertEquals(MyApp.onCreateCounterStatic, MyApp.onTerminateCounterStatic);
    }

    public void testCreateApplication() {
        MyApp app = createApplication(MyApp.class);
        assertNotNull(app);

        assertEquals(1, app.onCreateCounter);
        assertEquals(1, MyApp.onCreateCounterStatic);
        assertEquals(0, app.onTerminateCounter);
        assertEquals(0, MyApp.onTerminateCounterStatic);
    }

    public void testTerminateApplication() {
        MyApp app = createApplication(MyApp.class);
        terminateApplication();

        assertEquals(1, app.onCreateCounter);
        assertEquals(1, MyApp.onCreateCounterStatic);
        assertEquals(1, app.onTerminateCounter);
        assertEquals(1, MyApp.onTerminateCounterStatic);
    }

    public void testGetApplicationBeforeCreate() {
        try {
            getApplication();
            fail("Should have thrown");
        } catch (AssertionFailedError e) {
            // Expected
        }
    }

    public void testGetApplication() {
        try {
            getApplication();
            fail("Should have thrown");
        } catch (AssertionFailedError e) {
            // Expected
        }

        MyApp app = createApplication(MyApp.class);
        MyApp app2 = getApplication();
        assertSame(app, app2);
    }

    public void testGetApplicationAfterTerminate() {
        MyApp app = createApplication(MyApp.class);
        terminateApplication();
        try {
            getApplication();
            fail("Should have thrown");
        } catch (AssertionFailedError e) {
            // Expected
        }
    }


    public void testMultipleApplications() {
        MyApp app1 = createApplication(MyApp.class);
        terminateApplication();

        MyApp app2 = createApplication(MyApp.class);
        assertNotSame(app2, app1);

        MyApp app = getApplication();
        assertSame(app2, app);

        assertEquals(1, app1.onCreateCounter);
        assertEquals(1, app1.onTerminateCounter);
        assertEquals(1, app2.onCreateCounter);
        assertEquals(0, app2.onTerminateCounter);

        assertEquals(2, MyApp.onCreateCounterStatic);
        assertEquals(1, MyApp.onTerminateCounterStatic);
    }


}