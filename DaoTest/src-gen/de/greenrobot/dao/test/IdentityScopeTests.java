package de.greenrobot.dao.test;

import android.test.suitebuilder.TestSuiteBuilder;
import de.greenrobot.dao.IdentityScope;
import de.greenrobot.daotest.entity.DateEntityTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class IdentityScopeTests {

    public static Test suite() {
//        new TestSuiteBuilder(IdentityScopeTests.class).
        TestSuite suite = new TestSuite();
        DateEntityTest dateEntityTest = new DateEntityTest();
        dateEntityTest.setIdentityScopeBeforeSetUp(new IdentityScope<Long, DateEntity>());
        suite.addTest(dateEntityTest);
        return suite;
    }
}
