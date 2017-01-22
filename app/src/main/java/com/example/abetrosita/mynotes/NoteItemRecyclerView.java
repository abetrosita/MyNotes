package com.example.abetrosita.mynotes;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import static com.example.abetrosita.mynotes.MainActivity.LOADER_ID;


public class NoteItemRecyclerView extends RecyclerView implements
        NotesAdapter.NotesAdapterOnClickHandler{

    private static final String LOG_TAG = NoteItemRecyclerView.class.getSimpleName();
    private LoaderManager mLoaderManager;
    private LoaderManager.LoaderCallbacks mCallback;
    private ContentResolver mContentResolver;
    private Context mContext;
    private static String mFilterText;
    private int mNoteStatusFilter;



    public NoteItemRecyclerView(Context context, LoaderManager.LoaderCallbacks loaderCallbacks) {
        super(context);
        mContext = context;
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        RecyclerView recyclerViewNotes = (RecyclerView) findViewById(R.id.rv_notes);
        recyclerViewNotes.setLayoutManager(layoutManager);
        recyclerViewNotes.setHasFixedSize(false);
        mCallback = loaderCallbacks;
        mFilterText = "";

        mContentResolver = mContext.getContentResolver();
        NotesAdapter notesAdapter = new NotesAdapter(null, this);
        recyclerViewNotes.setAdapter(notesAdapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                //showToast("MOVED");
                return true;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                final int swipeStatusAction;
                final String swipeMessage;

                if(mNoteStatusFilter == AppConstants.NOTE_STATUS_DELETED) {
                    swipeStatusAction = AppConstants.NOTE_STATUS_ARCHIVED;
                }else if(mNoteStatusFilter == AppConstants.NOTE_STATUS_ARCHIVED){
                    if(swipeDir == ItemTouchHelper.RIGHT){
                        swipeStatusAction = AppConstants.NOTE_STATUS_DELETED;
                    }else {
                        swipeStatusAction = AppConstants.NOTE_STATUS_ACTIVE;
                    }
                }else if(mNoteStatusFilter == AppConstants.NOTE_STATUS_ACTIVE){
                    swipeStatusAction = AppConstants.NOTE_STATUS_ARCHIVED;
                }else {
                    swipeStatusAction = mNoteStatusFilter;
                }

                if(swipeStatusAction == AppConstants.NOTE_STATUS_DELETED){
                    swipeMessage = "Note deleted.";
                }else if(swipeStatusAction == AppConstants.NOTE_STATUS_ARCHIVED){
                    swipeMessage = "Note archived.";
                }else {
                    swipeMessage = "Note restored.";
                }

                String viewTag = (String) viewHolder.itemView.getTag();
                final Uri uri = NotesContract.Notes.buildNoteUri(viewTag);
                final ContentValues cv = new ContentValues();
                cv.put(NotesContract.Columns.STATUS, swipeStatusAction);
                mContentResolver.update(uri, cv, null, null);
                mLoaderManager.restartLoader(LOADER_ID, null, mCallback);

                Snackbar.make(viewHolder.itemView, swipeMessage, Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cv.put(NotesContract.Columns.STATUS, mNoteStatusFilter);
                                mContentResolver.update(uri, cv, null, null);
                                mLoaderManager.restartLoader(LOADER_ID, null, mCallback);
                            }
                        }).show();

            }
        }).attachToRecyclerView(recyclerViewNotes);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, NoteDetailActivity.class);
        intent.putExtra(AppConstants.NOTE_INTENT_ACTION, AppConstants.NOTE_INTENT_UPDATE);
        Uri uri = NotesContract.Notes.buildNoteUri(String.valueOf(view.getTag()));
        Cursor cursor = mContentResolver.query(uri,null,null,null,null);
        Note note = new Note(cursor);
        intent.putExtra(AppConstants.NOTE_INTENT_OBJECT, note);
        mContext.startActivity(intent);
    }
}
