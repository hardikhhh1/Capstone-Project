package com.harora.ceeride.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

import com.harora.ceeride.model.CeerideFavorite;
import com.harora.ceeride.model.CeeridePlace;

import java.util.ArrayList;

/**
 * Created by harora on 3/27/16.
 */
public class CeerideFavoriteDbUtils {

    public static final String PLACE_TAG = "Placetag";
    public static final String LIMIT_STATEMENT = " LIMIT 5";
    private static final String LOG_TAG = CeerideFavoriteDbUtils.class.getSimpleName();

    public static Uri save(Context context, CeerideFavorite favorite){
        ContentValues values = new ContentValues();
        CeeridePlace place = favorite.getCeeridePlace();
        values.put(DbSchema.Favorite.PLACE_NAME, place.getPlaceName());
        values.put(DbSchema.Favorite.LATITUDE, place.getLatitude());
        values.put(DbSchema.Favorite.LONGITUDE, place.getLongitude());
        values.put(DbSchema.Favorite.TAG, PLACE_TAG);
        ContentResolver resolver = context.getContentResolver();
        return resolver.insert(CeerideContract.Favorite.CONTENT_URI, values);
    }

    public static void incrementCounter(Context context, CeerideFavorite favorite) {
        ArrayList<CeerideFavorite> favorites = get(context, favorite.getId());
        Log.d(LOG_TAG, "Incrementing the counter");
        if (favorites.size() == 0) {
            Log.d(LOG_TAG, "Couldn't increment as the favourites size is 0");
            return;
        }
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DbSchema.Favorite.COUNTER, favorites.get(0).getCounter() + 1);
        resolver.update(CeerideContract.Favorite.CONTENT_URI, values,
                DbSchema.Favorite.ID + '=' + favorite.getId(), new String[]{});
    }

    public static ArrayList<CeerideFavorite> get(Context context, Integer id){
        ArrayList<CeerideFavorite> favorites = new ArrayList<>();
        ContentResolver resolver;
        Cursor cursor = null;
        try {
            String[] selectionArgs = {};
            String selection = "";
            if (id != null) {
                selection = DbSchema.Favorite.ID + "=" + id.toString();
            }
            resolver = context.getContentResolver();
            cursor = resolver.query(CeerideContract.Favorite.CONTENT_URI,
                    CeerideContract.Favorite.PROJECTION_ALL,
                    selection, selectionArgs, CeerideContract.Favorite.SORT_ORDER_DEFAULT +
                            LIMIT_STATEMENT);

            if (cursor.moveToFirst()) {
                do {
                    Integer favId = cursor.getInt(0);
                    Double latitude = cursor.getDouble(1);
                    Double longitude = cursor.getDouble(2);
                    String placeName = cursor.getString(3);
                    String tag = cursor.getString(4);
                    Integer counter = cursor.getInt(6);

                    CeerideFavorite fav = new CeerideFavorite(favId,
                            new CeeridePlace(latitude, longitude, placeName), tag, counter);
                    favorites.add(fav);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.d(LOG_TAG, "Error while getting the array list of fav : " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return favorites;
    }

}
