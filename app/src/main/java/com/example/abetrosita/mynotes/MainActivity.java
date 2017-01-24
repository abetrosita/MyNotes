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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        NotesAdapter.NotesAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private RecyclerView mNoteList;
    private TextView mAddNote;
    private Toast mToast;
    private Cursor mCursor;
    private String mFilterText;
    private int mNoteStatusFilter;
    private static int mLoaderId = 1;

    public static final int LOADER_ID = AppConstants.NOTE_STATUS_DEFAULT;
    public static final int NOTE_LOADER_ID = AppConstants.NOTE_STATUS_ACTIVE;
    public static final int ARCHIVE_LOADER_ID = AppConstants.NOTE_STATUS_ARCHIVED;
    public static final int DELETED_LOADER_ID = AppConstants.NOTE_STATUS_DELETED;
    public static LoaderManager mLoaderManager;
    public static LoaderManager.LoaderCallbacks mCallback;
    public static NotesAdapter mNotesAdapter;
    public static ContentResolver mContentResolver;
    public static Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //TODO: Save preference to save state of status...
        mNoteStatusFilter = AppConstants.NOTE_STATUS_DEFAULT;
        mAddNote = (TextView) findViewById(R.id.tv_add_normal_note);
        mNoteList = (RecyclerView) findViewById(R.id.rv_notes);
        mNoteList.setLayoutManager(layoutManager);
        mNoteList.setHasFixedSize(false);
        mCallback = this;
        mContext = this;
        mFilterText = "";

        mContentResolver = this.getContentResolver();
        mNotesAdapter = new NotesAdapter(null, this);
        mNoteList.setAdapter(mNotesAdapter);

        mLoaderManager = getSupportLoaderManager();


        mAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
                intent.putExtra(AppConstants.NOTE_INTENT_ACTION, AppConstants.NOTE_INTENT_ADD);
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
        }).attachToRecyclerView(mNoteList);

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
        //Log.d(LOG_TAG, "+++ MAIN ACIVITY ON RESUME CALLED");
        mLoaderManager.restartLoader(mLoaderId, null, this);


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
            new LabelDialog(mContext);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_status_active) {
            mNoteStatusFilter = AppConstants.NOTE_STATUS_ACTIVE;
            mLoaderId = NOTE_LOADER_ID;
            this.setTitle("Notes");
        } else if (id == R.id.nav_status_archive) {
            mNoteStatusFilter = AppConstants.NOTE_STATUS_ARCHIVED;
            mLoaderId = ARCHIVE_LOADER_ID;
            this.setTitle("Archive");

        } else if (id == R.id.nav_status_deleted) {
            mNoteStatusFilter = AppConstants.NOTE_STATUS_DELETED;
            mLoaderId = DELETED_LOADER_ID;
            this.setTitle("Deleted");

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        mLoaderManager.restartLoader(mLoaderId, null, mCallback);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Log.d(LOG_TAG, "+++ MAIN ACIVITY LOADER CREATE CALLED");
        //mContentResolver = this.getContentResolver();
        String selection = NotesContract.Columns.STATUS + "=" + String.valueOf(mNoteStatusFilter);
        CursorLoader cursorLoader = new CursorLoader(this, NotesContract.URI_TABLE, null, selection, null, null);
        return cursorLoader;
        //return new NotesLoader(this, mNoteStatusFilter, mContentResolver, mFilterText);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursor = cursor;
        TextView emptyList = (TextView) findViewById(R.id.tv_add_note);
        if(cursor != null) {
            mNotesAdapter.reloadNotesData(mCursor);
            emptyList.setVisibility(View.GONE);
        }else {
            emptyList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNotesAdapter.reloadNotesData(null);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
        intent.putExtra(AppConstants.NOTE_INTENT_ACTION, AppConstants.NOTE_INTENT_UPDATE);
        Uri uri = NotesContract.Notes.buildNoteUri(String.valueOf(view.getTag()));
        Cursor cursor = mContentResolver.query(uri,null,null,null,null);
        Note note = new Note(cursor);
        intent.putExtra(AppConstants.NOTE_INTENT_OBJECT, note);
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
