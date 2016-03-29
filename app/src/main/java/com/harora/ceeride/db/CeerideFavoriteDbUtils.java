package com.harora.ceeride.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.harora.ceeride.model.CeerideFavorite;
import com.harora.ceeride.model.CeeridePlace;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by harora on 3/27/16.
 */
public class CeerideFavoriteDbUtils {

    public static Uri save(Context context, CeerideFavorite favorite){
        ContentValues values = new ContentValues();
        CeeridePlace place = favorite.getCeeridePlace();
        values.put(DbSchema.Favorite.PLACE_NAME, place.getPlaceName());
        values.put(DbSchema.Favorite.LATITUDE, place.getLatitude());
        values.put(DbSchema.Favorite.LONGITUDE, place.getLongitude());
        values.put(DbSchema.Favorite.TAG, "Placetag");
        ContentResolver resolver = context.getContentResolver();
        Uri result = resolver.insert(CeerideContract.Favorite.CONTENT_URI, values);
        return result;
    }

    public static ArrayList<CeerideFavorite> get(Context context, Integer id){
        ArrayList<CeerideFavorite> favorites = new ArrayList<>();
        if(id == null){
            String[] temp = {};
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(CeerideContract.Favorite.CONTENT_URI,
                    CeerideContract.Favorite.PROJECTION_ALL,
                    "", temp, "");
            cursor.moveToFirst();
            while(cursor.moveToNext()){
                Double latitude =  cursor.getDouble(1);
                Double longitude = cursor.getDouble(2);
                String placeName = cursor.getString(3);
                String tag = cursor.getString(4);

                CeerideFavorite fav = new CeerideFavorite(
                        new CeeridePlace(latitude, longitude, placeName), tag);
                favorites.add(fav);

            }
            return favorites;
        }
        return favorites;
    }

}
