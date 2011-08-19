package de.greenrobot.daotest;

import de.greenrobot.dao.IdentityScope;
import de.greenrobot.dao.test.SimpleEntityNotNull;

public class PerformanceTestNotNullIdentityScope extends PerformanceTestNotNull {

    @Override
    protected void setUp() {
        IdentityScope<Long, SimpleEntityNotNull> identityScope = new IdentityScope<Long, SimpleEntityNotNull>();
        setIdentityScopeBeforeSetUp(identityScope);
        super.setUp();
    }

}
