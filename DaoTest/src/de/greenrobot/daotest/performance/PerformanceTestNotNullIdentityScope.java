package de.greenrobot.daotest.performance;

import de.greenrobot.dao.IdentityScopeLong;
import de.greenrobot.daotest.SimpleEntityNotNull;

public class PerformanceTestNotNullIdentityScope extends PerformanceTestNotNull {

    @Override
    protected void setUp() {
        IdentityScopeLong<SimpleEntityNotNull> identityScope = new IdentityScopeLong< SimpleEntityNotNull>();
        setIdentityScopeBeforeSetUp(identityScope);
        super.setUp();
    }

}
