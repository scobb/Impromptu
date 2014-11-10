package com.example.steve.impromptu.Entity;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Sean on 11/8/2014.
 */
public class ImpromptuLocation {

    private String formattedAddress;
    private LatLng coordinates;

    private ImpromptuLocation(){ }

    public ImpromptuLocation(String add, LatLng ll)
    {
        formattedAddress = add;
        coordinates = ll;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

}