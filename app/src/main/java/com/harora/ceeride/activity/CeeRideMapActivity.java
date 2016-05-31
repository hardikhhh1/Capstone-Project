package com.harora.ceeride.activity;

import com.harora.ceeride.model.CeeridePlace;


public  interface CeeRideMapActivity {

    void onPlaceSelected(CeeridePlace place, int index);

    void onSourceChanged(CeeridePlace place);

    void onDestinationChanged(CeeridePlace place);

}
