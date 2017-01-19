package com.example.abetrosita.mynotes;

/**
 * Created by AbetRosita on 1/14/2017.
 */

public class AppConstants {

    public static final String NOTE_URI_PREFIX_ID = "ID_";
    public static final String NOTE_URI_SEGMENT_TYPE = "type";
    public static final String NOTE_URI_SEGMENT_STATUS = "status";
    public static final String NOTE_URI_SEGMENT_LABEL = "label";

    public static final int NOTE_TYPE_DEFAULT = 1;
    public static final int NOTE_TYPE_NORMAL = 1;
    public static final int NOTE_TYPE_LIST = 2;
    public static final int NOTE_TYPE_PHOTO = 3;
    public static final int NOTE_STATUS_DEFAULT = 1;
    public static final int NOTE_STATUS_ACTIVE = 1;
    public static final int NOTE_STATUS_ARCHIVED = 2;
    public static final int NOTE_STATUS_DELETED = 3;
    public static final int NOTE_IMAGE_LOCATION_DEFAULT = 1;
    public static final int NOTE_IMAGE_LOCATION_LOCAL = 1;
    public static final int NOTE_IMAGE_LOCATION_GDRIVE = 2;
    public static final int NOTE_IMAGE_LOCATION_DROPBOX = 3;
    public static final String NOTE_NO_LABEL = "";

    public static final String NOTE_INTENT_OBJECT = "note_intent_transfer";
    public static final String NOTE_INTENT_ACTION = "note_intent_action";
    public static final String NOTE_INTENT_ADD = "note_intent_add";
    public static final String NOTE_INTENT_UPDATE = "note_intent_update";


    public static final String NOTE_NO_IMAGE = "";


    public static final String INTENT_EXTRA_ID = "note_id";

}