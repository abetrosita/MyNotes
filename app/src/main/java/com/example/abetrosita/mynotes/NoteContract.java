package com.example.abetrosita.mynotes;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by AbetRosita on 1/14/2017.
 */

public class NoteContract {
    interface Columns {
        String TYPE = "note_type";
        String STATUS = "note_status";
        String TITLE = "note_title";
        String BODY = "note_body";
        String IMAGE_PATH = "note_image_path";
        String IMAGE_LOCATION = "note_image_location";
        String LABEL = "note_label";
        String DATE_CREATED = "note_date_created";
        String DATE_MODIFIED = "note_date_modified";
    }

    public static final String CONTENT_AUTHORITY = "com.example.abetrosita.mynotes.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_NOTES = "notes";
    public static final String[] TOP_LEVEL_PATHS = {PATH_NOTES};
    public static final String VND_PREFIX = "vnd.android.cursor.";

    public static final Uri URI_TABLE = BASE_CONTENT_URI.buildUpon().
            appendEncodedPath(PATH_NOTES).build();
    public static final Uri URI_TYPE = URI_TABLE.buildUpon().
            appendEncodedPath(AppConstant.NOTE_URI_SEGMENT_TYPE).build();
    public static final Uri URI_STATUS = URI_TABLE.buildUpon().
            appendEncodedPath(AppConstant.NOTE_URI_SEGMENT_STATUS).build();
    public static final Uri URI_LABEL = URI_TABLE.buildUpon().
            appendEncodedPath(AppConstant.NOTE_URI_SEGMENT_LABEL).build();

    public static class Notes implements Columns, BaseColumns {
        public static final String CONTENT_TYPE = VND_PREFIX + "dir/" + CONTENT_AUTHORITY + "." + PATH_NOTES;
        public static final String CONTENT_ITEM_TYPE = VND_PREFIX + "item/" + CONTENT_AUTHORITY + "." + PATH_NOTES;

        public static Uri buildNoteUri(String noteId) {
            return URI_TABLE.buildUpon().appendEncodedPath(noteId).build();
        }

        public static Uri buildNoteTypeUri(String noteTypeId) {
            return URI_TYPE.buildUpon().appendEncodedPath(noteTypeId).build();
        }

        public static Uri buildNoteStatusUri(String noteStatusId) {
            return URI_STATUS.buildUpon().appendEncodedPath(noteStatusId).build();
        }

        public static Uri buildNoteLabelUri(String noteLabel) {
            return URI_LABEL.buildUpon().appendEncodedPath(noteLabel).build();
        }

        public static String getNoteId(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

}
