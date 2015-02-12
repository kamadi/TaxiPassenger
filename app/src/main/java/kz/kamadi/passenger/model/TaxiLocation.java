package kz.kamadi.passenger.model;

import android.location.Location;

/**
 * Created by Madiyar on 09.02.2015.
 */
public class TaxiLocation {

    private double latitude;
    private double longitude;


    public TaxiLocation(Location location){
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
    }
    public TaxiLocation(double longitude,double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
