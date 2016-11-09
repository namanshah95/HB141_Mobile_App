package com.binitshah.hb141;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    boolean hideMenu = false;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_id);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open_string, R.string.navigation_drawer_close_string);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_id);
        navigationView.setNavigationItemSelectedListener(this);


        boolean loggedIn = true; //todo modify by checking the SharedPreference for whether the user has been logged in or maybe Firebase will provide it.
        String returningFrom = "nothing"; //todo modify by checking the Intent for an extraString value
        if(!loggedIn){ //check if the person is logged in
            //todo send the user through the onboarding/login process
        }
        else if(returningFrom.equals("prevreports")){
            //set the fragment to Previous Reports
            toolbar.setTitle(getResources().getString(R.string.nav_prevreports_string));
            navigationView.setCheckedItem(R.id.nav_prevreports_id);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame_id, new PreviousReportsFragment());
            ft.commit();
            hideMenu = true;
            invalidateOptionsMenu();
        }
        else if(returningFrom.equals("settings")){
            //set the fragment to Settings
            toolbar.setTitle(getResources().getString(R.string.nav_settings_string));
            navigationView.setCheckedItem(R.id.nav_settings_id);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame_id, new SettingsFragment());
            ft.commit();
            hideMenu = true;
            invalidateOptionsMenu();
        }
        else {
            //default is to set the fragment to Maps Fragment
            toolbar.setTitle(getResources().getString(R.string.nav_chooselocation_string));
            navigationView.setCheckedItem(R.id.nav_chooselocation_id);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame_id, new MapFragment());
            ft.commit();
            hideMenu = false;
            invalidateOptionsMenu();
        }
        startActivity(new Intent(this, OnboardingActivity.class));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_id);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_chooselocation_id) {
            toolbar.setTitle(getResources().getString(R.string.nav_chooselocation_string));
            hideMenu = false;
            fragment = new MapFragment();
        } else if (id == R.id.nav_prevreports_id) {
            hideMenu = true;
            toolbar.setTitle(getResources().getString(R.string.nav_prevreports_string));
            fragment = new PreviousReportsFragment();
        } else if (id == R.id.nav_moreinfo_id) {
            hideMenu = true;
            toolbar.setTitle(getResources().getString(R.string.nav_moreinfo_string));
            fragment = new InfoFragment();
        } else if (id == R.id.nav_settings_id) {
            hideMenu = true;
            toolbar.setTitle(getResources().getString(R.string.nav_settings_string));
            fragment = new SettingsFragment();
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame_id, fragment);
            ft.commit();
            invalidateOptionsMenu();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_id);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        if(hideMenu){
            menu.getItem(0).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search_location:
                PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    //TODO: Handle Error
                } catch (GooglePlayServicesNotAvailableException e) {
                    //TODO: Handle exception
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
    //                Log.i(TAG, "Place: " + place.getName());
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
    //                Log.i(TAG, status.getStatusMessage());
    
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
        }

}
