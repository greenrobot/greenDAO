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
