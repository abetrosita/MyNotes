package com.example.abetrosita.mynotes;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

//import static com.example.abetrosita.mynotes.MainActivity.mCallback;


public class NoteRecyclerView extends RecyclerView{

    private static final String LOG_TAG = NoteRecyclerView.class.getSimpleName();
    private ContentResolver mContentResolver;
    private Activity mActivity;
    private Context mContext;
    private Cursor mCursor;
    private static String mFilterText;
    private int mNoteStatusFilter;

    public NoteAdapter noteAdapter;



    public NoteRecyclerView(Context context) {
        super(context);
        mContext = context;
        mContentResolver = context.getContentResolver();

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        RecyclerView recyclerViewNotes = (RecyclerView) findViewById(R.id.rv_notes);
        recyclerViewNotes.setLayoutManager(layoutManager);
        recyclerViewNotes.setHasFixedSize(false);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
                //do nothing, we only care about swiping
                return true;
            }
            @Override
            public void onSwiped(ViewHolder viewHolder, int swipeDir) {

                final int swipeStatusAction;
                final String swipeMessage;

                if(mNoteStatusFilter == AppConstant.NOTE_STATUS_DELETED) {
                    swipeStatusAction = AppConstant.NOTE_STATUS_ARCHIVED;
                }else if(mNoteStatusFilter == AppConstant.NOTE_STATUS_ARCHIVED){
                    if(swipeDir == ItemTouchHelper.RIGHT){
                        swipeStatusAction = AppConstant.NOTE_STATUS_DELETED;
                    }else {
                        swipeStatusAction = AppConstant.NOTE_STATUS_ACTIVE;
                    }
                }else if(mNoteStatusFilter == AppConstant.NOTE_STATUS_ACTIVE){
                    swipeStatusAction = AppConstant.NOTE_STATUS_ARCHIVED;
                }else {
                    swipeStatusAction = mNoteStatusFilter;
                }

                if(swipeStatusAction == AppConstant.NOTE_STATUS_DELETED){
                    swipeMessage = "Note deleted.";
                }else if(swipeStatusAction == AppConstant.NOTE_STATUS_ARCHIVED){
                    swipeMessage = "Note archived.";
                }else {
                    swipeMessage = "Note restored.";
                }

                String viewTag = (String) viewHolder.itemView.getTag();
                final Uri uri = NoteContract.Notes.buildNoteUri(viewTag);
                final ContentValues cv = new ContentValues();
                cv.put(NoteContract.Columns.STATUS, swipeStatusAction);
                mContentResolver.update(uri, cv, null, null);
                //mLoaderManager.restartLoader(NOTE_LOADER_ID, null, mCallback);

                Snackbar.make(viewHolder.itemView, swipeMessage, Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cv.put(NoteContract.Columns.STATUS, mNoteStatusFilter);
                                mContentResolver.update(uri, cv, null, null);
                                //mLoaderManager.restartLoader(NOTE_LOADER_ID, null, mCallback);
                            }
                        }).show();

            }
        }).attachToRecyclerView(recyclerViewNotes);
    }


}
