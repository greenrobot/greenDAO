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
package de.greenrobot.daotest2.entity;

import android.os.Build;
import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest2.KeepEntity;
import de.greenrobot.daotest2.dao.KeepEntityDao;

public class KeepEntityTest extends AbstractDaoTestLongPk<KeepEntityDao, KeepEntity> {

    public KeepEntityTest() {
        super(KeepEntityDao.class);
    }

    @Override
    protected KeepEntity createEntity(Long key) {
        KeepEntity entity = new KeepEntity();
        entity.setId(key);
        return entity;
    }

    public void testKeepSectionAvailable() {
        KeepEntity keepEntity = new KeepEntity(42l);
        assertEquals("KeepEntity ID=42 (extra="+Build.VERSION.SDK+")", keepEntity.toString());
    }

}
