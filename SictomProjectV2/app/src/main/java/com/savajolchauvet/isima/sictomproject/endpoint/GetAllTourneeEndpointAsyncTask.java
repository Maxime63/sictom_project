package com.savajolchauvet.isima.sictomproject.endpoint;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.appspot.speedy_baton_840.sictomApi.SictomApi;
import com.appspot.speedy_baton_840.sictomApi.model.TTournee;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxime on 11/02/2015.
 */
public class GetAllTourneeEndpointAsyncTask extends AsyncTask<Pair<Context, String>, Void, List<TTournee>> {
    private SictomApi sictomApi = null;
    private Context context;

    public GetAllTourneeEndpointAsyncTask(){
        SictomApi.Builder builder = new SictomApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                null);
        sictomApi = builder.build();
    }

    @Override
    protected List<TTournee> doInBackground(Pair<Context, String>... params) {
        List<TTournee> camions = new ArrayList<>();

        context = params[0].first;

        try {
            camions = sictomApi.getAllTournees().execute().getItems();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return camions;
    }
}
