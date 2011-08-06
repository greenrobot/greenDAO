package de.greenrobot.dao;

/** Potential future use ;) . */
public class ActiveEntity {
    private AbstractDaoMaster daoMaster;

    protected AbstractDaoMaster getDaoMaster() {
        return daoMaster;
    }

    /* package */ void setDaoMaster(AbstractDaoMaster daoMaster) {
        this.daoMaster = daoMaster;
    }

}
