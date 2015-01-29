package com.savajolchauvet.isima.sictomproject;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Maps;
import com.savajolchauvet.isima.bdd.TCoordonneesDataSource;
import com.savajolchauvet.isima.constante.ConstanteMetier;
import com.savajolchauvet.isima.service.CoordPushService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class MapsActivity extends FragmentActivity implements LocationListener {
    //Logger
    private static final Logger logger = Logger.getLogger(MapsActivity.class.getName());

    //GPS Configurations
    public static final long INTERVAL_TIME_UPDATE = 5000;
    public static final float INTERVAL_MIN_DISTANCE_UPDATE = 0;

    // Might be null if Google Play services APK is not available.
    private GoogleMap mMap;

    //GPS
    private LocationManager mLocationManager;

    //Datasource for coords
    private TCoordonneesDataSource mTCoordonneesDataSource;
    private List<String> mWaitingCoords;

    //Service for upload data
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpIfNeeded() {
        if(mLocationManager == null){
            mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_TIME_UPDATE, INTERVAL_MIN_DISTANCE_UPDATE, this);
        }

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            logger.info("Create new map");
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }

        if(mWaitingCoords == null){
            logger.info("Create new waiting coords list");
            mWaitingCoords = new ArrayList<>();
        }

        if(mTCoordonneesDataSource == null){
            logger.info("Create new TCoordonneesDataSource");
            mTCoordonneesDataSource = TCoordonneesDataSource.getInstance(this);

            //Open database first time for create db
            mTCoordonneesDataSource.openWrite();
            mTCoordonneesDataSource.close();
        }

        if(mServiceIntent == null){
            logger.info("Create new service intent");
            mServiceIntent = new Intent(this, CoordPushService.class);
            startService(mServiceIntent);
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //Coordonnée de départ
        LatLng clermont = new LatLng(45.7796600, 3.0862800);
        mMap.addMarker(new MarkerOptions()
                .title("Clermont-Ferrand")
                .position(clermont));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clermont, 15));

    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        StringBuilder msg = new StringBuilder();
        msg.append("lat : ").append(lat).append("; lng : ").append(lng);

        Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show();

        LatLng newLatLng = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 15));


        mMap.addMarker(new MarkerOptions()
                .title(msg.toString())
                .position(newLatLng));

        //Insertion BDD SQLITE
        if(!mTCoordonneesDataSource.isOpen()) {
            mTCoordonneesDataSource.openWrite();

            //Insert waiting coords before insert the new coord
            for(String coord : mWaitingCoords){
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ConstanteMetier.STRING_DATE_FORMAT);
                    String[] coordData = coord.split(ConstanteMetier.SEPARATOR);
                    mTCoordonneesDataSource.createCoordonnee(Double.parseDouble(coordData[0]), Double.parseDouble(coordData[1]), simpleDateFormat.parse(coordData[2]));
                } catch (ParseException e) {
                    logger.info("Parse error during insert waiting coords");
                    e.printStackTrace();
                }
            }

            mWaitingCoords = new ArrayList<>();

            //Insert new coord
            mTCoordonneesDataSource.createCoordonnee(lat, lng, new Date(System.currentTimeMillis()));
            mTCoordonneesDataSource.close();
        }
        else{
            logger.info("Insert into waiting coords list");
            DateFormat df = new SimpleDateFormat(ConstanteMetier.STRING_DATE_FORMAT);
            String coord = lat + ConstanteMetier.SEPARATOR + lng + ConstanteMetier.SEPARATOR + df.format(new Date(System.currentTimeMillis()));
            mWaitingCoords.add(coord);
        }

        //Si on a atteint le nombre max de coordonnées en base, alors on upload
        if(mTCoordonneesDataSource.getNbCoords() >= TCoordonneesDataSource.NB_MAX_COORDS){
            startService(mServiceIntent);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
