package com.harora.ceeride.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by harora on 3/25/16.
 */
public class CeerideContentProvider extends ContentProvider {


    public static final String DB_RAW_QUERY = "PRAGMA table_info(favorites)";
    private static final String LOG_TAG = CeerideContentProvider.class.getSimpleName();
    private CeerideSqlOpenHelper mCeerideSqliteHelper;

    @Override
    public boolean onCreate() {
        mCeerideSqliteHelper = new CeerideSqlOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder){
        SQLiteDatabase db = mCeerideSqliteHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (CeerideContract.URI_MATCHER.match(uri)) {
            case CeerideContract.FAVORITE_LIST:
                builder.setTables(DbSchema.Favorite.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = CeerideContract.Favorite.SORT_ORDER_DEFAULT;
                }
                break;
        }

        Cursor cursor =
                builder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
        cursor.setNotificationUri(
                getContext().getContentResolver(),
                uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mCeerideSqliteHelper.getWritableDatabase();
        Cursor ti = db.rawQuery(DB_RAW_QUERY, null);
        try {
            if (CeerideContract.URI_MATCHER.match(uri) == CeerideContract.FAVORITE_LIST) {
                long id = db.insert(DbSchema.Favorite.TABLE_NAME,
                        null,
                        contentValues);
                return getUriForId(id, uri);
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error while inserting : " + e.getMessage());
        } finally {
            ti.close();
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
                // notify all listeners of changes:
                getContext().
                        getContentResolver().
                        notifyChange(itemUri, null);
            return itemUri;
        }

        throw new SQLException("Problem while inserting into uri: " + uri);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (CeerideContract.URI_MATCHER.match(uri)){
            case CeerideContract.FAVORITE_ID:
                return CeerideContract.Favorite.CONTENT_TYPE;

            case CeerideContract.FAVORITE_LIST:
                return CeerideContract.Favorite.CONTENT_FAVORITE_TYPE;

            default:
                return null;

        }
    }

}