package com.example.abetrosita.mynotes;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.abetrosita.mynotes.AppConstant.LABEL_IS_CLICKABLE;


public class NoteDetailActivity extends AppCompatActivity {

    public static FlowLayout mFlowLayoutLabel;
    EditText mEditTextTitle;
    EditText mEditTextBody;
    List<String> mLabelIds;
    ImageView mImageView;
    String mIntentAction;
    ContentValues mValues;
    String mImagePath;
    Note mNote;
    static Context mContext;

    private static final int PICK_IMAGE = 1;
    private static final String LOG_TAG = NoteDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mImagePath = "";
        mContext = this;
        mFlowLayoutLabel = (FlowLayout) findViewById(R.id.ll_note_labels);
        mEditTextTitle = (EditText) findViewById(R.id.et_note_title);
        mEditTextBody = (EditText) findViewById(R.id.et_note_body);

        mImageView = (ImageView) findViewById(R.id.detail_image);
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
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

        mIntentAction = getIntent().getStringExtra(AppConstant.NOTE_INTENT_ACTION);
        if(mIntentAction.equals(AppConstant.NOTE_INTENT_UPDATE)){
            mNote = (Note) getIntent().getSerializableExtra(AppConstant.NOTE_INTENT_OBJECT);
            mEditTextTitle.setText(mNote.getTitle());
            mEditTextBody.setText(mNote.getBody());
            mLabelIds = mNote.getLabelIds();
            loadFlowLabel(mLabelIds);
            mImagePath = mNote.getImagePath();
            if(mImagePath.length() > 0){
                Picasso.with(getApplicationContext()).load(Uri.parse(mImagePath)).into(mImageView);
                mImageView.setVisibility(View.VISIBLE);
            }
        }else{
            loadFlowLabel(null);
        }

        mEditTextBody.requestFocusFromTouch();
        mEditTextBody.setSelection(mEditTextBody.getText().length());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public static void loadFlowLabel(List<String> labelIds){
        mFlowLayoutLabel.removeAllViews();
        mFlowLayoutLabel.addView(new LabelListLayout(mContext, labelIds, LABEL_IS_CLICKABLE));
    }


    @Override
    public void onBackPressed() {
        String title = mEditTextTitle.getText().toString();
        String body = mEditTextBody.getText().toString();
        String imagePath = AppConstant.NOTE_NO_IMAGE;
        String labelIds = "";
        if (LabelListLayout.mLabelIds != null) {
            labelIds = TextUtils.join(",", LabelListLayout.mLabelIds);
        }
        if(title.length() + body.length() == 0) {
            finish();
            return;
        }

        switch (mIntentAction){
            case AppConstant.NOTE_INTENT_ADD:
                Note note = new Note(title, body, imagePath);
                mValues = note.getContentValues();
                mValues.put(NoteContract.Columns.LABEL, labelIds);
                mValues.put(NoteContract.Columns.IMAGE_PATH, mImagePath);
                MainActivity.mContentResolver.insert(NoteContract.URI_TABLE, mValues);
                break;
            case AppConstant.NOTE_INTENT_UPDATE:
                mNote.setTitle(title);
                mNote.setBody(body);
                mNote.setDateModified(getDateTime());
                mValues = mNote.getContentValues();
                mValues.put(NoteContract.Columns.LABEL, labelIds);
                mValues.put(NoteContract.Columns.IMAGE_PATH, mImagePath);
                //Log.d(LOG_TAG, "++++ ImagePath:" + mImagePath);
                Uri uri = NoteContract.Notes.buildNoteUri(String.valueOf(mNote.getId()));
                MainActivity.mContentResolver.update(uri, mValues, null, null);
                break;
        }

        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);

        if(mIntentAction.equals(AppConstant.NOTE_INTENT_UPDATE)){
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
