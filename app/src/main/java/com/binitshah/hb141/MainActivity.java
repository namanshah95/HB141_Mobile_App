package com.binitshah.hb141;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DetailFragment.OnFragmentInteractionListener {

    Toolbar toolbar;
    boolean hideMenu = false;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE;

    //Firebase Variables
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        mAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_id);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open_string, R.string.navigation_drawer_close_string);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_id);
        navigationView.setNavigationItemSelectedListener(this);


        String returningFrom = "nothing"; //todo modify by checking the Intent for an extraString value
        if(mAuth.getCurrentUser() == null){ //check if the person is logged in
            //todo send the user through the onboarding/login process
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
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
        if (mAuth.getCurrentUser() != null) {
            TextView subtitle = (TextView) navigationView.getHeaderView(0).findViewById((R.id.nav_header_subtitle_id));
            subtitle.setText(mAuth.getCurrentUser().getEmail());
        }
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
        } else if (id == R.id.nav_signout_id) {
            hideMenu = true;
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, MainActivity.class));
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
                    place = PlaceAutocomplete.getPlace(this, data);

                    // Initialize Firebase Database Reference
                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    mDatabase.child("establishment").child(place.getId()).child("Name").setValue(place.getName().toString());
                    mDatabase.child("establishment").child(place.getId()).child("Address").setValue(place.getAddress().toString());
                    mDatabase.child("establishment").child(place.getId()).child("Phone Number").setValue(place.getPhoneNumber().toString());
                    mDatabase.child("establishment").child(place.getId()).child("Website").setValue(place.getWebsiteUri().toString());
                    mDatabase.child("establishment").child(place.getId()).child("Place Type").setValue(place.getPlaceTypes());

                    // /MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
                    //fragment.updateMapViewPort(place.getViewport());

                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
    //                Log.i(TAG, status.getStatusMessage());
    
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
        }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (place != null) {
            toolbar.setTitle(place.getName().toString());

            Bundle bundle = new Bundle();
            bundle.putString("eid", place.getId());
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame_id, detailFragment);
            ft.commit();
            hideMenu = true;
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
