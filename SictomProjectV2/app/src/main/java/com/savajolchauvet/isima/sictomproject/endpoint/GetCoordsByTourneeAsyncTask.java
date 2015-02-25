package com.savajolchauvet.isima.sictomproject.endpoint;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.appspot.speedy_baton_840.sictomApi.SictomApi;
import com.appspot.speedy_baton_840.sictomApi.model.TCoordonnee;
import com.appspot.speedy_baton_840.sictomApi.model.TUtilisateur;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by Maxime on 24/02/2015.
 */
public class GetCoordsByTourneeAsyncTask extends AsyncTask<Pair<Context, String>, Void, List<TCoordonnee>> {
    private SictomApi sictomApi = null;
    private Context context;

    public GetCoordsByTourneeAsyncTask(){
        SictomApi.Builder builder = new SictomApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                null);
        sictomApi = builder.build();
    }

    @Override
    protected List<TCoordonnee> doInBackground(Pair<Context, String>... params) {
        context = params[0].first;
        long mTournee = Long.valueOf(params[0].second);

        try {
            return sictomApi.getCoordsByTournee(mTournee).execute().getItems();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
