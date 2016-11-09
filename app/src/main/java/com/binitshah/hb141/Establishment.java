package com.binitshah.hb141;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by bshah on 11/9/2016.
 */

@IgnoreExtraProperties
public class Establishment {
    private String website;
    private String phoneNumber;
    private List<Integer> placeTypes;
    private String name;
    private String placeId;
    private String address;

    public Establishment() {
    }

    public Establishment(String website, String phoneNumber, List<Integer> placeTypes, String name, String placeId, String address) {
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.placeTypes = placeTypes;
        this.name = name;
        this.placeId = placeId;
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
