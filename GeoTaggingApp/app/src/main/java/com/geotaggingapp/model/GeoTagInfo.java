package com.geotaggingapp.model;

import java.io.Serializable;

public class GeoTagInfo implements Serializable{

    private double latitude, longitude;
    private String address;
    private byte[] geoTagImage;

    public GeoTagInfo(byte[] geoTagImage, double latitude, double longitude, String address) {
        this.geoTagImage = geoTagImage;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;

    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public byte[] getGeoTagImage() {
        return geoTagImage;
    }
}
