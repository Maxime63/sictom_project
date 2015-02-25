package com.savajolchauvet.isima.sictomproject.endpoint;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.appspot.speedy_baton_840.sictomApi.SictomApi;
import com.appspot.speedy_baton_840.sictomApi.model.TTournee;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.savajolchauvet.isima.sictomproject.constante.ConstanteMetier;

import java.io.IOException;

/**
 * Created by Maxime on 24/02/2015.
 */
public class UpdateTourneeEndpointAsyncTask extends AsyncTask<Pair<Context, String>, Void, TTournee>
{

    private SictomApi sictomApi = null;
    private Context context;


    public UpdateTourneeEndpointAsyncTask(){
        SictomApi.Builder builder = new SictomApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                null);
        sictomApi = builder.build();
    }
    @Override
    protected TTournee doInBackground(Pair<Context, String>... params) {
        context = params[0].first;
        String paramsRecu = params[0].second;

        String[] tourneeParams = paramsRecu.split(ConstanteMetier.SEPARATOR);
        long tourneeId = Long.valueOf(tourneeParams[0]);
        double charge = Double.valueOf(tourneeParams[1]);
        String date = tourneeParams[2];

        try {
            return sictomApi.updateTournee(tourneeId, charge, date).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }    }
}
