package com.savajolchauvet.isima.sictomproject.endpoint;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.appspot.speedy_baton_840.sictomApi.SictomApi;
import com.appspot.speedy_baton_840.sictomApi.model.TTournee;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.savajolchauvet.isima.sictomproject.bdd.TCoordonneesDataSource;
import com.savajolchauvet.isima.sictomproject.constante.ConstanteMetier;

import java.io.IOException;

/**
 * Created by Maxime on 22/02/2015.
 */
public class InsertTourneeEndpointAsyncTask extends AsyncTask<Pair<Context, String>, Void, TTournee> {

    private SictomApi sictomApi = null;
    private Context context;


    public InsertTourneeEndpointAsyncTask(){
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
        long chauffeurId = Long.valueOf(tourneeParams[0]);
        long firstRipperId = Long.valueOf(tourneeParams[1]);
        long secondRipperId = Long.valueOf(tourneeParams[2]);
        long camionId = Long.valueOf(tourneeParams[3]);
        long numero = Long.valueOf(tourneeParams[4]);
        String date = tourneeParams[5];
        String nom = tourneeParams[6];

        try {
            return sictomApi.insertTournee(camionId, chauffeurId, date, firstRipperId, nom, numero, secondRipperId).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
