package com.harora.ceeride.db;

/**
 * Created by harora on 3/25/16.
 */

class DbSchema {

    static String DB_NAME = "ceeride.db";
    static String DDL_DROP_TBL_FAVORITES =
            "DROP TABLE IF EXISTS favorites";
    static String DML_WHERE_ID_CLAUSE = "_id = ?";
    static String DEFAULT_TBL_ITEMS_SORT_ORDER = Favorite.TAG + " ASC";

    public class Favorite {
        public static final String TABLE_NAME = "favorites";
        public static final String ID = "id";
        public static final String TAG = "favorite_tag";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String PLACE_NAME = "place_name";
        public static final String LAST_UPDATED = "last_updated";
        public static final String COUNTER = "counter";

        public static final String CREATE_TABLE_FAVORITES =
                "CREATE TABLE favorites (" +
                        "id                 INTEGER  PRIMARY KEY AUTOINCREMENT," +
                        "favorite_tag       TEXT NOT NULL," +
                        "latitude           REAL NOT NULL UNIQUE," +
                        "longitude          REAL NOT NULL UNIQUE," +
                        "place_name         TEXT NOT NULL UNIQUE," +
                        "last_updated       TIMESTAMP NULL," +
                        "counter            TIMESTAMP NOT NULL DEFAULT 0" +
                        ")";


        public static final String UPDATE_TIME_TRIGGER =
                "CREATE TRIGGER update_time_trigger" +
                        "  AFTER UPDATE ON " + TABLE_NAME + " FOR EACH ROW" +
                        "  BEGIN " +
                        "UPDATE " + TABLE_NAME +
                        "  SET " + LAST_UPDATED + " = current_timestamp" +
                        "  WHERE " + ID + " = old." + ID + ";" +
                        "  END";
    }

}
