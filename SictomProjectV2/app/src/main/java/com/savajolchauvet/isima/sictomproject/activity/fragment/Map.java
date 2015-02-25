package com.savajolchauvet.isima.sictomproject.activity.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appspot.speedy_baton_840.sictomApi.model.TTournee;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.savajolchauvet.isima.sictomproject.R;
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

/**
 * Created by Maxime on 18/02/2015.
 */
public class Map extends Fragment implements OnMapReadyCallback, LocationListener {
    private static final Logger logger = Logger.getLogger(Map.class.getName());
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

    private View view;

    private long mTourneeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        if(args != null){
            mTourneeId = args.getLong(ConstanteMetier.TOURNEE_ID_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_maps, container, false);
        }catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpIfNeeded();
    }

    private void setUpIfNeeded() {
        if(mLocationManager == null){
            mLocationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_TIME_UPDATE, INTERVAL_MIN_DISTANCE_UPDATE, this);
        }

        if(mWaitingCoords == null){
            logger.info("Create new waiting coords list");
            mWaitingCoords = new ArrayList<>();
        }

        if(mTCoordonneesDataSource == null){
            logger.info("Create new TCoordonneesDataSource");
            mTCoordonneesDataSource = TCoordonneesDataSource.getInstance(getActivity());

            //Open database first time for create db
            mTCoordonneesDataSource.openWrite();
            mTCoordonneesDataSource.close();
        }

        if(mServiceIntent == null){
            logger.info("Create new service intent");
            mServiceIntent = new Intent(getActivity(), CoordPushService.class);
            getActivity().startService(mServiceIntent);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        StringBuilder msg = new StringBuilder();
        msg.append("lat : ").append(lat).append("; lng : ").append(lng);

        Toast.makeText(getActivity(), msg.toString(), Toast.LENGTH_SHORT).show();

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
                    mTCoordonneesDataSource.createCoordonnee(Double.parseDouble(coordData[0]), Double.parseDouble(coordData[1]), simpleDateFormat.parse(coordData[2]), mTourneeId);
                } catch (ParseException e) {
                    logger.info("Parse error during insert waiting coords");
                    e.printStackTrace();
                }
            }

            mWaitingCoords = new ArrayList<>();

            //Insert new coord
            mTCoordonneesDataSource.createCoordonnee(lat, lng, new Date(System.currentTimeMillis()), mTourneeId);
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
            getActivity().startService(mServiceIntent);
        }
    }

    public void stopRetrieveData(){
        mLocationManager.removeUpdates(this);
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
