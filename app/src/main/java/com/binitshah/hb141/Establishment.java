package com.binitshah.hb141;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;
import java.util.Locale;

/**
 * Created by bshah on 11/9/2016.
 * Serves as establishment data holder
 * NOTE: this is not the establishment class that gets updated to firebase. there is data here that isn't needed up there, and futhermore, the establishment in firebase needs ids of reports that have been filled out on that location
 */

class Establishment {
    private String address;
    private String attributions;
    private String id;
    private LatLng latLng;
    private Locale locale;
    private String name;
    private String phoneNumber;
    private List<Integer> placeTypes;
    private LatLngBounds viewport;
    private Uri websiteUri;
    private float likelihood;

    public Establishment(String address, String attributions, String id, LatLng latLng, Locale locale, String name, String phoneNumber, List<Integer> placeTypes, LatLngBounds viewport, Uri websiteUri, float likelihood) {
        this.address = address;
        this.attributions = attributions;
        this.id = id;
        this.latLng = latLng;
        this.locale = locale;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.placeTypes = placeTypes;
        this.viewport = viewport;
        this.websiteUri = websiteUri;
        this.likelihood = likelihood;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Integer> getPlaceTypes() {
        return placeTypes;
    }

    public void setPlaceTypes(List<Integer> placeTypes) {
        this.placeTypes = placeTypes;
    }

    public LatLngBounds getViewport() {
        return viewport;
    }

    public void setViewport(LatLngBounds viewport) {
        this.viewport = viewport;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public float getLikelihood() {
        return likelihood;
    }

    public void setLikelihood(float likelihood) {
        this.likelihood = likelihood;
    }
}
