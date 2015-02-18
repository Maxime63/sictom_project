package com.savajolchauvet.isima.sictomproject.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.appspot.speedy_baton_840.sictomApi.model.TCamion;
import com.appspot.speedy_baton_840.sictomApi.model.TTournee;
import com.appspot.speedy_baton_840.sictomApi.model.TUtilisateur;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.savajolchauvet.isima.sictomproject.R;
import com.savajolchauvet.isima.sictomproject.activity.fragment.CurrentPosition;
import com.savajolchauvet.isima.sictomproject.activity.fragment.FullTrip;
import com.savajolchauvet.isima.sictomproject.activity.fragment.Signin;
import com.savajolchauvet.isima.sictomproject.activity.navigation.CustomDrawerAdapter;
import com.savajolchauvet.isima.sictomproject.activity.navigation.DrawerItem;
import com.savajolchauvet.isima.sictomproject.bdd.TCoordonneesDataSource;
import com.savajolchauvet.isima.sictomproject.constante.ConstanteMetier;
import com.savajolchauvet.isima.sictomproject.service.CoordPushService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class MainActivity extends ActionBarActivity implements LocationListener, com.google.android.gms.maps.OnMapReadyCallback{
    private static final Logger logger = Logger.getLogger(MainActivity.class.getName());

    private TUtilisateur mChauffeur;
    private TUtilisateur mFirstRipper;
    private TUtilisateur mSecondRipper;
    private TCamion mCamion;
    private TTournee mTournee;

    //Drawer list property
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private CustomDrawerAdapter mAdapter;
    private List<DrawerItem> mDataList;






    private MapFragment mapFragment;

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
        setContentView(R.layout.activity_main);

        mDrawerTitle = getTitle();
        mTitle = getTitle();

        mDataList = new ArrayList<DrawerItem>();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        mDataList.add(new DrawerItem(getString(R.string.maps_title), R.drawable.ic_maps));
        mDataList.add(new DrawerItem(getString(R.string.current_position_title), R.drawable.ic_current_position));
        mDataList.add(new DrawerItem(getString(R.string.full_trip_title), R.drawable.ic_maps_path));
        mDataList.add(new DrawerItem(getString(R.string.signout_title), R.drawable.ic_logout));
        mDataList.add(new DrawerItem(getString(R.string.exit_title), R.drawable.ic_stop));

        mAdapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, mDataList);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setItemsCanFocus(true);
        mDrawerList.setOnItemClickListener(new DrawerListListner());
        logger.info("Menu set up");

        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setHomeAsUpIndicator(0);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            startMaps(null, null, null, null, null);
        }
    }

    public void startMaps(TUtilisateur chauffeur, TUtilisateur firstRipper, TUtilisateur secondRipper,
                          TCamion camion, TTournee tournee){
        mChauffeur = chauffeur;
        mFirstRipper = firstRipper;
        mSecondRipper = secondRipper;
        mCamion = camion;

        //Create tournee
        mapFragment = MapFragment.newInstance();
        //(MapFragment) getFragmentManager().findFragmentById(R.id.map);
        selectItem(0);
        mapFragment.getMapAsync(this);
    }



    private void setUpIfNeeded() {
        if(mLocationManager == null){
            mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_TIME_UPDATE, INTERVAL_MIN_DISTANCE_UPDATE, this);
        }

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap != null) {
            setUpMap();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpIfNeeded();
    }

    private class DrawerListListner implements ListView.OnItemClickListener{
        public DrawerListListner(){
            logger.info("Construction of listner !");
        }

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            logger.info("Listner : position catched ==> " + position);
            selectItem(position);
        }
    }

    private void selectItem(int position){
        logger.info("Selected position ==> " + position);

        Fragment fragment = null;


        switch (position){
            case 0:
                fragment = mapFragment;
                break;
            case 1:
                fragment = new CurrentPosition();
                break;
            case 2:
                fragment = new FullTrip();
                break;
            case 3:
                fragment = new Signin();
                break;
            case 4:
                break;
            default:
        }

        if(position == 0 || position == 1 || position == 2){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);

        if(position == 0){
            transaction.addToBackStack(null);
        }

        transaction.commit();

        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            logger.info("Je passe ici");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
