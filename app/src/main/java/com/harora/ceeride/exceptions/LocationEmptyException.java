package com.harora.ceeride.exceptions;

import android.content.Context;

/**
 * Created by harora on 3/29/16.
 */
public class LocationEmptyException extends CeerideException {

    public LocationEmptyException(Context mContext, String message, String displayMessage) {
        super(mContext, message, displayMessage);
    }
}
