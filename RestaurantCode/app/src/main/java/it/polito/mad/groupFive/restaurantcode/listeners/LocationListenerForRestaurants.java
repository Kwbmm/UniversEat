package it.polito.mad.groupFive.restaurantcode.listeners;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.*;
import com.google.android.gms.location.LocationListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.polito.mad.groupFive.restaurantcode.SearchRestaurants.SearchRestaurants;

public class LocationListenerForRestaurants implements LocationListener {

    private GoogleApiClient gac;
    private SearchRestaurants.RestaurantAdapter ra;
    private Context context;

    public LocationListenerForRestaurants(GoogleApiClient gac, SearchRestaurants.RestaurantAdapter ra, Context context) {
        this.gac = gac;
        this.ra = ra;
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location) {
        final String METHOD_NAME = this.getClass().getName()+" - onLocationChanged";
        LocationServices.FusedLocationApi.removeLocationUpdates(this.gac,this);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRoot = db.getReference("restaurant");
        Query restaurantQuery = dbRoot.limitToFirst(10); //Get the first 10 restaurants
        restaurantQuery.addListenerForSingleValueEvent(new GetRestaurantListener(this.ra,location,context));
    }
}
