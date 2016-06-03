package it.polito.mad.groupFive.restaurantcode.listeners;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.polito.mad.groupFive.restaurantcode.Home;

public class LocationListener implements android.location.LocationListener {
    private LocationManager lm;
    private Home.MenuAdapter ma;
    private Context context;

    public LocationListener(LocationManager lm, Home.MenuAdapter ma, Context context){
        this.lm = lm;
        this.ma = ma;
        this.context = context;
    }
    @Override
    public void onLocationChanged(Location location) {
        final String METHOD_NAME = this.getClass().getName()+" - onLocationChanged";
        lm.removeUpdates(this);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRoot = db.getReference("restaurant");
        Query menuQuery = dbRoot.limitToFirst(10); //Get the first 10 restaurants
        menuQuery.addListenerForSingleValueEvent(new GetMenusIDFromRestaurantListener(this.ma,location,context));
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
