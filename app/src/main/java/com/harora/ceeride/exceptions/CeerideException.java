package com.harora.ceeride.exceptions;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by harora on 3/29/16.
 */
public class CeerideException extends Exception {

    private Context mContext;

    CeerideException(Context mContext, String message, String displayMessage){
        super(message);
        this.mContext = mContext;
        showExceptionMessage(displayMessage);
    }

    private void showExceptionMessage(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
                .show();
    }

}
