package com.example.abetrosita.mynotes;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import static android.content.ContentValues.TAG;



public class NotesProvider  extends ContentProvider {
    private NotesDatabase mOpenHelper;

    private static String LOG_TAG = NotesProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int NOTES = 100;
    private static final int NOTE_ID = 101;
    private static final int NOTE_TYPE = 201;
    private static final int NOTE_STATUS = 202;
    private static final int NOTE_LABEL = 203;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = NotesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, NotesContract.PATH_NOTES, NOTES);
        matcher.addURI(authority, NotesContract.PATH_NOTES + "/#", NOTE_ID);
        matcher.addURI(authority, NotesContract.PATH_NOTES + "/" + AppConstants.NOTE_URI_SEGMENT_TYPE +"/*", NOTE_TYPE);
        matcher.addURI(authority, NotesContract.PATH_NOTES + "/" + AppConstants.NOTE_URI_SEGMENT_STATUS +"/*", NOTE_STATUS);
        matcher.addURI(authority, NotesContract.PATH_NOTES + "/" + AppConstants.NOTE_URI_SEGMENT_LABEL +"/*", NOTE_LABEL);
        return matcher;
    }

    public void deleteDatabase(){
        mOpenHelper.close();
        NotesDatabase.deleteDatabase(getContext());
        mOpenHelper = new NotesDatabase(getContext());
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new NotesDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        final String lastPath = uri.getLastPathSegment();

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(NotesDatabase.Tables.NOTES);

        switch (match) {
            case NOTES:
                // do nothing
                break;
            case NOTE_ID:
                queryBuilder.appendWhere(BaseColumns._ID +"="+ lastPath);
                break;
            case NOTE_TYPE:
                queryBuilder.appendWhere(NotesContract.Columns.TYPE +"="+lastPath);
                break;
            case NOTE_STATUS:
                queryBuilder.appendWhere(NotesContract.Columns.STATUS +"="+lastPath);
            case NOTE_LABEL:
                queryBuilder.appendWhere(NotesContract.Columns.LABEL +"="+lastPath);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                return NotesContract.Notes.CONTENT_TYPE;
            default:
                return NotesContract.Notes.CONTENT_ITEM_TYPE;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.v(TAG, "insert(uri=" + uri + " values=" + values.toString());
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case NOTES:
                long recordId = db.insertOrThrow(NotesDatabase.Tables.NOTES, null, values);
                if(recordId > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                Log.d(LOG_TAG, "Item added in databaes" + String.valueOf(recordId));
                return NotesContract.Notes.buildNoteUri(String.valueOf(recordId));
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int numRowsDeleted = 0;
        if(uri.equals(NotesContract.URI_TABLE)){
            deleteDatabase();
            return numRowsDeleted;
        }
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case NOTE_ID:
                String id = NotesContract.Notes.getNoteId(uri);
                String selectionCriteria = BaseColumns._ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                numRowsDeleted = db.delete(NotesDatabase.Tables.NOTES, selectionCriteria, selectionArgs);
                if (numRowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(NotesContract.URI_TABLE, null);
                }
                return numRowsDeleted;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v(TAG, "update(uri=" + uri + " values=" + values.toString());
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int numRowsUpdated = 0;

        String selectionCriteria = selection;
        switch (match) {
            case NOTES:
                break;
            case NOTE_ID:
                String id = NotesContract.Notes.getNoteId(uri);
                selectionCriteria = BaseColumns._ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        numRowsUpdated = db.update(NotesDatabase.Tables.NOTES, values, selectionCriteria, selectionArgs);
        return numRowsUpdated;

    }

}
