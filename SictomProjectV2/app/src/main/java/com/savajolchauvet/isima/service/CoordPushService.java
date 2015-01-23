package com.savajolchauvet.isima.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import com.savajolchauvet.isima.bdd.TCoordonneesDataSource;
import com.savajolchauvet.isima.endpoint.TCoordonneeEndpointAsyncTask;
import com.savajolchauvet.isima.sictomproject.backend.endpoint.tCoordonneeApi.model.TCoordonnee;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * Created by Maxime on 21/01/2015.
 */
public class CoordPushService extends IntentService {
    private static final Logger logger = Logger.getLogger(CoordPushService.class.getName());

    private static final long UPLOAD_TIMER = 100000;//10s
    private TCoordonneesDataSource mTCoordonneesDataSource;
    private Timer mTimer;

    public CoordPushService(){
        super(CoordPushService.class.getName());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mTCoordonneesDataSource = TCoordonneesDataSource.getInstance(this.getApplicationContext());
        mTimer = new Timer();
        mTimer.schedule(new UploadTimerTask(this), UPLOAD_TIMER);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mTCoordonneesDataSource.open();

        logger.info("-------->START TO UPLOAD DATA<-----------");

        try {
            List<TCoordonnee> coords = mTCoordonneesDataSource.getAllCoordonnees();

            for(TCoordonnee coord : coords){
                try {
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    String coordParam = coord.getId() + ";" + coord.getLatitude() + ";" + coord.getLongitude() + ";" + df.format(System.currentTimeMillis());
                    logger.info("Start to upload coord ==> " + coordParam);
                    String id = new TCoordonneeEndpointAsyncTask(mTCoordonneesDataSource).execute(new Pair<Context, String>(this, coordParam)).get();

                    logger.info("Coordonnee uploaded ==> " + id);

                    mTCoordonneesDataSource.deleteCoordonnee(Long.parseLong(id));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        logger.info("----------->DATA UPLOADED<-------------");
        mTCoordonneesDataSource.close();
    }

    private class UploadTimerTask extends TimerTask {
        private Context mContext;

        public UploadTimerTask(Context context) {
            mContext = context;
            logger.info("UploadTImerTask : new TimerTask created");
        }

        @Override
        public void run() {
            mTCoordonneesDataSource.open();

            logger.info("UploadTImerTask : Try to upload coords");

            List<TCoordonnee> coords = null;
            try {
                coords = mTCoordonneesDataSource.getAllCoordonnees();
                for (TCoordonnee coord : coords) {
                    try {
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        String coordParam = coord.getId() + ";" + coord.getLatitude() + ";" + coord.getLongitude() + ";" + df.format(System.currentTimeMillis());
                        String id = new TCoordonneeEndpointAsyncTask(mTCoordonneesDataSource).execute(new Pair<Context, String>(mContext, coordParam)).get();

                        mTCoordonneesDataSource.deleteCoordonnee(Long.parseLong(id));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            logger.info("UploadTImerTask : coords uploaded");

            mTCoordonneesDataSource.close();
            mTimer.schedule(new UploadTimerTask(mContext), UPLOAD_TIMER);
        }
    }
}
