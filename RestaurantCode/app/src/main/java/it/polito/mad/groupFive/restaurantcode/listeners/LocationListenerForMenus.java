package it.polito.mad.groupFive.restaurantcode.listeners;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.polito.mad.groupFive.restaurantcode.Home;

public class LocationListenerForMenus implements com.google.android.gms.location.LocationListener {
    private GoogleApiClient gac;
    private Home.MenuAdapter ma;
    private Context context;


    public LocationListenerForMenus(GoogleApiClient gac, Home.MenuAdapter ma, Context context) {
        this.gac = gac;
        this.ma = ma;
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location) {
        final String METHOD_NAME = this.getClass().getName()+" - onLocationChanged";
        LocationServices.FusedLocationApi.removeLocationUpdates(this.gac,this);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRoot = db.getReference("restaurant");
        Query menuQuery = dbRoot.limitToFirst(10); //Get the first 10 restaurants
        menuQuery.addListenerForSingleValueEvent(new GetMenusIDFromRestaurantListener(this.ma,location,context));
    }
}
