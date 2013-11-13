/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * This file is part of greenDAO Generator.
 * 
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.greenrobot.daotest.entity;

import java.util.List;

import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.daotest.RelationEntity;
import de.greenrobot.daotest.RelationEntityDao;

/**
 * @author Markus
 */
public class RelationEntityTestIdentityScope extends RelationEntityTest {

    @Override
    protected void setUp() throws Exception {
        identityScopeTypeForSession = IdentityScopeType.Session;
        super.setUp();
    }

    public void testToOneLoadDeepIdentityScope() {
        RelationEntity entity = insertEntityWithRelations(42l);
        RelationEntity entity2 = insertEntityWithRelations(42l);
        entity = dao.loadDeep(entity.getId());
        entity2 = dao.loadDeep(entity2.getId());
        assertFalse(entity.getId().equals(entity2.getId()));
        assertTestEntity(entity);
        assertTestEntity(entity2);
        assertSame(entity.getTestEntity(), entity2.getTestEntity());
    }

    public void testToQueryDeepIdentityScope() {
        insertEntityWithRelations(42l);
        RelationEntity entity2 = insertEntityWithRelations(42l);
        String columnName = RelationEntityDao.Properties.SimpleString.columnName;
        List<RelationEntity> entityList = dao.queryDeep("WHERE T." + columnName + "=?", "findMe");
        assertEquals(2, entityList.size());
        RelationEntity entity = entityList.get(0);
        assertTestEntity(entity);
        entity2 = entityList.get(1);
        assertTestEntity(entity2);
        assertSame(entity.getTestEntity(), entity2.getTestEntity());
    }

    public void testLoadDeepIdentityScope() {
        RelationEntity entity = insertEntityWithRelations(42l);
        RelationEntity entity2 = dao.loadDeep(entity.getId());
        RelationEntity entity3 = dao.loadDeep(entity.getId());
        assertSame(entity, entity2);
        assertSame(entity, entity3);
        assertTestEntity(entity);
    }

    public void testQueryDeepIdentityScope() {
        RelationEntity entity = insertEntityWithRelations(42l);

        String columnName = RelationEntityDao.Properties.SimpleString.columnName;
        List<RelationEntity> entityList = dao.queryDeep("WHERE T." + columnName + "=?", "findMe");
        RelationEntity entity2 = entityList.get(0);
        entityList = dao.queryDeep("WHERE T." + columnName + "=?", "findMe");
        RelationEntity entity3 = entityList.get(0);

        assertSame(entity, entity2);
        assertSame(entity, entity3);
        assertTestEntity(entity);
    }

}
