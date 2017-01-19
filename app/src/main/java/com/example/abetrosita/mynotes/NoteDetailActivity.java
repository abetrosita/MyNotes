package com.example.abetrosita.mynotes;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AbetRosita on 1/14/2017.
 */

public class NoteDetailActivity extends AppCompatActivity {

    EditText noteTitle;
    EditText noteBody;
    EditText noteLabel;
    ImageView noteImage;
    String mIntentAction;
    String mFilePath;
    ContentValues mValues;
    String mImagePath;
    Bitmap mBitmap;
    Note mNote;

    private static final int PICK_IMAGE = 1;
    private static final String LOG_TAG = NoteDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mImagePath = "";

        noteTitle = (EditText) findViewById(R.id.et_note_title);
        noteBody = (EditText) findViewById(R.id.et_note_body);
        noteLabel = (EditText) findViewById(R.id.et_note_label);
        noteImage = (ImageView) findViewById(R.id.detail_image);
        mValues = new ContentValues();
        noteImage.setVisibility(View.GONE);



        mIntentAction = getIntent().getStringExtra(AppConstants.NOTE_INTENT_ACTION);
        if(mIntentAction.equals(AppConstants.NOTE_INTENT_UPDATE)){
            mNote = (Note) getIntent().getSerializableExtra(AppConstants.NOTE_INTENT_OBJECT);
            noteTitle.setText(mNote.getTitle());
            noteBody.setText(mNote.getBody());
            noteLabel.setText(mNote.getLabel());
            mImagePath = mNote.getImagePath();
            Log.d(LOG_TAG, "+++ IMAGE PATH RETRIEVED: " + mImagePath);
            if(mImagePath.length() > 0){
                Picasso.with(getApplicationContext()).load(Uri.parse(mImagePath)).into(noteImage);
                noteImage.setVisibility(View.VISIBLE);
            }
        }

        noteBody.requestFocusFromTouch();
        noteBody.setSelection(noteBody.getText().length());;

    }


    @Override
    public void onBackPressed() {
        String title = noteTitle.getText().toString();
        String body = noteBody.getText().toString();
        String label = noteLabel.getText().toString();
        String imagePath = AppConstants.NOTE_NO_IMAGE;

        if(title.length() + body.length() == 0) {
            Toast.makeText(this, "Empty note not saved.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        switch (mIntentAction){
            case AppConstants.NOTE_INTENT_ADD:
                Note note = new Note(title, body, label, imagePath);
                mValues = note.getContentValues();
                mValues.put(NotesContract.Columns.IMAGE_PATH, mImagePath);
                MainActivity.mContentResolver.insert(NotesContract.URI_TABLE, mValues);
                break;
            case AppConstants.NOTE_INTENT_UPDATE:
                mNote.setTitle(title);
                mNote.setBody(body);
                mNote.setLabel(label);
                mNote.setDateModified(getDateTime());
                mValues = mNote.getContentValues();
                mValues.put(NotesContract.Columns.IMAGE_PATH, mImagePath);
                Log.d(LOG_TAG, "++++ ImagePath:" + mImagePath);
                Uri uri = NotesContract.Notes.buildNoteUri(String.valueOf(mNote.getId()));
                MainActivity.mContentResolver.update(uri, mValues, null, null);
                break;
        }

        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);

        if(mIntentAction.equals(AppConstants.NOTE_INTENT_UPDATE)){
            this.setTitle("Edit Note");
        }else {
            this.setTitle("Add Note");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add_image:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_IMAGE);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case PICK_IMAGE:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    mImagePath = selectedImage.toString();
                    noteImage.setVisibility(View.VISIBLE);
                    Picasso.with(getApplicationContext()).load(selectedImage).into(noteImage);
               }
        }
    }


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
