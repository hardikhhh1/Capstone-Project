package com.harora.ceeride.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by harora on 3/25/16.
 */
public class CeeridePreferences {

    public static final String UBER_RIDE = "Uber";
    public static final String LYFT_RIDE = "Lyft";
    public static final String LYFT_RIDE_PREFERENCE = "lyft_ride_preference";
    private static final String UBER_RIDE_PREFERENCE = "uber_ride_preference";

    public static Set<String> getRides(Context context){
        List<String> rides = new ArrayList<>();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        if(SP.getBoolean(UBER_RIDE_PREFERENCE, true)){
            rides.add(UBER_RIDE);
        }
        if (SP.getBoolean(LYFT_RIDE_PREFERENCE, true)) {
            rides.add(LYFT_RIDE);
        }
        return new HashSet<>(rides);
    }

}
