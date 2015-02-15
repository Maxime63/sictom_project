package com.savajolchauvet.isima.sictomproject.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Maxime on 14/02/2015.
 */
public class MapsService extends IntentService {

    public MapsService(){
        super(MapsService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
