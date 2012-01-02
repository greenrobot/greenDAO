/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
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
package de.greenrobot.daoexample;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class NoteTest extends AbstractDaoTestLongPk<NoteDao, Note> {

    public NoteTest() {
        super(NoteDao.class);
    }

    @Override
    protected Note createEntity(Long key) {
        Note entity = new Note();
        entity.setId(key);
        entity.setText("green note"); // Has to be set as it is "not null"
        return entity;
    }

}
