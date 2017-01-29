package com.example.abetrosita.mynotes;

import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.abetrosita.mynotes.AppConstant.CONTENT_AUTHORITY;
import static com.example.abetrosita.mynotes.AppConstant.VND_PREFIX;


/**
 * Created by AbetRosita on 1/23/2017.
 */

public class LabelContract {
    interface Columns {
        String LABEL_NAME = "label_name";
        String LABEL_COLOR = "label_color";
        String LABEL_POSITION = "label_position";
    }

    public static final String PATH_LABELS = "labels";
    public static final String[] TOP_LEVEL_PATHS = {PATH_LABELS};
    public static final Uri URI_TABLE = AppConstant.BASE_CONTENT_URI.buildUpon().
            appendEncodedPath(PATH_LABELS).build();

    public static class Labels implements LabelContract.Columns, BaseColumns {
        public static final String CONTENT_TYPE = VND_PREFIX + "dir/" + CONTENT_AUTHORITY + "." + PATH_LABELS;
        public static final String CONTENT_ITEM_TYPE = VND_PREFIX + "item/" + CONTENT_AUTHORITY + "." + PATH_LABELS;

        public static Uri buildLabelUri(String labelId) {
            return URI_TABLE.buildUpon().appendEncodedPath(labelId).build();
        }

        public static String getLabelId(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
}
