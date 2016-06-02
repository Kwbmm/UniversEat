package it.polito.mad.groupFive.restaurantcode.listeners;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class LocationListener implements android.location.LocationListener {
    @Override
    public void onLocationChanged(Location location) {
        final String METHOD_NAME = this.getClass().getName()+" - onLocationChanged";
        Log.d(METHOD_NAME,"Lat: "+location.getLatitude()+"\nLong: "+location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
