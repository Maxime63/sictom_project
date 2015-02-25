package com.savajolchauvet.isima.sictomproject.activity.fragment;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.savajolchauvet.isima.sictomproject.R;


public class CurrentPosition extends Fragment implements OnMapReadyCallback, LocationListener {
    //GPS Configurations
    public static final long INTERVAL_TIME_UPDATE = 100;
    public static final float INTERVAL_MIN_DISTANCE_UPDATE = 0;


    private GoogleMap mMap;

    //GPS
    private LocationManager mLocationManager;


    public CurrentPosition() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_position, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.current_position_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(mLocationManager == null){
            mLocationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_TIME_UPDATE, INTERVAL_MIN_DISTANCE_UPDATE, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        StringBuilder msg = new StringBuilder();
        msg.append("lat : ").append(lat).append("; lng : ").append(lng);

        LatLng newLatLng = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 15));

        mMap.clear();

        mMap.addMarker(new MarkerOptions()
                .title(msg.toString())
                .position(newLatLng));
    }

    @Override
    public void onStop() {
        super.onStop();
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
