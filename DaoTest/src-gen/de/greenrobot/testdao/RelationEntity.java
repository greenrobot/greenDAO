package de.greenrobot.testdao;

import android.util.Log;
import de.greenrobot.testdao.DaoMaster;

// THIS CODE IS GENERATED, DO NOT EDIT.
/**
 * Entity mapped to table RELATION_ENTITY (schema version 1).
 */
public class RelationEntity {

    private Long id;
    private Long parentId;
    private Long testId;

    /** Used to resolve relations */
    private DaoMaster daoMaster;

    private RelationEntity relationEntity;
    private boolean relationEntity__resolved;

    private TestEntity testEntity;
    private boolean testEntity__resolved;

    public RelationEntity() {
    }

    public RelationEntity(Long id) {
        this.id = id;
    }

    public RelationEntity(Long id, Long parentId, Long testId) {
        this.id = id;
        this.parentId = parentId;
        this.testId = testId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoMaster(DaoMaster daoMaster) {
        this.daoMaster = daoMaster;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    /** To-one relationship, resolved on first access. */
    public RelationEntity getRelationEntity() {
        if (!relationEntity__resolved) {
            RelationEntityDao dao = daoMaster.getRelationEntityDao();
            relationEntity = dao.load(parentId);
            relationEntity__resolved = true;
        }
        return relationEntity;
    }

    /** To-one relationship, resolved on first access. */
    public TestEntity getTestEntity() {
        if (!testEntity__resolved) {
            long start = System.nanoTime();
            long startM = System.currentTimeMillis();

            TestEntityDao dao = daoMaster.getTestEntityDao();
            testEntity = dao.load(testId);
            testEntity__resolved = true;

            long time = System.nanoTime() - start;
            long timeM = System.currentTimeMillis() - startM;
            Log.d("XXXXX", ""+time+"ns "+timeM+"ms ");

             start = System.nanoTime();

             dao = daoMaster.getTestEntityDao();
            testEntity = dao.load(testId);
            testEntity__resolved = true;

             time = System.nanoTime() - start;
            Log.d("XXXXX", ">>"+time+"ns");

        }
        return testEntity;
    }

}
