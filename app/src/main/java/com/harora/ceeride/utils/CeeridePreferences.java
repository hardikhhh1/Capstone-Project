package com.harora.ceeride.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by harora on 3/25/16.
 */
public class CeeridePreferences {

    private static final List<String> rides = Arrays.asList("Uber", "Lyft");

//    public static float getMaximumWalkableDistance(Context context){
//        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(Context);
//        float walkableDistance = SP.getFloat("walkable_distance", 0);
//        return walkableDistance;
//    }


    public static Set<String> getRides(Context context){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        return SP.getStringSet("ride", new HashSet<>(rides));
    }

}
