package de.greenrobot.dao.test;

import de.greenrobot.dao.test.DaoMaster;

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * Entity mapped to table RELATION_ENTITY (schema version 1).
*/
public class RelationEntity {

    private Long id; 
    private Long parentId; 
    private Long testId; 
    private String simpleString; 

    /** Used to resolve relations */
    private DaoMaster daoMaster;

    private RelationEntity parent;
    private boolean parent__resolved;

    private TestEntity testEntity;
    private boolean testEntity__resolved;

    public RelationEntity() {
    }

    public RelationEntity(Long id) {
        this.id = id;
    }

    public RelationEntity(Long id, Long parentId, Long testId, String simpleString) {
        this.id = id;
        this.parentId = parentId;
        this.testId = testId;
        this.simpleString = simpleString;
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

    public String getSimpleString() {
        return simpleString;
    } 

    public void setSimpleString(String simpleString) {
        this.simpleString = simpleString;
    } 

    /** To-one relationship, resolved on first access. */ 
    public RelationEntity getParent() {
        if(!parent__resolved) {
            RelationEntityDao dao = daoMaster.getRelationEntityDao();
             parent = dao.load(parentId);
             parent__resolved = true;
        }
        return parent;
    } 

    public void setParent(RelationEntity parent) {
        this.parent = parent;
        parentId = parent == null ? null : parent.getId();
        parent__resolved = true;
    } 

    /** To-one relationship, resolved on first access. */ 
    public TestEntity getTestEntity() {
        if(!testEntity__resolved) {
            TestEntityDao dao = daoMaster.getTestEntityDao();
             testEntity = dao.load(testId);
             testEntity__resolved = true;
        }
        return testEntity;
    } 

    public void setTestEntity(TestEntity testEntity) {
        this.testEntity = testEntity;
        testId = testEntity == null ? null : testEntity.getId();
        testEntity__resolved = true;
    } 


}
