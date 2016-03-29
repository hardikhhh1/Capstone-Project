package com.harora.ceeride.db;

/**
 * Created by harora on 3/25/16.
 */

import android.provider.BaseColumns;

public class DbSchema {

        public class Favorite{
                public static final String TABLE_NAME = "favorites";
                public static final String ID = "id";
                public static final String TAG = "favorite_tag";
                public static final String LATITUDE = "latitude";
                public static final String LONGITUDE = "longitude";
                public static final String PLACE_NAME = "place_name";

                public static final String CREATE_TABLE_FAVORITES =
                        "CREATE TABLE favorites (" +
                                "id                 INTEGER  PRIMARY KEY AUTOINCREMENT," +
                                "favorite_tag       TEXT NOT NULL," +
                                "latitude           REAL NOT NULL," +
                                "longitude          REAL NOT NULL," +
                                "place_name         TEXT NOT NULL" +
                                ")";
        }

        static String DB_NAME = "ceeride.db";



        // The following trigger is here to show you how to
        // achieve referential integrity without foreign keys.
//        String DDL_CREATE_TRIGGER_DEL_ITEMS =
//                "CREATE TRIGGER delete_items DELETE ON items \n"
//                        + "begin\n"
//                        + "  delete from photos where items_id = old._id;\n"
//                        + "end\n";

        static String DDL_DROP_TBL_FAVORITES =
                "DROP TABLE IF EXISTS favorites";

        static String DML_WHERE_ID_CLAUSE = "_id = ?";

        static String DEFAULT_TBL_ITEMS_SORT_ORDER = Favorite.TAG + " ASC";

}
