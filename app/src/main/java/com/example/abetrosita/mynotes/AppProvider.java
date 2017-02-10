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
import static com.example.abetrosita.mynotes.NoteContract.Notes.getNoteId;


public class AppProvider extends ContentProvider {
    private AppDatabase mOpenHelper;

    private static String LOG_TAG = AppProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int NOTES = 100;
    private static final int NOTE_ID = 101;
    private static final int NOTE_TYPE = 201;
    private static final int NOTE_STATUS = 202;
    private static final int NOTE_LABEL = 203;
    private static final int LABELS = 300;
    private static final int LABEL_ID = 301;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = NoteContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, NoteContract.PATH_NOTES, NOTES);
        matcher.addURI(authority, NoteContract.PATH_NOTES + "/#", NOTE_ID);
        matcher.addURI(authority, NoteContract.PATH_NOTES + "/" + AppConstant.NOTE_URI_SEGMENT_TYPE +"/*", NOTE_TYPE);
        matcher.addURI(authority, NoteContract.PATH_NOTES + "/" + AppConstant.NOTE_URI_SEGMENT_STATUS +"/*", NOTE_STATUS);
        matcher.addURI(authority, NoteContract.PATH_NOTES + "/" + AppConstant.NOTE_URI_SEGMENT_LABEL +"/*", NOTE_LABEL);

        matcher.addURI(authority, LabelContract.PATH_LABELS, LABELS);
        matcher.addURI(authority, LabelContract.PATH_LABELS + "/#", LABEL_ID);
        return matcher;
    }

    public void deleteDatabase(){
        mOpenHelper.close();
        AppDatabase.deleteDatabase(getContext());
        mOpenHelper = new AppDatabase(getContext());
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new AppDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        final String lastPath = uri.getLastPathSegment();
        sortOrder = BaseColumns._ID + " DESC";

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match) {
            case NOTES:
                queryBuilder.setTables(AppDatabase.Tables.NOTES);
                break;
            case NOTE_ID:
                queryBuilder.setTables(AppDatabase.Tables.NOTES);
                queryBuilder.appendWhere(BaseColumns._ID +"="+ lastPath);
                break;
            case NOTE_TYPE:
                queryBuilder.setTables(AppDatabase.Tables.NOTES);
                queryBuilder.appendWhere(NoteContract.Columns.TYPE +"="+lastPath);
                break;
            case NOTE_STATUS:
                queryBuilder.setTables(AppDatabase.Tables.NOTES);
                queryBuilder.appendWhere(NoteContract.Columns.STATUS +"="+lastPath);
            case NOTE_LABEL:
                queryBuilder.setTables(AppDatabase.Tables.NOTES);
                queryBuilder.appendWhere(NoteContract.Columns.LABEL +"="+lastPath);
                break;
            case LABELS:
                queryBuilder.setTables(AppDatabase.Tables.LABELS);
                break;
            case LABEL_ID:
                queryBuilder.setTables(AppDatabase.Tables.LABELS);
                queryBuilder.appendWhere(BaseColumns._ID +"="+ lastPath);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        //Log.d("LOG_VALUES", "+++ LOADER CALLED LABEL QUERY+++");
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                return NoteContract.Notes.CONTENT_TYPE;
            case LABELS:
                return LabelContract.Labels.CONTENT_TYPE;
            case LABEL_ID:
                return LabelContract.Labels.CONTENT_ITEM_TYPE;
            default:
                return NoteContract.Notes.CONTENT_ITEM_TYPE;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.v(TAG, "insert(uri=" + uri + " values=" + values.toString());
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        long recordId = -1;

        switch (match) {
            case NOTES:
                recordId = db.insertOrThrow(AppDatabase.Tables.NOTES, null, values);
                if(recordId > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                //Log.d(LOG_TAG, "NOTE ADDED");
                return NoteContract.Notes.buildNoteUri(String.valueOf(recordId));
            case LABELS:
                recordId = db.insertOrThrow(AppDatabase.Tables.LABELS, null, values);
                if(recordId > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                //Log.d(LOG_TAG, "LABEL ADDED");
                return NoteContract.Notes.buildNoteUri(String.valueOf(recordId));
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int numRowsDeleted = 0;
        if(uri.equals(NoteContract.URI_TABLE)){
            deleteDatabase();
            return numRowsDeleted;
        }
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        String id;
        String selectionCriteria;



        switch (match) {
            case NOTE_ID:
                id = getNoteId(uri);
                selectionCriteria = BaseColumns._ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                numRowsDeleted = db.delete(AppDatabase.Tables.NOTES, selectionCriteria, selectionArgs);
                if (numRowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(NoteContract.URI_TABLE, null);
                }
                return numRowsDeleted;
            case LABEL_ID:
                id = LabelContract.Labels.getLabelId(uri);
                selectionCriteria = BaseColumns._ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                numRowsDeleted = db.delete(AppDatabase.Tables.LABELS, selectionCriteria, selectionArgs);
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
        String id;

        String selectionCriteria = selection;
        switch (match) {
            case NOTES:
                break;
            case NOTE_ID:
                id = getNoteId(uri);
                selectionCriteria = BaseColumns._ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                numRowsUpdated = db.update(AppDatabase.Tables.NOTES, values, selectionCriteria, selectionArgs);
                break;
            case LABELS:
                break;
            case LABEL_ID:
                id = LabelContract.Labels.getLabelId(uri);
                selectionCriteria = BaseColumns._ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                numRowsUpdated = db.update(AppDatabase.Tables.LABELS, values, selectionCriteria, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        return numRowsUpdated;
    }
}
