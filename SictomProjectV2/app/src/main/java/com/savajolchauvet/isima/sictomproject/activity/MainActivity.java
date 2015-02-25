package com.savajolchauvet.isima.sictomproject.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appspot.speedy_baton_840.sictomApi.model.TCamion;
import com.appspot.speedy_baton_840.sictomApi.model.TTournee;
import com.appspot.speedy_baton_840.sictomApi.model.TUtilisateur;
import com.savajolchauvet.isima.sictomproject.R;
import com.savajolchauvet.isima.sictomproject.activity.fragment.CurrentPosition;
import com.savajolchauvet.isima.sictomproject.activity.fragment.FinishRouting;
import com.savajolchauvet.isima.sictomproject.activity.fragment.FullTrip;
import com.savajolchauvet.isima.sictomproject.activity.fragment.Map;
import com.savajolchauvet.isima.sictomproject.activity.fragment.Signin;
import com.savajolchauvet.isima.sictomproject.activity.navigation.CustomDrawerAdapter;
import com.savajolchauvet.isima.sictomproject.activity.navigation.DrawerItem;
import com.savajolchauvet.isima.sictomproject.constante.ConstanteMetier;
import com.savajolchauvet.isima.sictomproject.endpoint.InsertTourneeEndpointAsyncTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;


public class MainActivity extends ActionBarActivity{
    private static final Logger logger = Logger.getLogger(MainActivity.class.getName());

    private TUtilisateur mChauffeur;
    private TUtilisateur mFirstRipper;
    private TUtilisateur mSecondRipper;
    private TCamion mCamion;
    private TTournee mTournee;
    private long mTypeTournee;

    //Drawer list property
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private CustomDrawerAdapter mAdapter;
    private List<DrawerItem> mDataList;

    private Fragment mapFragment;
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
        mDataList.add(new DrawerItem(getString(R.string.finish_title), R.drawable.ic_finish));
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
            selectItem(4);
        }
    }

    public void startMaps(TUtilisateur chauffeur, TUtilisateur firstRipper, TUtilisateur secondRipper,
                          TCamion camion, long numero){
        mChauffeur = chauffeur;
        mFirstRipper = firstRipper;
        mSecondRipper = secondRipper;
        mCamion = camion;
        mTypeTournee = numero;

        //Create tournee
        DateFormat df = new SimpleDateFormat(ConstanteMetier.STRING_DATE_FORMAT);


        String params = mChauffeur.getId() + ConstanteMetier.SEPARATOR +
                        mFirstRipper.getId() + ConstanteMetier.SEPARATOR +
                        mSecondRipper.getId() + ConstanteMetier.SEPARATOR +
                        mCamion.getId() + ConstanteMetier.SEPARATOR +
                        numero + ConstanteMetier.SEPARATOR +
                        df.format(new Date(System.currentTimeMillis())) + ConstanteMetier.SEPARATOR +
                        "Tournee" + numero;

        try {
            mTournee = new InsertTourneeEndpointAsyncTask().execute(new Pair<Context, String>(this, params)).get();
            Bundle args = new Bundle();
            args.putLong(ConstanteMetier.TOURNEE_ID_PARAM, mTournee.getId());
            mapFragment = new Map();
            mapFragment.setArguments(args);
            selectItem(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
        Bundle args = new Bundle();

        switch (position){
            case 0:
                fragment = mapFragment;
                break;
            case 1:
                fragment = new CurrentPosition();
                break;
            case 2:
                fragment = new FullTrip();
                args.putLong(ConstanteMetier.TOURNEE_ID_PARAM, mTournee.getId());
                fragment.setArguments(args);
                break;
            case 3:
                ((Map) mapFragment).stopRetrieveData();
                fragment = new FinishRouting();
                args.putLong(ConstanteMetier.TOURNEE_ID_PARAM, mTournee.getId());
                fragment.setArguments(args);
                break;
            case 4:
                fragment = new Signin();
                break;
            case 5:
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
