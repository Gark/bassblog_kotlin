package pixel.kotlin.bassblog.storage;

import android.net.Uri;


public final class IoContract {

    private IoContract() {
    }

    public static final String AUTHORITY = "pixel.kotlin.bassblog";

    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public interface IoBaseColumns {
        String COL_ID_NO_PREFIX = "_id";
    }

    public static class Post implements IoBaseColumns {


        private Post() {
        }

        public static final String PATH = "post";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);

        public static final String TABLE_NAME = "post";
        public static final String COL_POST_ID = "post_id";
        public static final String COL_ID = TABLE_NAME + "." + COL_ID_NO_PREFIX;
        public static final String COL_TITLE = "post_title";
        public static final String COL_PUBLISHED = "post_published";
        public static final String COL_LABEL = "post_label";
        public static final String COL_IMAGE = "post_image";
        public static final String COL_FAVORITE = "post_favorite";
        public static final String COL_TRACK = "post_track_url";
    }
}