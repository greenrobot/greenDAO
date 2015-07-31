package de.greenrobot.daotest;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import de.greenrobot.daotest.SimpleEntity;
import de.greenrobot.daotest.SimpleEntityNotNull;
import de.greenrobot.daotest.TestEntity;
import de.greenrobot.daotest.RelationEntity;
import de.greenrobot.daotest.DateEntity;
import de.greenrobot.daotest.SpecialNamesEntity;
import de.greenrobot.daotest.AbcdefEntity;
import de.greenrobot.daotest.ToManyTargetEntity;
import de.greenrobot.daotest.ToManyEntity;
import de.greenrobot.daotest.JoinManyToDateEntity;
import de.greenrobot.daotest.TreeEntity;
import de.greenrobot.daotest.AnActiveEntity;
import de.greenrobot.daotest.ExtendsImplementsEntity;
import de.greenrobot.daotest.StringKeyValueEntity;
import de.greenrobot.daotest.AutoincrementEntity;
import de.greenrobot.daotest.SqliteMaster;
import de.greenrobot.daotest.CustomTypeEntity;

import de.greenrobot.daotest.SimpleEntityDao;
import de.greenrobot.daotest.SimpleEntityNotNullDao;
import de.greenrobot.daotest.TestEntityDao;
import de.greenrobot.daotest.RelationEntityDao;
import de.greenrobot.daotest.DateEntityDao;
import de.greenrobot.daotest.SpecialNamesEntityDao;
import de.greenrobot.daotest.AbcdefEntityDao;
import de.greenrobot.daotest.ToManyTargetEntityDao;
import de.greenrobot.daotest.ToManyEntityDao;
import de.greenrobot.daotest.JoinManyToDateEntityDao;
import de.greenrobot.daotest.TreeEntityDao;
import de.greenrobot.daotest.AnActiveEntityDao;
import de.greenrobot.daotest.ExtendsImplementsEntityDao;
import de.greenrobot.daotest.StringKeyValueEntityDao;
import de.greenrobot.daotest.AutoincrementEntityDao;
import de.greenrobot.daotest.SqliteMasterDao;
import de.greenrobot.daotest.CustomTypeEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig simpleEntityDaoConfig;
    private final DaoConfig simpleEntityNotNullDaoConfig;
    private final DaoConfig testEntityDaoConfig;
    private final DaoConfig relationEntityDaoConfig;
    private final DaoConfig dateEntityDaoConfig;
    private final DaoConfig specialNamesEntityDaoConfig;
    private final DaoConfig abcdefEntityDaoConfig;
    private final DaoConfig toManyTargetEntityDaoConfig;
    private final DaoConfig toManyEntityDaoConfig;
    private final DaoConfig joinManyToDateEntityDaoConfig;
    private final DaoConfig treeEntityDaoConfig;
    private final DaoConfig anActiveEntityDaoConfig;
    private final DaoConfig extendsImplementsEntityDaoConfig;
    private final DaoConfig stringKeyValueEntityDaoConfig;
    private final DaoConfig autoincrementEntityDaoConfig;
    private final DaoConfig sqliteMasterDaoConfig;
    private final DaoConfig customTypeEntityDaoConfig;

    private final SimpleEntityDao simpleEntityDao;
    private final SimpleEntityNotNullDao simpleEntityNotNullDao;
    private final TestEntityDao testEntityDao;
    private final RelationEntityDao relationEntityDao;
    private final DateEntityDao dateEntityDao;
    private final SpecialNamesEntityDao specialNamesEntityDao;
    private final AbcdefEntityDao abcdefEntityDao;
    private final ToManyTargetEntityDao toManyTargetEntityDao;
    private final ToManyEntityDao toManyEntityDao;
    private final JoinManyToDateEntityDao joinManyToDateEntityDao;
    private final TreeEntityDao treeEntityDao;
    private final AnActiveEntityDao anActiveEntityDao;
    private final ExtendsImplementsEntityDao extendsImplementsEntityDao;
    private final StringKeyValueEntityDao stringKeyValueEntityDao;
    private final AutoincrementEntityDao autoincrementEntityDao;
    private final SqliteMasterDao sqliteMasterDao;
    private final CustomTypeEntityDao customTypeEntityDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        simpleEntityDaoConfig = daoConfigMap.get(SimpleEntityDao.class).clone();
        simpleEntityDaoConfig.initIdentityScope(type);

        simpleEntityNotNullDaoConfig = daoConfigMap.get(SimpleEntityNotNullDao.class).clone();
        simpleEntityNotNullDaoConfig.initIdentityScope(type);

        testEntityDaoConfig = daoConfigMap.get(TestEntityDao.class).clone();
        testEntityDaoConfig.initIdentityScope(type);

        relationEntityDaoConfig = daoConfigMap.get(RelationEntityDao.class).clone();
        relationEntityDaoConfig.initIdentityScope(type);

        dateEntityDaoConfig = daoConfigMap.get(DateEntityDao.class).clone();
        dateEntityDaoConfig.initIdentityScope(type);

        specialNamesEntityDaoConfig = daoConfigMap.get(SpecialNamesEntityDao.class).clone();
        specialNamesEntityDaoConfig.initIdentityScope(type);

        abcdefEntityDaoConfig = daoConfigMap.get(AbcdefEntityDao.class).clone();
        abcdefEntityDaoConfig.initIdentityScope(type);

        toManyTargetEntityDaoConfig = daoConfigMap.get(ToManyTargetEntityDao.class).clone();
        toManyTargetEntityDaoConfig.initIdentityScope(type);

        toManyEntityDaoConfig = daoConfigMap.get(ToManyEntityDao.class).clone();
        toManyEntityDaoConfig.initIdentityScope(type);

        joinManyToDateEntityDaoConfig = daoConfigMap.get(JoinManyToDateEntityDao.class).clone();
        joinManyToDateEntityDaoConfig.initIdentityScope(type);

        treeEntityDaoConfig = daoConfigMap.get(TreeEntityDao.class).clone();
        treeEntityDaoConfig.initIdentityScope(type);

        anActiveEntityDaoConfig = daoConfigMap.get(AnActiveEntityDao.class).clone();
        anActiveEntityDaoConfig.initIdentityScope(type);

        extendsImplementsEntityDaoConfig = daoConfigMap.get(ExtendsImplementsEntityDao.class).clone();
        extendsImplementsEntityDaoConfig.initIdentityScope(type);

        stringKeyValueEntityDaoConfig = daoConfigMap.get(StringKeyValueEntityDao.class).clone();
        stringKeyValueEntityDaoConfig.initIdentityScope(type);

        autoincrementEntityDaoConfig = daoConfigMap.get(AutoincrementEntityDao.class).clone();
        autoincrementEntityDaoConfig.initIdentityScope(type);

        sqliteMasterDaoConfig = daoConfigMap.get(SqliteMasterDao.class).clone();
        sqliteMasterDaoConfig.initIdentityScope(type);

        customTypeEntityDaoConfig = daoConfigMap.get(CustomTypeEntityDao.class).clone();
        customTypeEntityDaoConfig.initIdentityScope(type);

        simpleEntityDao = new SimpleEntityDao(simpleEntityDaoConfig, this);
        simpleEntityNotNullDao = new SimpleEntityNotNullDao(simpleEntityNotNullDaoConfig, this);
        testEntityDao = new TestEntityDao(testEntityDaoConfig, this);
        relationEntityDao = new RelationEntityDao(relationEntityDaoConfig, this);
        dateEntityDao = new DateEntityDao(dateEntityDaoConfig, this);
        specialNamesEntityDao = new SpecialNamesEntityDao(specialNamesEntityDaoConfig, this);
        abcdefEntityDao = new AbcdefEntityDao(abcdefEntityDaoConfig, this);
        toManyTargetEntityDao = new ToManyTargetEntityDao(toManyTargetEntityDaoConfig, this);
        toManyEntityDao = new ToManyEntityDao(toManyEntityDaoConfig, this);
        joinManyToDateEntityDao = new JoinManyToDateEntityDao(joinManyToDateEntityDaoConfig, this);
        treeEntityDao = new TreeEntityDao(treeEntityDaoConfig, this);
        anActiveEntityDao = new AnActiveEntityDao(anActiveEntityDaoConfig, this);
        extendsImplementsEntityDao = new ExtendsImplementsEntityDao(extendsImplementsEntityDaoConfig, this);
        stringKeyValueEntityDao = new StringKeyValueEntityDao(stringKeyValueEntityDaoConfig, this);
        autoincrementEntityDao = new AutoincrementEntityDao(autoincrementEntityDaoConfig, this);
        sqliteMasterDao = new SqliteMasterDao(sqliteMasterDaoConfig, this);
        customTypeEntityDao = new CustomTypeEntityDao(customTypeEntityDaoConfig, this);

        registerDao(SimpleEntity.class, simpleEntityDao);
        registerDao(SimpleEntityNotNull.class, simpleEntityNotNullDao);
        registerDao(TestEntity.class, testEntityDao);
        registerDao(RelationEntity.class, relationEntityDao);
        registerDao(DateEntity.class, dateEntityDao);
        registerDao(SpecialNamesEntity.class, specialNamesEntityDao);
        registerDao(AbcdefEntity.class, abcdefEntityDao);
        registerDao(ToManyTargetEntity.class, toManyTargetEntityDao);
        registerDao(ToManyEntity.class, toManyEntityDao);
        registerDao(JoinManyToDateEntity.class, joinManyToDateEntityDao);
        registerDao(TreeEntity.class, treeEntityDao);
        registerDao(AnActiveEntity.class, anActiveEntityDao);
        registerDao(ExtendsImplementsEntity.class, extendsImplementsEntityDao);
        registerDao(StringKeyValueEntity.class, stringKeyValueEntityDao);
        registerDao(AutoincrementEntity.class, autoincrementEntityDao);
        registerDao(SqliteMaster.class, sqliteMasterDao);
        registerDao(CustomTypeEntity.class, customTypeEntityDao);
    }
    
    public void clear() {
        simpleEntityDaoConfig.getIdentityScope().clear();
        simpleEntityNotNullDaoConfig.getIdentityScope().clear();
        testEntityDaoConfig.getIdentityScope().clear();
        relationEntityDaoConfig.getIdentityScope().clear();
        dateEntityDaoConfig.getIdentityScope().clear();
        specialNamesEntityDaoConfig.getIdentityScope().clear();
        abcdefEntityDaoConfig.getIdentityScope().clear();
        toManyTargetEntityDaoConfig.getIdentityScope().clear();
        toManyEntityDaoConfig.getIdentityScope().clear();
        joinManyToDateEntityDaoConfig.getIdentityScope().clear();
        treeEntityDaoConfig.getIdentityScope().clear();
        anActiveEntityDaoConfig.getIdentityScope().clear();
        extendsImplementsEntityDaoConfig.getIdentityScope().clear();
        stringKeyValueEntityDaoConfig.getIdentityScope().clear();
        autoincrementEntityDaoConfig.getIdentityScope().clear();
        sqliteMasterDaoConfig.getIdentityScope().clear();
        customTypeEntityDaoConfig.getIdentityScope().clear();
    }

    public SimpleEntityDao getSimpleEntityDao() {
        return simpleEntityDao;
    }

    public SimpleEntityNotNullDao getSimpleEntityNotNullDao() {
        return simpleEntityNotNullDao;
    }

    public TestEntityDao getTestEntityDao() {
        return testEntityDao;
    }

    public RelationEntityDao getRelationEntityDao() {
        return relationEntityDao;
    }

    public DateEntityDao getDateEntityDao() {
        return dateEntityDao;
    }

    public SpecialNamesEntityDao getSpecialNamesEntityDao() {
        return specialNamesEntityDao;
    }

    public AbcdefEntityDao getAbcdefEntityDao() {
        return abcdefEntityDao;
    }

    public ToManyTargetEntityDao getToManyTargetEntityDao() {
        return toManyTargetEntityDao;
    }

    public ToManyEntityDao getToManyEntityDao() {
        return toManyEntityDao;
    }

    public JoinManyToDateEntityDao getJoinManyToDateEntityDao() {
        return joinManyToDateEntityDao;
    }

    public TreeEntityDao getTreeEntityDao() {
        return treeEntityDao;
    }

    public AnActiveEntityDao getAnActiveEntityDao() {
        return anActiveEntityDao;
    }

    public ExtendsImplementsEntityDao getExtendsImplementsEntityDao() {
        return extendsImplementsEntityDao;
    }

    public StringKeyValueEntityDao getStringKeyValueEntityDao() {
        return stringKeyValueEntityDao;
    }

    public AutoincrementEntityDao getAutoincrementEntityDao() {
        return autoincrementEntityDao;
    }

    public SqliteMasterDao getSqliteMasterDao() {
        return sqliteMasterDao;
    }

    public CustomTypeEntityDao getCustomTypeEntityDao() {
        return customTypeEntityDao;
    }

}
