package com.harora.ceeride.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by harora on 3/25/16.
 */
public class CeerideContentProvider extends ContentProvider {


    CeerideSqlOpenHelper mCeerideSqliteHelper;

    @Override
    public boolean onCreate() {
        mCeerideSqliteHelper = new CeerideSqlOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection,
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
            case CeerideContract.FAVORITE_ID:
//                builder.setTables(CeerideContract.Favorite.);
//                builder.appendWhere(CeerideContract.Favorite. + " = " +
//                    uri.getLastPathSegment());
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
        // if we want to be notified of any changes:
//        if (useAuthorityUri) {
//            cursor.setNotificationUri(
//                    getContext().getContentResolver(),
//                    LentItemsContract.CONTENT_URI);
//        }
        cursor.setNotificationUri(
                getContext().getContentResolver(),
                uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mCeerideSqliteHelper.getWritableDatabase();
        Cursor ti = db.rawQuery("PRAGMA table_info(favorites)", null);
        if ( ti.moveToFirst() ) {
            do {
                System.out.println("col: " + ti.getString(1));
            } while (ti.moveToNext());
        }
        if (CeerideContract.URI_MATCHER.match(uri) == CeerideContract.FAVORITE_LIST){
            long id = db.insert(DbSchema.Favorite.TABLE_NAME,
                    null,
                    contentValues);
            return getUriForId(id, uri);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
//            if (!isInBatchMode()) {
                // notify all listeners of changes:
                getContext().
                        getContentResolver().
                        notifyChange(itemUri, null);
//            }
            return itemUri;
        }
        // s.th. went wrong:
        throw new SQLException(
                "Problem while inserting into uri: " + uri);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
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