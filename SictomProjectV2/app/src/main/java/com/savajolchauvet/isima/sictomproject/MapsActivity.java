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
import com.savajolchauvet.isima.bdd.TCoordonneesDataSource;
import com.savajolchauvet.isima.service.CoordPushService;

import java.util.Date;
import java.util.Timer;

public class MapsActivity extends FragmentActivity implements LocationListener {
    //GPS Configurations
    public static final long INTERVAL_TIME_UPDATE = 5000;
    public static final float INTERVAL_MIN_DISTANCE_UPDATE = 0;

    // Might be null if Google Play services APK is not available.
    private GoogleMap mMap;

    //GPS
    private LocationManager mLocationManager;

    //Datasource for coords
    private TCoordonneesDataSource mCoordonneesDataSource;

    //Service for upload data
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mCoordonneesDataSource = TCoordonneesDataSource.getInstance(this);
        setUpMapIfNeeded();

        mServiceIntent = new Intent(this, CoordPushService.class);
        startService(mServiceIntent);

        //Coordonnée de départ
        LatLng clermont = new LatLng(45.7796600, 3.0862800);
        mMap.addMarker(new MarkerOptions()
                .title("Clermont-Ferrand")
                .position(clermont));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clermont, 15));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_TIME_UPDATE, INTERVAL_MIN_DISTANCE_UPDATE, this);
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
        mCoordonneesDataSource.open();
        mCoordonneesDataSource.createCoordonnee(lat, lng,new Date(System.currentTimeMillis()) );
        mCoordonneesDataSource.close();

        //Si on a atteint le nombre max de coordonnées en base, alors on upload
        if(mCoordonneesDataSource.getNbCoords() >= TCoordonneesDataSource.NB_MAX_COORDS){
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
