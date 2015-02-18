package com.savajolchauvet.isima.sictomproject.service;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.savajolchauvet.isima.sictomproject.R;
import com.savajolchauvet.isima.sictomproject.activity.MainActivity;

/**
 * Created by Maxime on 14/02/2015.
 */
public class MapsService extends IntentService {
    private GoogleMap mMap;

    public MapsService(){
        super(MapsService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }
}
