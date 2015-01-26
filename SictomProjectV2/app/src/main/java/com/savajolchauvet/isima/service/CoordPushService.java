package com.savajolchauvet.isima.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Pair;

import com.savajolchauvet.isima.bdd.TCoordonneesDataSource;
import com.savajolchauvet.isima.constante.ConstanteMetier;
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
public class CoordPushService extends IntentService{
    private static final Logger logger = Logger.getLogger(CoordPushService.class.getName());

    private static final long UPLOAD_TIMER = 100000;//10s
    private TCoordonneesDataSource mTCoordonneesDataSource;
    private Timer mTimer;

    private boolean isOpen;

    private NetworkInfo activeNetwork;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            logger.info("Connectivity changed, action received ==> " + action);

            if(action.equals(ConstanteMetier.CONNECTIVITY_CHANGE)){
                logger.info("Connectivity changed, start to upload data");
                if(!isOpen){
                    isOpen = true;
                    uploadData();
                    isOpen = false;
                }
                else{
                    logger.info("Database already open");
                }
                logger.info("Connectivity changed, data uploaded");
            }
        }
    };

    public CoordPushService(){
        super(CoordPushService.class.getName());
        isOpen = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ConnectivityManager cm = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstanteMetier.CONNECTIVITY_CHANGE);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private boolean isConnected(){
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mTCoordonneesDataSource = TCoordonneesDataSource.getInstance(this.getApplicationContext());
        mTimer = new Timer();
        mTimer.schedule(new UploadTimerTask(this), UPLOAD_TIMER);
        logger.info("Service started");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        logger.info("New intent, try to upload data");
        if(!isOpen){
            isOpen = true;
            uploadData();
            isOpen = false;
        }
        else{
            logger.info("Database already open");
        }
        logger.info("New intent, data uploaded");
    }

    private void uploadData() {
        logger.info("-------->START TO UPLOAD DATA<-----------");
        mTCoordonneesDataSource.open();

        if(isConnected()) {
            try {
                List<TCoordonnee> coords = mTCoordonneesDataSource.getAllCoordonnees();

                for (TCoordonnee coord : coords) {
                    try {
                        DateFormat df = new SimpleDateFormat(ConstanteMetier.STRING_DATE_FORMAT);
                        String coordParam = coord.getId() + ";" + coord.getLatitude() + ";" + coord.getLongitude() + ";" + df.format(System.currentTimeMillis());
                        logger.info("Start to upload coord ==> " + coordParam);
                        String id = new TCoordonneeEndpointAsyncTask(mTCoordonneesDataSource).execute(new Pair<Context, String>(this, coordParam)).get();

                        logger.info("Coordonnee uploaded ==> " + id);

                        if (!id.equals(ConstanteMetier.NO_COORD_INSERTED_ERROR_CODE)) {
                            mTCoordonneesDataSource.deleteCoordonnee(Long.parseLong(id));
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else{
            logger.info("Not connected to a network");
        }
        mTCoordonneesDataSource.close();
        logger.info("----------->DATA UPLOADED<-------------");
    }

    private class UploadTimerTask extends TimerTask {
        private Context mContext;

        public UploadTimerTask(Context context) {
            mContext = context;
            logger.info("UploadTImerTask : new TimerTask created");
        }

        @Override
        public void run() {
            logger.info("UploadTImerTask : Try to upload coords");

            if(!isOpen) {
                isOpen = true;
                uploadData();
                isOpen = false;
            }
            else{
                logger.info("Database already open");
            }

            logger.info("UploadTImerTask : coords uploaded");
            mTimer.schedule(new UploadTimerTask(mContext), UPLOAD_TIMER);
        }
    }
}
