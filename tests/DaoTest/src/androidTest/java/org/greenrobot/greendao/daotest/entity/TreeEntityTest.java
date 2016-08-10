/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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

package org.greenrobot.greendao.daotest.entity;

import org.greenrobot.greendao.test.AbstractDaoSessionTest;
import org.greenrobot.greendao.daotest.DaoMaster;
import org.greenrobot.greendao.daotest.DaoSession;
import org.greenrobot.greendao.daotest.TreeEntity;
import org.greenrobot.greendao.daotest.TreeEntityDao;

public class TreeEntityTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    private TreeEntityDao treeEntityDao;

    public TreeEntityTest() {
        super(DaoMaster.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        treeEntityDao = daoSession.getTreeEntityDao();
    }

    public void testNavigateTreeFromLeaf() {
        createTree();
        daoSession.clear();

        TreeEntity child1Child = treeEntityDao.load(101l);
        assertEquals(0, child1Child.getChildren().size());

        TreeEntity child1 = child1Child.getParent();
        assertEquals(11l, child1.getId().longValue());
        assertEquals(1, child1.getChildren().size());
        assertSame(child1Child, child1.getChildren().get(0));

        TreeEntity root = child1.getParent();
        assertEquals(1l, root.getId().longValue());
        assertEquals(2, root.getChildren().size());
        assertNull(root.getParent());
    }

    public void testNavigateTreeFromMiddle() {
        createTree();
        daoSession.clear();

        TreeEntity child1 = treeEntityDao.load(11l);
        assertEquals(1, child1.getChildren().size());
        TreeEntity child1Child = child1.getChildren().get(0);
        assertEquals(101, child1Child.getId().longValue());
        assertEquals(0, child1Child.getChildren().size());

        TreeEntity root = child1.getParent();
        assertEquals(1l, root.getId().longValue());
        assertEquals(2, root.getChildren().size());
        assertNull(root.getParent());
    }

    public void testNavigateTreeFromRoot() {
        createTree();
        daoSession.clear();

        TreeEntity root = treeEntityDao.load(1l);
        assertEquals(2, root.getChildren().size());
        assertNull(root.getParent());

        TreeEntity child1 = root.getChildren().get(0);
        TreeEntity child2 = root.getChildren().get(1);
        if (child1.getId() != 11l) {
            child1 = child2;
            child2 = root.getChildren().get(0);
        }

        assertSame(root, child1.getParent());
        assertEquals(1, child1.getChildren().size());
        TreeEntity child1Child = child1.getChildren().get(0);
        assertEquals(101, child1Child.getId().longValue());
        assertEquals(0, child1Child.getChildren().size());
        
        assertSame(root, child2.getParent());
        assertEquals(0, child2.getChildren().size());
    }

    private void createTree() {
        TreeEntity root = new TreeEntity(1l);
        TreeEntity child1 = new TreeEntity(11l);
        child1.setParent(root);
        TreeEntity child2 = new TreeEntity(12l);
        child2.setParent(root);
        TreeEntity child1Child = new TreeEntity(101l);
        child1Child.setParent(child1);
        treeEntityDao.insertInTx(root, child1, child2, child1Child);
    }

}
