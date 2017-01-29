package com.example.abetrosita.mynotes;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.PrintWriter;


public class NoteLoader extends AsyncTaskLoader<Cursor> {
    private static final String LOG_TAG = NoteLoader.class.getSimpleName();
    private ContentResolver mContentResolver;
    private Cursor mCursor;
    private int mStatus;
    private String mFilterText;

    public NoteLoader(Context context, int status, ContentResolver contentResolver, String filterText){
        super(context);
        mContentResolver = contentResolver;
        mFilterText = filterText;
        mStatus = status;
    }

    public NoteLoader(Context context, Uri uri, ContentResolver contentResolver){
        super(context);
        mContentResolver = contentResolver;
        mFilterText = "";
    }

    public NoteLoader(Context context) {
        super(context);
    }

    @Override
    public void setUpdateThrottle(long delayMS) {
        super.setUpdateThrottle(delayMS);
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
    }

    @Override
    protected boolean onCancelLoad() {
        return super.onCancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        super.onCanceled(cursor);
        if(mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public Cursor loadInBackground() {
        //TODO: ADD SEARCH FUNCTION
        String selection = NoteContract.Columns.STATUS + "=" + String.valueOf(mStatus);
        mCursor = mContentResolver.query(NoteContract.URI_TABLE, null, selection, null, null);
        return mCursor;
    }

    @Override
    public void deliverResult(Cursor cursor) {

        if(isReset()){
            if(cursor != null){
                mCursor.close();
                return;
            }
        }

        Cursor oldCursor = mCursor;
        mCursor = cursor;
        if(mCursor == null){
            Log.d(LOG_TAG, "+++++ No data returned.");
        }

        if(isStarted()) {
            super.deliverResult(cursor);
        }

        if(oldCursor != null && oldCursor != cursor) {
            oldCursor.close();
        }

    }

    @Override
    protected void onStartLoading() {
        if(mCursor != null) {
            deliverResult(mCursor);
        }
        if(takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if(mCursor != null){
            mCursor.close();
        }
        mCursor = null;
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected Cursor onLoadInBackground() {
        return super.onLoadInBackground();
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();
    }

    @Override
    public boolean isLoadInBackgroundCanceled() {
        return super.isLoadInBackgroundCanceled();
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
    }
}
