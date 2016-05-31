package com.harora.ceeride.db;

/**
 * Created by harora on 3/25/16.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class CeerideSqlOpenHelper extends SQLiteOpenHelper {

    private static final String NAME = DbSchema.DB_NAME;
    private static final int VERSION = 6;

    public CeerideSqlOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbSchema.Favorite.CREATE_TABLE_FAVORITES);
        db.execSQL(DbSchema.Favorite.UPDATE_TIME_TRIGGER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DbSchema.DDL_DROP_TBL_FAVORITES);
        onCreate(db);
    }

}
