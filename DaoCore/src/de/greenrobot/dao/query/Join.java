/*
 * Copyright (C) 2011-2015 Markus Junginger, greenrobot (http://greenrobot.de)
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
package de.greenrobot.dao.query;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;

public class Join<SRC, DST> {

    private final AbstractDao<SRC, ?> daoSource;
    private final AbstractDao<DST, ?> daoDestination;

    private final Property joinPropertySource;
    private final Property joinPropertyDestination;

    public Join(AbstractDao<SRC, ?> daoSource, Property sourceJoinProperty,
                AbstractDao<DST, ?> daoDestination, Property destinationJoinProperty) {
        this.daoSource = daoSource;
        this.joinPropertySource = sourceJoinProperty;
        this.daoDestination = daoDestination;
        this.joinPropertyDestination = destinationJoinProperty;
    }

}
