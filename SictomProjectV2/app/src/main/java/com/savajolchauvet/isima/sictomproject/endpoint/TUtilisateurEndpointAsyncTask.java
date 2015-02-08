package com.savajolchauvet.isima.sictomproject.endpoint;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.appspot.speedy_baton_840.sictomApi.SictomApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

/**
 * Created by Maxime on 08/02/2015.
 */
public class TUtilisateurEndpointAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {

    private SictomApi sictomApi = null;
    private Context context;

    public TUtilisateurEndpointAsyncTask(){
        SictomApi.Builder builder = new SictomApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                null);
        sictomApi = builder.build();
    }

    @Override
    protected String doInBackground(Pair<Context, String>... params) {


        context = params[0].first;
        String paramsRecu = params[0].second;

        String[] paramsIdPwd = paramsRecu.split(";");

        String id = paramsIdPwd[0];
        String pwd = paramsIdPwd[1];

        try {
            sictomApi.getUtilisateurByLoginPassword(id, pwd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
