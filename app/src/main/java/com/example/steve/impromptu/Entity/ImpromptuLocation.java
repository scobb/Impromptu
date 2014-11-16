package com.example.steve.impromptu.Entity;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Sean on 11/8/2014.
 */
public class ImpromptuLocation {

    private String formattedAddress;
    private String name;
    private LatLng coordinates;

    private ImpromptuLocation(){ }

    public ImpromptuLocation(String add, String n, LatLng ll)
    {
        formattedAddress = add;
        name = n;
        coordinates = ll;
    }

    public String toString(){
        String res = name;
        if(!name.equals("")) {
            res += "/n";
        }
        res += name + "/n" + coordinates;
        return res;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}