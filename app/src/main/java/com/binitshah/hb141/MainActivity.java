package com.binitshah.hb141;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
        implements NavigationView.OnNavigationItemSelectedListener, DetailFragment.OnFragmentInteractionListener {

    Toolbar toolbar;
    private NavigationView navigationView;
    boolean hideMenu = false;
    private final String TAG = "HB141Log";
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
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
                                subtitle.setText(subtitle.getText() + " | " + rep.toString() + " Points");
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
                        new DownloadImageTask(propic).execute(user.getPhotoUrl().toString());
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
        String returningFrom = "nothing"; //todo modify by checking the Intent for an extraString value
        FragmentTransaction ft;
        switch (returningFrom) {
            case "prevreports":
                //set the fragment to Previous Reports
                toolbar.setTitle(getResources().getString(R.string.nav_prevreports_string));
                navigationView.setCheckedItem(R.id.nav_prevreports_id);
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame_id, new PreviousReportsFragment());
                ft.commit();
                hideMenu = true;
                invalidateOptionsMenu();
                break;
            case "settings":
                //set the fragment to Settings
                toolbar.setTitle(getResources().getString(R.string.nav_settings_string));
                navigationView.setCheckedItem(R.id.nav_settings_id);
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame_id, new SettingsFragment());
                ft.commit();
                hideMenu = true;
                invalidateOptionsMenu();
                break;
            default:
                //default is to set the fragment to Maps Fragment
                toolbar.setTitle(getResources().getString(R.string.nav_chooselocation_string));
                navigationView.setCheckedItem(R.id.nav_chooselocation_id);
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame_id, new MapFragment());
                ft.commit();
                hideMenu = false;
                invalidateOptionsMenu();
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
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            //delete other locally saved data
            startActivity(new Intent(MainActivity.this, OnboardingActivity.class));
            finish();
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
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                place = PlaceAutocomplete.getPlace(this, data);

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

    @Override
    public void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView propicImageView;
        String propicUrl;

        DownloadImageTask(CircleImageView propicImageViewHolder) {
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
