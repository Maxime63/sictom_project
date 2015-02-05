package com.savajolchauvet.isima.sictomproject.activity;

import android.app.Fragment;
import android.content.res.Configuration;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.savajolchauvet.isima.sictomproject.R;
import com.savajolchauvet.isima.sictomproject.activity.fragment.FullTrip;
import com.savajolchauvet.isima.sictomproject.activity.fragment.MapsGPS;
import com.savajolchauvet.isima.sictomproject.activity.fragment.Signin;
import com.savajolchauvet.isima.sictomproject.activity.navigation.CustomDrawerAdapter;
import com.savajolchauvet.isima.sictomproject.activity.navigation.DrawerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class MainActivity extends ActionBarActivity {
    private static final Logger logger = Logger.getLogger(MainActivity.class.getName());

    //Drawer list property
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private CustomDrawerAdapter mAdapter;


    private List<DrawerItem> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataList = new ArrayList<DrawerItem>();
        mTitle = mDrawerTitle = getTitle();
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
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

        switch (position){
            case 0:
                fragment = new MapsGPS();
                break;
            case 1:
                fragment = new FullTrip();
                break;
            case 2:
                fragment = new Signin();
                break;
            case 3:
                break;
            default:
        }

        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
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
