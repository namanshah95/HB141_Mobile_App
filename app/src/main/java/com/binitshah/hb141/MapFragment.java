package com.binitshah.hb141;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * MapsFragment is where users will be able to select the business where they currently are and either report whether the business is hb141 compliant or if there
 * is any suspicious trafficking behavior to report.
 */
public class MapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = "HB141Log";
    Establishment establishment;
    Context context;
    private Handler mHandler = new Handler();
    ArrayList<Establishment> establishments = new ArrayList<>();
    private ProgressDialog pDialog;
    private RecyclerView establishment_rv;

    //Places API
    protected GoogleApiClient mGoogleApiClient;

    //Maps API
    MapView mMapView;
    GoogleMap googleMap;

    //Locations API
    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 3;

    public static MapFragment newInstance(Establishment establishmentHolder) {
        MapFragment mMapFragment = new MapFragment();
        mMapFragment.setEstablishment(establishmentHolder);
        return mMapFragment;
    }

    public void setEstablishment(Establishment establishmentHolder) {
        this.establishment = establishmentHolder;
    }

    public MapFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Finding nearby establishments...");
        establishment_rv = (RecyclerView) v.findViewById(R.id.establishment_recyclerview);

        try {
            //this segment will initialize the mapview
            mMapView = (MapView) v.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();// needed to get the map to display immediately
            MapsInitializer.initialize(context);
        } catch (Exception e) {
            Log.e(TAG, "Darn", e);
        }

        //then this segment uses the mapview to create a googlemap
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap readyMap) {
                googleMap = readyMap;
                Log.d(TAG, "Map is ready.");
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.setBuildingsEnabled(true);
            }
        });

        //this segment initializes the googleapiclient for places api use
        buildGoogleApiClient();

        if(establishment == null) {
            getNearbyPlaces();
        }

        return v;
    }

    private void getNearbyPlaces() {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            pDialog.show();
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                    for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
                        Log.d(TAG, "unsorted:" + placeLikelihood.getPlace().getName() + " | " + placeLikelihood.getLikelihood());
                        Place place = placeLikelihood.getPlace();
                        String address = null;
                        String attributions = null;
                        String phoneNumber = null;
                        if(place.getAddress() != null) {
                            address = place.getAddress().toString();
                        }
                        if(place.getAttributions() != null) {
                            attributions = place.getAttributions().toString();
                        }
                        if(place.getPhoneNumber() != null) {
                            phoneNumber = place.getPhoneNumber().toString();
                        }

                        Establishment establishment = new Establishment(
                                address,
                                attributions,
                                place.getId(),
                                place.getLatLng(),
                                place.getLocale(),
                                place.getName().toString(),
                                phoneNumber,
                                place.getPlaceTypes(),
                                place.getViewport(),
                                place.getWebsiteUri(),
                                placeLikelihood.getLikelihood()
                        );
                        establishments.add(establishment);
                    }
                    placeLikelihoods.release();
                    createEstablishmentsViewPager();
                }
            });
        }
        else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void createEstablishmentsViewPager() {
        //let's first sort the establishments by likelihood
        Collections.sort(establishments, new Comparator<Establishment>() {
            public int compare(Establishment a, Establishment b) {
                if(a.getLikelihood() < b.getLikelihood()) {
                    return 1;
                }
                else if(b.getLikelihood() > a.getLikelihood()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        for(Establishment establishment : establishments) {
            Log.d(TAG, "Sorted Establishment" + establishment.getName() + " | likelihood: " + establishment.getLikelihood());
        }

        establishment_rv.setVisibility(View.VISIBLE);
        establishment_rv.setHasFixedSize(true);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(establishment_rv);
        establishment_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        SnapRecyclerAdapter adapter = new SnapRecyclerAdapter(context, establishments);
        establishment_rv.setAdapter(adapter);

        pDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getNearbyPlaces();
                }
                else {
                    final Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Without the location permission, we cannot detect establishments around you. Please search for your current location.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Provide Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    });
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setMaxLines(5);
                    snackbar.show();
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            snackbar.dismiss();
                        }
                    }, 5000);
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
                .build();
    }


    //callbacks for the googleapiclient
    @Override
    public void onConnected(@Nullable Bundle bundle){
        Log.d(TAG, "CONNECTION CONNECTED");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "CONNECTION SUSPENDED: " + String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "CONNECTION FAILED: " + connectionResult.getErrorMessage());
    }

    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}

