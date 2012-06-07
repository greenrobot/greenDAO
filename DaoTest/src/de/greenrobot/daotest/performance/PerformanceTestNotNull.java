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
package de.greenrobot.daotest.performance;

import de.greenrobot.daotest.SimpleEntityNotNull;
import de.greenrobot.daotest.SimpleEntityNotNullDao;
import de.greenrobot.daotest.entity.SimpleEntityNotNullHelper;

public class PerformanceTestNotNull extends PerformanceTest<SimpleEntityNotNullDao, SimpleEntityNotNull, Long> {

    static long sequence;

    public PerformanceTestNotNull() {
        super(SimpleEntityNotNullDao.class);
    }

    @Override
    protected SimpleEntityNotNull createEntity() {
        return SimpleEntityNotNullHelper.createEntity(sequence++);
    }

}
