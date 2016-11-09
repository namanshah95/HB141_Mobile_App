package com.binitshah.hb141;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * MapsFragment is where users will be able to select the business where they currently are and either report whether the business is hb141 compliant or if there
 * is any suspicious trafficking behavior to report.
 */
public class MapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final String LOG = "HB141Log";

    //Places API
    Context context;
    protected GoogleApiClient mGoogleApiClient;
    protected LatLng mLastLocation;

    //Maps API
    MapView mMapView;
    GoogleMap googleMap;

    //Locations API
    private Location mLocation;
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;

    //Location
    LocationManager mLocationManager;
    long TIME = 10000; //every ten seconds
    float DISTANCE = 3; //every three meters
    private static final int PERMISSION_REQUEST_CODE = 1;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        //this segment will initialize the mapview
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //then this segment uses the mapview to create a googlemap
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap readyMap) {
                googleMap = readyMap;
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.setBuildingsEnabled(true);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.762909, -84.422675), 6)); //defaults to atlanta
            }
        });

        //this segment initializes the googleapiclient for places api use
        buildGoogleApiClient();


        mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if(!checkLocationPermission()) {
            Log.d(LOG, "GPS Location: " + checkLocationPermission());
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }
        else{
            Log.d(LOG, "GPS Location: " + checkLocationPermission());
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME, DISTANCE, mLocationListener);
        }



        return v;
    }

    private boolean checkLocationPermission() {
        int permissionCheckFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionCheckFine == PackageManager.PERMISSION_GRANTED || permissionCheckCoarse == PackageManager.PERMISSION_GRANTED;
    }

    //Location
    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
        }

        @Override
        public void onStatusChanged(String string, int someint, Bundle bundle){

        }

        @Override
        public void onProviderEnabled(String string){

        }

        @Override
        public void onProviderDisabled(String string){

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(LOG,"Request Code: " + requestCode);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    if(checkLocationPermission()) {
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME, DISTANCE, mLocationListener);
                    }
                }
            }
        }
    }

    //callbacks for the maps api and googleapiclient
    @Override
    public void onResume(){
        super.onResume();
        mMapView.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause(){
        super.onPause();
        mMapView.onPause();
        mGoogleApiClient.disconnect();
    }

    //callbacks for the maps api
    @Override
    public void onDestroy(){
        super.onDestroy();
        mMapView.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    //used to build the googleapiclient
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(getActivity(), this)
                .build();
    }


    //callbacks for the googleapiclient
    @Override
    public void onConnected(@Nullable Bundle bundle){
        Log.d(LOG, "CONNECTION CONNECTED");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG, "CONNECTION SUSPENDED: " + String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG, "CONNECTION FAILED: " + connectionResult.getErrorMessage());
    }

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}

