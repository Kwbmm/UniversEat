package it.polito.mad.groupFive.restaurantcode.listeners;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.*;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import it.polito.mad.groupFive.restaurantcode.SearchRestaurants.SearchRestaurants;

public class LocationListenerForRestaurants implements LocationListener {

    private GoogleApiClient gac;
    private SearchRestaurants.RestaurantAdapter ra;
    private Context context;
    private SupportPlaceAutocompleteFragment paf;

    public LocationListenerForRestaurants(GoogleApiClient gac, SearchRestaurants.RestaurantAdapter ra, Context context, SupportPlaceAutocompleteFragment paf) {
        this.gac = gac;
        this.ra = ra;
        this.context = context;
        this.paf = paf;
    }

    @Override
    public void onLocationChanged(Location location) {
        final String METHOD_NAME = this.getClass().getName()+" - onLocationChanged";
        LocationServices.FusedLocationApi.removeLocationUpdates(this.gac,this);
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            Address a = addressList.get(0);
            String address = String.format("%s, %s, %s",a.getAddressLine(0), a.getLocality(), a.getCountryName());
            this.paf.setText(address);
        } catch (IOException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRoot = db.getReference("restaurant");
        Query restaurantQuery = dbRoot.limitToLast(10); //Get the newest 10 restaurants
        restaurantQuery.addListenerForSingleValueEvent(new GetRestaurantListener(this.ra,location,context));
    }
}
