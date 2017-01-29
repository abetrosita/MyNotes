package com.example.abetrosita.mynotes;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        NoteAdapter.NotesAdapterOnClickHandler{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private RecyclerView mNoteList;
    private TextView mAddNote;
    private Toast mToast;
    private Cursor mCursor;
    private String mFilterText;
    private int mNoteStatusFilter;
    private static int mLoaderId = 1;
    private static int mActiveLoaderId = 1;

    private LoaderManager.LoaderCallbacks noteLoader;
    private LoaderManager.LoaderCallbacks labelLoader;

    public static final int LABEL_LOADER_ID = 100;
    public static final int NOTE_LOADER_ID = AppConstant.NOTE_STATUS_ACTIVE;
    public static final int ARCHIVE_LOADER_ID = AppConstant.NOTE_STATUS_ARCHIVED;
    public static final int DELETED_LOADER_ID = AppConstant.NOTE_STATUS_DELETED;
    //public static LoaderManager mLoaderManager;
    public  NoteAdapter sMNoteAdapter;
    public static ContentResolver mContentResolver;
    public  Context mContext;

    public static List<Label> mLabels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //TODO: Save preference to save state of status...
        mNoteStatusFilter = AppConstant.NOTE_STATUS_DEFAULT;
        mAddNote = (TextView) findViewById(R.id.tv_add_normal_note);
        mNoteList = (RecyclerView) findViewById(R.id.rv_notes);
        mNoteList.setLayoutManager(layoutManager);
        mNoteList.setHasFixedSize(false);
        mContext = this;
        mFilterText = "";
        mLabels = new ArrayList<>();
        noteLoader = new NoteLoader();
        labelLoader = new LabelLoader();

        mContentResolver = this.getContentResolver();
        sMNoteAdapter = new NoteAdapter(null, this);
        mNoteList.setAdapter(sMNoteAdapter);

        //mLoaderManager = getSupportLoaderManager();


        mAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
                intent.putExtra(AppConstant.NOTE_INTENT_ACTION, AppConstant.NOTE_INTENT_ADD);
                startActivity(intent);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
           @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                return true;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

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
                getSupportLoaderManager().restartLoader(NOTE_LOADER_ID, null, noteLoader);

                Snackbar.make(viewHolder.itemView, swipeMessage, Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cv.put(NoteContract.Columns.STATUS, mNoteStatusFilter);
                                mContentResolver.update(uri, cv, null, null);
                                getSupportLoaderManager().restartLoader(NOTE_LOADER_ID, null, noteLoader);
                            }
                        }).show();

           }
        }).attachToRecyclerView(mNoteList);


        //getSupportLoaderManager().initLoader(mLoaderId, null, noteLoader);
        getSupportLoaderManager().initLoader(LABEL_LOADER_ID, null, labelLoader);

        final int REQUEST_CODE_ASK_PERMISSIONS = 555;
        int hasReadContactsPermission =
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if(hasReadContactsPermission != PackageManager.PERMISSION_GRANTED){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_ASK_PERMISSIONS);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "+++ MAIN ACIVITY ON RESUME CALLED");
        getSupportLoaderManager().restartLoader(mLoaderId, null, noteLoader);
//        getSupportLoaderManager().restartLoader(LABEL_LOADER_ID, null, labelLoader);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_label){
            getSupportLoaderManager().restartLoader(LABEL_LOADER_ID, null, labelLoader);
            new LabelDialog(mContext, mLabels);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_status_active) {
            mNoteStatusFilter = AppConstant.NOTE_STATUS_ACTIVE;
            mLoaderId = NOTE_LOADER_ID;
            this.setTitle("Notes");
        } else if (id == R.id.nav_status_archive) {
            mNoteStatusFilter = AppConstant.NOTE_STATUS_ARCHIVED;
            mLoaderId = ARCHIVE_LOADER_ID;
            this.setTitle("Archive");

        } else if (id == R.id.nav_status_deleted) {
            mNoteStatusFilter = AppConstant.NOTE_STATUS_DELETED;
            mLoaderId = DELETED_LOADER_ID;
            this.setTitle("Trash");

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        getSupportLoaderManager().restartLoader(mLoaderId, null, noteLoader);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class NoteLoader implements LoaderManager.LoaderCallbacks<Cursor>{

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.d(LOG_TAG, "NOTE ON CREATE LOADER CALLED");
            String selection = NoteContract.Columns.STATUS + "=" + String.valueOf(mNoteStatusFilter);
            return new CursorLoader(MainActivity.this, NoteContract.URI_TABLE, null, selection, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            mCursor = cursor;
            Log.d(LOG_TAG, "NOTE ON LOAD FINISHED CALLED: " + String.valueOf(cursor.getCount()));
            TextView emptyList = (TextView) findViewById(R.id.tv_add_note);
            if(cursor.getCount() > 0) {
                emptyList.setVisibility(View.GONE);
            }else {
                emptyList.setVisibility(View.VISIBLE);
            }
            sMNoteAdapter.reloadNotesData(mCursor);

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            sMNoteAdapter.reloadNotesData(null);
        }
    }

    private class LabelLoader implements LoaderManager.LoaderCallbacks<Cursor>{

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.d(LOG_TAG, "LABEL ON CREATE LOADER CALLED");
            mLabels.clear();
            return new CursorLoader(MainActivity.this, LabelContract.URI_TABLE, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Log.d(LOG_TAG, "LABEL ON FINISHED LOADER CALLED");
            if(cursor.getCount() == 0) return;
            cursor.moveToFirst();
            do {
                mLabels.add(new Label(cursor));
            }while(cursor.moveToNext());
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mLabels.clear();
        }
    }



    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
        intent.putExtra(AppConstant.NOTE_INTENT_ACTION, AppConstant.NOTE_INTENT_UPDATE);
        Uri uri = NoteContract.Notes.buildNoteUri(String.valueOf(view.getTag()));
        Cursor cursor = mContentResolver.query(uri,null,null,null,null);
        Note note = new Note(cursor);
        intent.putExtra(AppConstant.NOTE_INTENT_OBJECT, note);
        startActivity(intent);
    }

    private void showToast(String message){
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        mToast.show();
    }

}
