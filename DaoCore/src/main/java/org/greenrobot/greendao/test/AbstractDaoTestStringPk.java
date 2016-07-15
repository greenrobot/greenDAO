/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.greenrobot.greendao.test;

import org.greenrobot.greendao.AbstractDao;

/**
 * Base class for DAOs having a String as a PK.
 * 
 * @author Markus
 * 
 * @param <D>
 *            DAO class
 * @param <T>
 *            Entity type of the DAO
 */
public abstract class AbstractDaoTestStringPk<D extends AbstractDao<T, String>, T> extends
        AbstractDaoTestSinglePk<D, T, String> {

    public AbstractDaoTestStringPk(Class<D> daoClass) {
        super(daoClass);
    }

    @Override
    protected String createRandomPk() {
        int len = 1 + random.nextInt(30);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char c = (char) ('a' + random.nextInt('z' - 'a'));
            builder.append(c);
        }
        return builder.toString();
    }

}
