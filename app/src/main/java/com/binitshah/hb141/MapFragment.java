package com.binitshah.hb141;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

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


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                final LatLngBounds NEWENGLAND = new LatLngBounds(
                        new LatLng(41, -73), new LatLng(47, -68));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLocation, 6)); //defaults to atlanta
            }
        });

        //this segment initializes the googleapiclient for places api use
        buildGoogleApiClient();


        return v;
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
//    @Override
//    public void onConnected(@Nullable Bundle bundle){
//        Log.d(LOG, "CONNECTION CONNECTED");
//    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Location lastLocation = null;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
        if (lastLocation != null) {
            mLastLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        }
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

