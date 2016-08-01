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

package org.greenrobot.greendao.performance;

import org.greenrobot.greendao.daotest.SimpleEntityNotNull;
import org.greenrobot.greendao.daotest.SimpleEntityNotNullDao;
import org.greenrobot.greendao.daotest.entity.SimpleEntityNotNullHelper;

import java.util.List;

public class PerformanceTestNotNull extends PerformanceTest<SimpleEntityNotNullDao, SimpleEntityNotNull, Long> {

    static long sequence;

    public PerformanceTestNotNull() {
        super(SimpleEntityNotNullDao.class);
    }

    @Override
    protected SimpleEntityNotNull createEntity() {
        return SimpleEntityNotNullHelper.createEntity(sequence++);
    }

    @Override
    protected void accessAll(List<SimpleEntityNotNull> list, String traceName) {
        startClock(traceName);
        for (int i = 0; i < list.size(); i++) {
            SimpleEntityNotNull entity = list.get(i);
            entity.getId();
            entity.getSimpleBoolean();
            entity.getSimpleByte();
            entity.getSimpleShort();
            entity.getSimpleInt();
            entity.getSimpleLong();
            entity.getSimpleFloat();
            entity.getSimpleDouble();
            entity.getSimpleString();
            entity.getSimpleByteArray();
        }
        stopClock();
    }

}
