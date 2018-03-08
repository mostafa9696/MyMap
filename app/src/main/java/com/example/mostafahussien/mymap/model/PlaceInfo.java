package com.example.mostafahussien.mymap.model;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Mostafa Hussien on 28/02/2018.
 */

public class PlaceInfo {
    private String name;
    private String address;
    private String phoneNumber;
    private LatLng latlng;
    private String attributions;
    private float rating;
    private Uri websiteUri;
    private String id;
    public PlaceInfo(String name, String address, String phoneNumber, String addidress, LatLng latlng, String attributions, float rating, Uri websiteUri) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.latlng = latlng;
        this.attributions = attributions;
        this.rating = rating;
        this.websiteUri = websiteUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public PlaceInfo() {

    }
}
