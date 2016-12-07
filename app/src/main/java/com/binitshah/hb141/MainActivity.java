package com.binitshah.hb141;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    private NavigationView navigationView;
    boolean hideMenu = false;
    private static final String TAG = "HB141Log";
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final int NAV_CHOOSE_LOCATION = 0;
    public static final int NAV_PREVIOUS_REPORTS = 1;
    public static final int NAV_MORE_INFO = 2;
    public static final int NAV_SETTINGS = 3;
    Context context;


    //Firebase Variables
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.nav_chooselocation_string));
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        context = getApplicationContext();

        //facebook analytics
        FacebookSdk.sdkInitialize(context);
        AppEventsLogger.activateApp(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_id);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open_string, R.string.navigation_drawer_close_string);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_id);
        navigationView.setNavigationItemSelectedListener(this);

        //check if the person is logged in
        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
        }

        switchNavToProperFragment();
        setUpNavHeader();
    }

    private void setUpNavHeader() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    TextView maintitle = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_maintitle_id);
                    final TextView subtitle = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_subtitle_id);
                    CircleImageView propic = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_propic_id);
                    maintitle.setText(user.getDisplayName());
                    subtitle.setText(user.getEmail());

                    DatabaseReference ref = mDatabase.child("users").child(user.getUid()).child("reputation");
                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Object rep = dataSnapshot.getValue();
                            if(rep != null) {
                                subtitle.setText(subtitle.getText() + " | " + rep.toString() + " points");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "Getting data from firebase database failed.", databaseError.toException());
                        }
                    };
                    ref.addListenerForSingleValueEvent(postListener);

                    //see if we have the profile picture locally stored and if so, then set it.
                    if(doesProfilePicExist()) {
                        Log.d(TAG, "It does and we've set it as a temp.");
                        propic.setImageBitmap(BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/hb141/propic.jpg"));
                    }

                    //download the profile picture from firebase
                    try {
                        new DownloadProPic(propic).execute(user.getPhotoUrl().toString());
                    }
                    catch (NullPointerException e) {
                        Snackbar.make(findViewById(android.R.id.content), "Unable to retrieve user data", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }

    private boolean doesProfilePicExist() {
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/hb141/propic.jpg");
            return file.exists();
        }
        catch(NullPointerException e){
            Log.e(TAG, "Local Image doesn't exist.", e);
            return false;
        }
    }

    private void switchNavToProperFragment() {
        int returnCode = NAV_CHOOSE_LOCATION; //todo modify by checking the Intent for an extraInt value

        switch (returnCode) {
            case NAV_CHOOSE_LOCATION:
                onNavigationItemSelected(navigationView.getMenu().getItem(NAV_CHOOSE_LOCATION));
                navigationView.setCheckedItem(R.id.nav_chooselocation_id);
                break;
            case NAV_PREVIOUS_REPORTS:
                onNavigationItemSelected(navigationView.getMenu().getItem(NAV_PREVIOUS_REPORTS));
                navigationView.setCheckedItem(R.id.nav_prevreports_id);
                break;
            case NAV_MORE_INFO:
                onNavigationItemSelected(navigationView.getMenu().getItem(NAV_MORE_INFO));
                navigationView.setCheckedItem(R.id.nav_moreinfo_id);
                break;
            case NAV_SETTINGS:
                onNavigationItemSelected(navigationView.getMenu().getItem(NAV_SETTINGS));
                navigationView.setCheckedItem(R.id.nav_settings_id);
                break;
            default:
                onNavigationItemSelected(navigationView.getMenu().getItem(NAV_CHOOSE_LOCATION));
                navigationView.setCheckedItem(R.id.nav_chooselocation_id);
                break;
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.nav_chooselocation_id:
                toolbar.setTitle(getResources().getString(R.string.nav_chooselocation_string));
                hideMenu = false;
                fragment = new MapFragment();
                break;
            case R.id.nav_prevreports_id:
                hideMenu = true;
                toolbar.setTitle(getResources().getString(R.string.nav_prevreports_string));
                fragment = new PreviousReportsFragment();
                break;
            case R.id.nav_moreinfo_id:
                hideMenu = true;
                toolbar.setTitle(getResources().getString(R.string.nav_moreinfo_string));
                fragment = new InfoFragment();
                break;
            case R.id.nav_settings_id:
                hideMenu = true;
                toolbar.setTitle(getResources().getString(R.string.nav_settings_string));
                fragment = new SettingsFragment();
                break;
        }

        /*
            hideMenu = true;
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            //delete other locally saved data
            startActivity(new Intent(MainActivity.this, OnboardingActivity.class));
            finish();
         */

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
        switch (item.getItemId()) {
            case R.id.search_location:
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, "Google Play Error", e);
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
                Log.d(TAG, "Place data: " + place + " | " + place.getId() + place.getAddress());
            }
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            Log.i(TAG, status.getStatusMessage());
            Snackbar.make(findViewById(android.R.id.content), "Unable to retrieve search results", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private class DownloadProPic extends AsyncTask<String, Void, Bitmap> {
        CircleImageView propicImageView;
        String propicUrl;

        DownloadProPic(CircleImageView propicImageViewHolder) {
            this.propicImageView = propicImageViewHolder;
        }

        protected Bitmap doInBackground(String... urls) {
            propicUrl = urls[0];
            Bitmap downloadedProPic = null;
            if(propicUrl != null && !propicUrl.equals("")) {
                try {
                    InputStream in = new java.net.URL(propicUrl).openStream();
                    downloadedProPic = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e(TAG, "Error getting the profilepicture from online", e);
                }
            }
            return downloadedProPic;
        }

        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                propicImageView.setImageBitmap(result);
                try {
                    File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "hb141");
                    file.mkdir();
                    File image = new File(file.getAbsoluteFile(), "propic.jpg");
                    FileOutputStream out = new FileOutputStream(image);
                    result.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    Log.d(TAG, "Propic was saved locally.");
                }
                catch (Exception e) {
                    Log.d(TAG, "Propic was NOT saved locally.");
                }
            }
        }
    }
}
