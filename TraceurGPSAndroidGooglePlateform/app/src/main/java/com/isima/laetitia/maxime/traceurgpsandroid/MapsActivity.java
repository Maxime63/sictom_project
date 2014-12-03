package com.isima.laetitia.maxime.traceurgpsandroid;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity
                          implements GooglePlayServicesClient.ConnectionCallbacks,
                                     GooglePlayServicesClient.OnConnectionFailedListener,
                                     LocationListener{

    //Constantes
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int UPDATE_INTERVAL = 5000;
    private static final int FASTEST_UPDATE_INTERVAL = 1000;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    LocationClient mLocationClient;
    LocationRequest mLocationRequest;
    boolean mUpdatesResquested;

    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setInterval(FASTEST_UPDATE_INTERVAL);

        mSharedPreferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        mLocationClient = new LocationClient(this, this, this);
        mUpdatesResquested = false;

        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mSharedPreferences.contains("KEY_UPDATE_ON")){
            mUpdatesResquested = mSharedPreferences.getBoolean("KEY_UPDATE_ON", false);
        }
        else{
            mEditor.putBoolean("KEY_UPDATE_ON", false);
            mEditor.commit();
        }

        setUpMapIfNeeded();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //On connecte le client
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        if(mLocationClient.isConnected()){
            mLocationClient.removeLocationUpdates(this);
        }

        //on déconnecte le client
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mEditor.putBoolean("KEY_UPDATE_ON", mUpdatesResquested);
        mEditor.commit();
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                switch (resultCode){
                    case Activity.RESULT_OK:
                        break;
                }
                break;
        }
    }

    /**
     * Méthode appelée lors de la connexion au service.
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Connecté", Toast.LENGTH_SHORT).show();
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    /**
     * Méthode appelée lors de la déconnexion au service.
     */
    @Override
    public void onDisconnected(){
        Toast.makeText(this, "Déconnecté, essayez de vous reconnecter", Toast.LENGTH_SHORT).show();
    }

    /**
     * Méthode appelé lors d'un problème de connexion.
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Si google peut résoudre lui même résoudre les erreurs.
        if(connectionResult.hasResolution()){
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        else{
            //Dans le cas où il y a aucune résolution possible, alors on affiche le code d'erreur
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Nouvelle location " + Double.toString(location.getLatitude()) + ";" + Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Nouvelle position"));
    }

    /**
     * Permet de vérifier que les services google play sont disponnibles.
     * @return true si les services sont connectés, sinon false.
     */
    private boolean servicesConnected(){
        //Permettra de savoir si les services google play sont bien connectés ou non.
        boolean isConnected = true;
        //On vérifie que les services google play sont bien disponibles
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        //Si les services google play sont bien disponibles
        if(ConnectionResult.SUCCESS != resultCode){
            //On récupère errorDialog renvoyé par google play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

            //Si le google play service peut nous permettre d'afficher une errorDialog
            if(errorDialog != null){
                //On créer un fragment pour l'affichage de l'erreur.
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                //On lui affecte l'errorDialog retourné par le google play services.
                errorFragment.setDialog(errorDialog);
                //On affiche l'erreur.
                errorFragment.show(getFragmentManager(), "Traceur GPS");
            }

            isConnected = false;
        }

        return isConnected;
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Position de départ"));
    }

    void showErrorDialog(int code) {
        GooglePlayServicesUtil.getErrorDialog(code, this, CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
    }

    /**
     * Déclaration d'un DialogFragment permettant d'afficher les erreurs.
     */
    public static class ErrorDialogFragment extends DialogFragment{
        //Le champ qui contient les dialog.
        private Dialog mDialog;

        /**
         * Constructeur par défaut.
         * Initialise un dialogue à null.
         */
        public ErrorDialogFragment(){
            super();
            mDialog = null;
        }

        /**
         * Permet d'affecter le dialog à afficher.
         * @param dialog le dialogue à affecter à afficher.
         */
        public void setDialog(Dialog dialog){
            mDialog = dialog;
        }

        /**
         * Méthode appelée lors de la création du dialogue courant.
         * @param savedInstanceState le bundle de l'état courant.
         * @return Retourne le dialogue au DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
