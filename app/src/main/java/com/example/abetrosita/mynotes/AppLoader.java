package com.example.abetrosita.mynotes;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.TextView;

import static com.example.abetrosita.mynotes.MainActivity.sMNoteAdapter;

/**
 * Created by AbetRosita on 1/28/2017.
 */

public class AppLoader{
    private Context mContext;
    private int mLoaderId;
    private String mStatusFilter;
    private Cursor mCursor;
    private Activity mActivity;

    public AppLoader(Activity activity, int loaderId, String statusFilter){
        mContext = activity.getApplicationContext();
        mLoaderId = loaderId;
        mStatusFilter = statusFilter;

    }


    public class NoteCallback implements LoaderManager.LoaderCallbacks<Cursor>{

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String selection = NoteContract.Columns.STATUS + "=" + String.valueOf(mStatusFilter);
            return new CursorLoader(mContext, NoteContract.URI_TABLE, null, selection, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            mCursor = cursor;
            TextView emptyList = (TextView) mActivity.findViewById(R.id.tv_add_note);
            if(cursor != null) {
                sMNoteAdapter.reloadNotesData(mCursor);
                emptyList.setVisibility(View.GONE);
            }else {
                emptyList.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            sMNoteAdapter.reloadNotesData(null);
        }
    }





}
