package com.savajolchauvet.isima.endpoint;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.savajolchauvet.isima.bdd.TCoordonneesDataSource;
import com.savajolchauvet.isima.constante.ConstanteMetier;
import com.savajolchauvet.isima.sictomproject.backend.endpoint.tCoordonneeApi.TCoordonneeApi;
import com.savajolchauvet.isima.sictomproject.backend.endpoint.tCoordonneeApi.model.TCoordonnee;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Maxime on 21/01/2015.
 */
public class TCoordonneeEndpointAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
    private static Logger logger = Logger.getLogger(TCoordonneeEndpointAsyncTask.class.getName());

    private TCoordonneeApi tCoordonneeApiService = null;
    private Context context;


    public TCoordonneeEndpointAsyncTask(TCoordonneesDataSource coordonneeDataSource){
        TCoordonneeApi.Builder builder = new TCoordonneeApi.Builder(AndroidHttp.newCompatibleTransport(),
                                                                    new AndroidJsonFactory(),
                                                                    null);
        tCoordonneeApiService = builder.build();
    }

    @Override
    protected String doInBackground(Pair<Context, String>... params) {
        context = params[0].first;
        String paramsRecu = params[0].second;

        logger.info("Coords recived ==> " + paramsRecu);

        String[] paramsLatLng = paramsRecu.split(";");

        long id = Long.valueOf(paramsLatLng[0]);
        double latitude = Double.valueOf(paramsLatLng[1]);
        double longitude = Double.valueOf(paramsLatLng[2]);
        String date = paramsLatLng[3];

        try {
            TCoordonnee coord = tCoordonneeApiService.insertTCoordonnee(id, latitude, longitude, date).execute();

            if(coord != null){
                return String.valueOf(coord.getId());
            }
            else{
                return ConstanteMetier.NO_COORD_INSERTED_ERROR_CODE;
            }
        } catch (IOException e) {
            return e.getMessage();
        }
    }

}
