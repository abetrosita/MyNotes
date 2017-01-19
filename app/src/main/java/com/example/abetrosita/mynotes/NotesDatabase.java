package com.example.abetrosita.mynotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by AbetRosita on 1/14/2017.
 */

public class NotesDatabase extends SQLiteOpenHelper {

        private static final String LOG_TAG = NotesDatabase.class.getSimpleName();
        private static final String DATABASE_NAME = "notes.db";
        private static final int DATABASE_VERSION = 2;
        private final Context mContext;

        interface Tables {
            String NOTES = "notes";
        }

        public NotesDatabase(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + Tables.NOTES + "("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + NotesContract.Columns.TYPE + " INTEGER NOT NULL, "
                    + NotesContract.Columns.STATUS + " INTEGER NOT NULL, "
                    + NotesContract.Columns.TITLE + " TEXT NOT NULL, "
                    + NotesContract.Columns.BODY + " TEXT NOT NULL, "
                    + NotesContract.Columns.IMAGE_PATH + " TEXT NOT NULL, "
                    + NotesContract.Columns.IMAGE_LOCATION + " INTEGER NOT NULL, "
                    + NotesContract.Columns.LABEL + " TEXT NOT NULL, "
                    + NotesContract.Columns.DATE_CREATED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + NotesContract.Columns.DATE_MODIFIED + " INTEGER NOT NULL)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            int version = oldVersion;
            if(version == 1){
                // add extra fields without deleting existing data
                version = 2;
            }
            if(version != DATABASE_VERSION) {
                db.execSQL("DROP TABLE IF EXISTS " + Tables.NOTES);
                onCreate(db);
            }
        }

        public static void deleteDatabase(Context context) {
            context.deleteDatabase(DATABASE_NAME);
        }
}
