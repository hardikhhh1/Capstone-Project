package com.harora.ceeride.db;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;

/**
 * Created by harora on 3/25/16.
 */
public class CeerideContract {


    public static final String AUTHORITY =
                "com.harora.ceeride";

    public static final Uri CONTENT_URI =
                Uri.parse("content://" + AUTHORITY);

    /**
     * Constants for the Items table
     * of the lentitems provider.
     */
    public static final class Favorite {


        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CeerideContract.CONTENT_URI,
                        "favorites");

        /**
         * The mime type of a directory of items.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/com.harora.ceeride_favorites";
        /**
         * The mime type of a single item.
         */
        public static final String CONTENT_FAVORITE_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/com.harora.ceeride_favorites";
        /**
         * A projection of all columns
         * in the items table.
         */
        public static final String[] PROJECTION_ALL =
                {DbSchema.Favorite.ID, DbSchema.Favorite.LATITUDE,
                DbSchema.Favorite.LONGITUDE, DbSchema.Favorite.PLACE_NAME,
                DbSchema.Favorite.TAG};
        /**
         * The default sort order for
         * queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT =
                DbSchema.Favorite.TAG + " ASC";
    }

    // helper constants for use with the UriMatcher
    static final int FAVORITE_LIST = 1;
    static final int FAVORITE_ID = 2;
    static final UriMatcher URI_MATCHER;

    // prepare the UriMatcher
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(CeerideContract.AUTHORITY,
                "favorites",
                FAVORITE_LIST);
        URI_MATCHER.addURI(CeerideContract.AUTHORITY,
                "favorites/#",
                FAVORITE_ID);
    }
}

