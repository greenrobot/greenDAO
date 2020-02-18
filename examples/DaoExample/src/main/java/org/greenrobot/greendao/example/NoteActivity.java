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
package org.greenrobot.greendao.example;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import org.greenrobot.greendao.query.Query;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private View addNoteButton;

    private NoteDao noteDao;
    private Query<Note> notesQuery;
    private NotesAdapter notesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setUpViews();

        // get the note DAO
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        noteDao = daoSession.getNoteDao();

        // query all notes, sorted a-z by their text
        notesQuery = noteDao.queryBuilder().orderAsc(NoteDao.Properties.Text).build();
        updateNotes();
    }

    private void updateNotes() {
        List<Note> notes = notesQuery.list();
        notesAdapter.setNotes(notes);
    }

    protected void setUpViews() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notesAdapter = new NotesAdapter(noteClickListener);
        recyclerView.setAdapter(notesAdapter);

        addNoteButton = findViewById(R.id.buttonAdd);
        addNoteButton.setEnabled(false);

        editText = findViewById(R.id.editTextNote);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addNote();
                return true;
            }
            return false;
        });
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = s.length() != 0;
                addNoteButton.setEnabled(enable);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void onAddButtonClick(View view) {
        addNote();
    }

    private void addNote() {
        String noteText = editText.getText().toString();
        editText.setText("");

        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Added on " + df.format(new Date());

        Note note = new Note();
        note.setText(noteText);
        note.setComment(comment);
        note.setDate(new Date());
        note.setType(NoteType.TEXT);
        noteDao.insert(note);
        Log.d("DaoExample", "Inserted new note, ID: " + note.getId());

        updateNotes();
    }

    NotesAdapter.NoteClickListener noteClickListener = new NotesAdapter.NoteClickListener() {
        @Override
        public void onNoteClick(int position) {
            Note note = notesAdapter.getNote(position);
            Long noteId = note.getId();

            noteDao.deleteByKey(noteId);
            Log.d("DaoExample", "Deleted note, ID: " + noteId);

            updateNotes();
        }
    };
}