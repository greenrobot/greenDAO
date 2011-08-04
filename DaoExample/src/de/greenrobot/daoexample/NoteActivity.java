package de.greenrobot.daoexample;

import java.util.Date;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class NoteActivity extends ListActivity {

    private SQLiteDatabase db;
    private Cursor cursor;
    private NoteDao noteDao;
    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        MyDbHelper helper = new MyDbHelper(this);
        db = helper.getWritableDatabase();
        noteDao = new NoteDao(db);
        cursor = db.query("NOTE", noteDao.getAllColumns(), null, null, null, null, "TEXT ASC");

        String[] from = { "TEXT", "DATE" };
        int[] to = { android.R.id.text1, android.R.id.text2 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from,
                to);
        setListAdapter(adapter);

        editText = (EditText) findViewById(R.id.editTextNote);
        addUiListeners();
    }

    protected void addUiListeners() {
        editText.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addNote();
                    return true;
                }
                return false;
            }
        });

        final View button = findViewById(R.id.buttonAdd);
        button.setEnabled(false);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = s.length() != 0;
                button.setEnabled(enable);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void onMyButtonClick(View view) {
        addNote();
    }

    private void addNote() {
        String noteText = editText.getText().toString();
        editText.setText("");

        Note note = new Note(null, noteText, new Date().toString());
        noteDao.insert(note);
        Log.d("DaoExample", "Inserted new note, ID: " + note.getId());

        cursor.requery();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        noteDao.deleteByKey(id);
        Log.d("DaoExample", "Deleted note, ID: " + id);
        cursor.requery();
    }

}