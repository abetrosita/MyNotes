package com.example.abetrosita.mynotes;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.abetrosita.mynotes.AppConstants.LABEL_IS_CLICKABLE;

/**
 * Created by AbetRosita on 1/14/2017.
 */

public class NoteDetailActivity extends AppCompatActivity {

    FlowLayout mFlowLayoutLabel;
    EditText mEditTextTitle;
    EditText mEditTextBody;
    List<String> mLabels;
    ImageView mImageView;
    String mIntentAction;
    ContentValues mValues;
    String mImagePath;
    Note mNote;

    private static final int PICK_IMAGE = 1;
    private static final String LOG_TAG = NoteDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Context context = this;
        mImagePath = "";

        mFlowLayoutLabel = (FlowLayout) findViewById(R.id.ll_note_labels);
        mFlowLayoutLabel.removeAllViews();
        mEditTextTitle = (EditText) findViewById(R.id.et_note_title);
        mEditTextBody = (EditText) findViewById(R.id.et_note_body);
        //TODO: ADD LABELS TABLE TO CONTROL LABEL SELECTIONS
        //mTextViewLabel = (EditText) findViewById(R.id.et_note_label);

        mImageView = (ImageView) findViewById(R.id.detail_image);
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setTitle("Delete Image");
                dialogBuilder.setMessage("Do you want to delete the selected image?");
                dialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        mImageView.setVisibility(View.GONE);
                        mImagePath = "";
                    }
                });
                dialogBuilder.show();
                return true;
            }
        });

        mValues = new ContentValues();
        //TODO: CONSIDER TO ADD MULTIPLE IMAGES
        mImageView.setVisibility(View.GONE);

        mIntentAction = getIntent().getStringExtra(AppConstants.NOTE_INTENT_ACTION);
        if(mIntentAction.equals(AppConstants.NOTE_INTENT_UPDATE)){
            mNote = (Note) getIntent().getSerializableExtra(AppConstants.NOTE_INTENT_OBJECT);
            mEditTextTitle.setText(mNote.getTitle());
            mEditTextBody.setText(mNote.getBody());
            mLabels = mNote.getLabelList();
            mFlowLayoutLabel.addView(new LabelListLayout(this, mLabels, LABEL_IS_CLICKABLE));
            mImagePath = mNote.getImagePath();
            if(mImagePath.length() > 0){
                Picasso.with(getApplicationContext()).load(Uri.parse(mImagePath)).into(mImageView);
                mImageView.setVisibility(View.VISIBLE);
            }
        }

        mEditTextBody.requestFocusFromTouch();
        mEditTextBody.setSelection(mEditTextBody.getText().length());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }


    @Override
    public void onBackPressed() {
        String title = mEditTextTitle.getText().toString();
        String body = mEditTextBody.getText().toString();
        String imagePath = AppConstants.NOTE_NO_IMAGE;

        if(title.length() + body.length() == 0) {
            Toast.makeText(this, "Empty note not saved.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        switch (mIntentAction){
            case AppConstants.NOTE_INTENT_ADD:
                Note note = new Note(title, body, imagePath);
                mValues = note.getContentValues();
                mValues.put(NotesContract.Columns.IMAGE_PATH, mImagePath);
                MainActivity.mContentResolver.insert(NotesContract.URI_TABLE, mValues);
                break;
            case AppConstants.NOTE_INTENT_UPDATE:
                mNote.setTitle(title);
                mNote.setBody(body);
                //mNote.setLabel(label);
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
                    mImageView.setVisibility(View.VISIBLE);
                    Picasso.with(getApplicationContext()).load(selectedImage).into(mImageView);
               }
        }
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
