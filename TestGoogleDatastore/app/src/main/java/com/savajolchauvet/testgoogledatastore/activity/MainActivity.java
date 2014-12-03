package com.savajolchauvet.testgoogledatastore.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.savajolchauvet.testgoogledatastore.constante.ConstanteMetier;
import com.savajolchauvet.testgoogledatastore.endpoint.EndpointAsyncTask;
import com.maximechauvet.testgoogledatastore.R;


public class MainActivity extends Activity {
    private GoogleMap mMap;
    private LatLng mClermont = new LatLng(45.7833, 3.0833);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mClermont, 13));

        mMap.addMarker(new MarkerOptions()
                        .title("Clermont-Ferrand")
                        .snippet("Capitale Auvergnate")
                        .position(mClermont));

        String params = mClermont.latitude + ConstanteMetier.PARAMS_SEPARATOR + mClermont.longitude;

        new EndpointAsyncTask().execute(new Pair<Context, String>(this, params));
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
}
