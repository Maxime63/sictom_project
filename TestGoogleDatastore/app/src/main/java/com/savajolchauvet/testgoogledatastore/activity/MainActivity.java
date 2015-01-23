package com.savajolchauvet.testgoogledatastore.activity;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maximechauvet.testgoogledatastore.R;
import com.savajolchauvet.testgoogledatastore.bdd.DatabaseHelper;
import com.savajolchauvet.testgoogledatastore.endpoint.EndpointAsyncTask;

import java.text.ParseException;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity implements LocationListener {
    public static final long INTERVAL_TIME_UPDATE = 5000;
    public static final float INTERVAL_MIN_DISTANCE_UPDATE = 0;

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private DatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbh = new DatabaseHelper(this);
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //On obtient la référence du service.
        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_TIME_UPDATE, INTERVAL_MIN_DISTANCE_UPDATE, this);


        //Si le GPS est disponible, alors on s'y abonne
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            abonnementGps();

        }

    }

    @Override
    protected void onPause() {
        //ATTENTION NE PAS ARRETER L'APPLICATION !!!

        super.onPause();
        uploadData();
        desabonnementGps();
    }

    private void abonnementGps() {
        //Mise à jour toutes les 5sec et pour une distance minimale de différence. Ici elle est
        //à 0 pour effectuer les tests
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_TIME_UPDATE, INTERVAL_MIN_DISTANCE_UPDATE, this);
    }

    private void desabonnementGps() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        uploadData();
        desabonnementGps();
    }

    private void uploadData() {
        try {
            List<String> coords = dbh.getAllCoordonnees();

            for(String coord : coords){
                new EndpointAsyncTask().execute(new Pair<Context, String>(this, coord));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        //Insertion en BDD !
        dbh.addCoordonnee(lat, lng, new Date(System.currentTimeMillis()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Connexion au GPS", Toast.LENGTH_SHORT);
        if(provider.equals("gps")){
            abonnementGps();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Déconnexion au GPS", Toast.LENGTH_SHORT);
        if(provider.equals("gps")){
            desabonnementGps();
        }
    }
}
