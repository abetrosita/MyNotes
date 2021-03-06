package com.example.abetrosita.mynotes;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.R.attr.type;


public class Note implements Serializable {
    private int mId;
    private int mType;
    private int mStatus;
    private String mTitle;
    private String mBody;
    private String mImagePath;
    private int mImageLocation;
    private String mLabel;
    private String mDateCreated;
    private String mDateModified;

    private static final long serialVersionUID = 1L;

    public Note(int _id, int type, int status, String title, String body, String imagePath,
                int imageLocation, String label, String dateCreated, String dateModified) {
        mId = _id;
        mType = type;
        mStatus = status;
        mTitle = title;
        mBody = body;
        mImagePath = imagePath;
        mImageLocation = imageLocation;
        mLabel = label;
        mDateCreated = dateCreated;
        mDateModified = dateModified;
    }

    public Note(String title, String body, String label, String imagePath) {
        this(title, body, imagePath);
        mLabel = label;
    }

    public Note(String title, String body, String imagePath) {
        mType = type;
        mStatus = AppConstant.NOTE_STATUS_DEFAULT;
        mTitle = title;
        mBody = body;
        mLabel = "";
        mImagePath = imagePath;
        mImageLocation = AppConstant.NOTE_IMAGE_LOCATION_DEFAULT;
        mDateModified = getDateTime();
        mDateCreated = mDateModified;
    }

    public Note (Cursor cursor){
        cursor.moveToFirst();
        mId =  cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
        mType =  cursor.getInt(cursor.getColumnIndex(NoteContract.Columns.TYPE));
        mStatus = cursor.getInt(cursor.getColumnIndex(NoteContract.Columns.STATUS));
        mTitle = cursor.getString(cursor.getColumnIndex(NoteContract.Columns.TITLE));
        mBody = cursor.getString(cursor.getColumnIndex(NoteContract.Columns.BODY));
        mImagePath = cursor.getString(cursor.getColumnIndex(NoteContract.Columns.IMAGE_PATH));
        mImageLocation = cursor.getInt(cursor.getColumnIndex(NoteContract.Columns.IMAGE_LOCATION));
        mLabel = cursor.getString(cursor.getColumnIndex(NoteContract.Columns.LABEL));
        mDateCreated = cursor.getString(cursor.getColumnIndex(NoteContract.Columns.DATE_CREATED));
        mDateModified = cursor.getString(cursor.getColumnIndex(NoteContract.Columns.DATE_MODIFIED));
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public ContentValues getContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(NoteContract.Columns.TYPE, mType);
        cv.put(NoteContract.Columns.STATUS, mStatus);
        cv.put(NoteContract.Columns.TITLE, mTitle);
        cv.put(NoteContract.Columns.BODY, mBody);
        cv.put(NoteContract.Columns.LABEL, mLabel);
        cv.put(NoteContract.Columns.IMAGE_PATH, mImagePath);
        cv.put(NoteContract.Columns.IMAGE_LOCATION, mImageLocation);
        cv.put(NoteContract.Columns.DATE_MODIFIED, mDateModified);
        return cv;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public List<String> getLabelList(){
        if(mLabel == null){
            return null;
        }
        if(mLabel.length() == 0){
            return null;
        }
        return new ArrayList<>(Arrays.asList(mLabel.split("#,#")));
    }

    public List<String> getLabelIds(){
        if(mLabel.length() > 0 ){
            return Arrays.asList(mLabel.split(","));
        }
        return null;
    }

    public void setLabel(String[] labelIds){
        mLabel = TextUtils.join(",", labelIds);
    }

    public int getId() {
        return mId;
    }

    public void setId(int _id) {
        mId = _id;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

    public int getImageLocation() {
        return mImageLocation;
    }

    public void setImageLocation(int imageLocation) {
        mImageLocation = imageLocation;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public String getDateCreated() {
        return mDateCreated;
    }

    public void setDateCreated(String dateCreated) {
        mDateCreated = dateCreated;
    }

    public String getDateModified() {
        return mDateModified;
    }

    public void setDateModified(String dateModified) {
        mDateModified = dateModified;
    }
}
