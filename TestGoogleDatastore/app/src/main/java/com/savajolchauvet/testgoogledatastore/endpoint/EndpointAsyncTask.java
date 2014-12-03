package com.savajolchauvet.testgoogledatastore.endpoint;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.maximechauvet.api.myApi.MyApi;
import com.savajolchauvet.testgoogledatastore.constante.ConstanteMetier;

import java.io.IOException;

/**
 * Created by Maxime on 01/12/2014.
 */
public class EndpointAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
    private static MyApi myApiService = null;
    private Context context;

    public EndpointAsyncTask(){
        MyApi.Builder builder = new MyApi.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                null);
//                .setRootUrl("http://10.0.2.2:8080/_ah/api")
//                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                    @Override
//                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
//                        abstractGoogleClientRequest.setDisableGZipContent(true);
//                    }
//                });
        myApiService = builder.build();
    }

    @Override
    protected String doInBackground(Pair<Context, String>... params) {
        context = params[0].first;
        String paramsRecu = params[0].second;

        String[] paramsLatLng = paramsRecu.split(ConstanteMetier.PARAMS_SEPARATOR);

        double latitude = Double.valueOf(paramsLatLng[0]);
        double longitude = Double.valueOf(paramsLatLng[1]);

        try {
            return myApiService.addCoordonnee(latitude, longitude).execute().toString();
        }catch (IOException e){
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }
}
