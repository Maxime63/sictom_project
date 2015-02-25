package com.savajolchauvet.isima.sictomproject.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appspot.speedy_baton_840.sictomApi.model.TCoordonnee;
import com.google.android.gms.common.api.f;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.savajolchauvet.isima.sictomproject.R;
import com.savajolchauvet.isima.sictomproject.constante.ConstanteMetier;
import com.savajolchauvet.isima.sictomproject.endpoint.GetCoordsByTourneeAsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class FullTrip extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private PolylineOptions fullPath;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_full_trip, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.full_trip_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fullPath = new PolylineOptions();

        LatLngBounds latLngBounds;

        double lat;
        double lng;
        double minLat;
        double minLng;
        double maxLat;
        double maxLng;

        //Recup les coodonnées
        try {
            List<TCoordonnee> coords = new GetCoordsByTourneeAsyncTask().execute(new Pair<Context, String>(getActivity(), String.valueOf(mTourneeId))).get();
            if(coords != null && coords.size() > 0){
                minLat = maxLat = coords.get(0).getLatitude();
                minLng = maxLng = coords.get(0).getLongitude();

                for(TCoordonnee coord : coords){
                    lat = coord.getLatitude();
                    lng = coord.getLongitude();

                    if(lat < minLat){ minLat = lat; }

                    if(lat > maxLat){ maxLat = lat; }

                    if(lng < minLng){ minLng = lng; }

                    if(lng > maxLng){ maxLng = lng; }

                    LatLng latLng = new LatLng(lat, lng);
                    fullPath.add(latLng);
                }

                mMap.addPolyline(fullPath);
                latLngBounds = new LatLngBounds(new LatLng(minLat, minLng), new LatLng(maxLat, maxLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
            }
            else{
                Toast.makeText(getActivity(), "Pas de coordonnées synchronisé pour le moment", Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
