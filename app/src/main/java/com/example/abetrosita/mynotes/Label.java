package com.example.abetrosita.mynotes;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.io.Serializable;

/**
 * Created by AbetRosita on 1/25/2017.
 */

public class Label implements Serializable{
    private int mId;
    private String mName;
    private String mColor;
    private int mPosition;
    private boolean mNew;

    private static final long serialVersionUID = 2L;

    public Label(int id, String name, String color, int position) {
        mId = id;
        mName = name;
        mColor = color;
        mPosition = position;
    }

    public Label(String name) {
        mName = name;
        mColor = "#000000";
        mPosition = 0;
    }

    public Label(Cursor cursor){
        //cursor.moveToFirst();
        mId =  cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
        mName =  cursor.getString(cursor.getColumnIndex(LabelContract.Columns.LABEL_NAME));
        mColor =  cursor.getString(cursor.getColumnIndex(LabelContract.Columns.LABEL_COLOR));
        mPosition = cursor.getInt(cursor.getColumnIndex(LabelContract.Columns.LABEL_POSITION));
        mNew = false;
    }

    public ContentValues getContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(LabelContract.Columns.LABEL_NAME, mName);
        cv.put(LabelContract.Columns.LABEL_COLOR, mColor);
        cv.put(LabelContract.Columns.LABEL_POSITION, mPosition);
        return cv;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setNew(boolean _new){
        mNew = _new;
    }

    public boolean isNew(){
        return mNew;
    }
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        mColor = color;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }
}
