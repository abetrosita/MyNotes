package com.example.abetrosita.mynotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class AppDatabase extends SQLiteOpenHelper {

        private static final String LOG_TAG = AppDatabase.class.getSimpleName();
        private static final String DATABASE_NAME = "notes.db";
        private static final int DATABASE_VERSION = 3;

        interface Tables {
            String NOTES = "notes";
            String LABELS = "labels";
        }

        public AppDatabase(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + Tables.NOTES + "("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + NoteContract.Columns.TYPE + " INTEGER NOT NULL, "
                    + NoteContract.Columns.STATUS + " INTEGER NOT NULL, "
                    + NoteContract.Columns.TITLE + " TEXT NOT NULL, "
                    + NoteContract.Columns.BODY + " TEXT NOT NULL, "
                    + NoteContract.Columns.IMAGE_PATH + " TEXT NOT NULL, "
                    + NoteContract.Columns.IMAGE_LOCATION + " INTEGER NOT NULL, "
                    + NoteContract.Columns.LABEL + " TEXT NOT NULL, "
                    + NoteContract.Columns.DATE_CREATED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + NoteContract.Columns.DATE_MODIFIED + " INTEGER NOT NULL)");

            db.execSQL("CREATE TABLE " + Tables.LABELS + "("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + LabelContract.Columns.LABEL_NAME + " TEXT NOT NULL, "
                    + LabelContract.Columns.LABEL_COLOR + " TEXT NOT NULL, "
                    + LabelContract.Columns.LABEL_POSITION + " INTEGER NOT NULL)");
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
