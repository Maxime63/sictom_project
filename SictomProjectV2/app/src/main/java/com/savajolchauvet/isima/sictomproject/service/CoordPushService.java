package com.savajolchauvet.isima.sictomproject.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Pair;

import com.appspot.speedy_baton_840.sictomApi.model.TCoordonnee;
import com.savajolchauvet.isima.sictomproject.bdd.TCoordonneesDataSource;
import com.savajolchauvet.isima.sictomproject.constante.ConstanteMetier;
import com.savajolchauvet.isima.sictomproject.endpoint.TCoordonneeEndpointAsyncTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private static final long UPLOAD_TIMER = 600000;//10s
    private TCoordonneesDataSource mTCoordonneesDataSource;
    private Timer mTimer;

//    private boolean isOpen;

    private NetworkInfo activeNetwork;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            logger.info("Connectivity changed, action received ==> " + action);

            if(action.equals(ConstanteMetier.CONNECTIVITY_CHANGE)){
                logger.info("Connectivity changed, start to upload data");
                if(!mTCoordonneesDataSource.isOpen()){
                    uploadData();
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
        if(!mTCoordonneesDataSource.isOpen()){
            uploadData();
        }
        else{
            logger.info("Database already open");
        }
        logger.info("Connectivity changed, data uploaded");
    }

    private void uploadData() {
        logger.info("-------->START TO UPLOAD DATA<-----------");

        if(isConnected()) {
            List<TCoordonnee> coords = new ArrayList<>();
            if(!mTCoordonneesDataSource.isOpen()){
                try {
                    mTCoordonneesDataSource.openRead();
                    coords = mTCoordonneesDataSource.getAllCoordonnees();
                    mTCoordonneesDataSource.close();
                } catch (ParseException e) {
                    e.printStackTrace();
                    mTCoordonneesDataSource.close();
                }
            }
            else{
                logger.info("Database already open");
            }

            if(!mTCoordonneesDataSource.isOpen()){
                mTCoordonneesDataSource.openWrite();
                for (TCoordonnee coord : coords) {
                    try {
                        DateFormat df = new SimpleDateFormat(ConstanteMetier.STRING_DATE_FORMAT);
                        Date dateCoord = new Date(coord.getDate().getValue());

                        String coordParam = coord.getId() + ConstanteMetier.SEPARATOR + coord.getLatitude() + ConstanteMetier.SEPARATOR + coord.getLongitude() + ConstanteMetier.SEPARATOR + df.format(dateCoord) + ConstanteMetier.SEPARATOR + coord.getTourneeId();
                        logger.info("Start to upload coord ==> " + coordParam);

                        String id = new TCoordonneeEndpointAsyncTask(mTCoordonneesDataSource).execute(new Pair<Context, String>(this, coordParam)).get();

                        logger.info("Coordonnee uploaded ==> " + id);

                        if (!id.equals(ConstanteMetier.NO_COORD_INSERTED_ERROR_CODE)) {
                            mTCoordonneesDataSource.deleteCoordonnee(Long.parseLong(id));
                        }
                    } catch (InterruptedException | ExecutionException | NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                mTCoordonneesDataSource.close();
            }
            else{
                logger.info("Database already open");
            }
        }
        else{
            logger.info("Not connected to a network");
        }
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
            logger.info("-------------TIMER, START RUN-------------");
            logger.info("UploadTImerTask : Try to upload coords");

            if(!mTCoordonneesDataSource.isOpen()){
                uploadData();
            }
            else{
                logger.info("Database already open");
            }
            logger.info("Connectivity changed, data uploaded");

            logger.info("UploadTImerTask : coords uploaded");
            logger.info("-------------TIMER, RUN FINISHED-----------------");
            mTimer.schedule(new UploadTimerTask(mContext), UPLOAD_TIMER);
        }
    }
}
