package it.polito.mad.groupFive.restaurantcode.SearchRestaurants;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.listeners.GetRestaurantListener;
import it.polito.mad.groupFive.restaurantcode.listeners.LocationListenerForRestaurants;

public class SearchRestaurants extends NavigationDrawer implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int LOCATION_REQUEST_CODE = 1;
    private static final long LOCATION_UPDATE_TIME_MS = 3000;
    private static final long LOCATION_UPDATE_FASTEST_TIME_MS = 5000;

    private RecyclerView rv;
    private ProgressBar pb;
    private Button btnSearch;
    private FirebaseDatabase db;
    private DatabaseReference dbRoot;
    private FrameLayout mlay;

    private GoogleApiClient gac;
    private Location lastKnown;
    private SupportPlaceAutocompleteFragment paf;

    private double[] latitude = new double[1],longitude = new double[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.actionBar_restaurantSearch);
        mlay = (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_search_restaurants, mlay);

        this.gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        this.db = FirebaseDatabase.getInstance();
        this.pb = (ProgressBar) findViewById(R.id.progressBar_loadingDataRestaurant);
        pb.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        this.rv = (RecyclerView) findViewById(R.id.searchRestaurants_recyclerView);
        this.btnSearch = (Button) findViewById(R.id.button_SearchRestaurants);
        this.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),SearchRestaurantResults.class);
                Bundle b = new Bundle();
                b.putDouble("lat",latitude[0]);
                b.putDouble("lon",longitude[0]);
                i.putExtras(b);
                startActivity(i);
            }
        });
        this.paf = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        this.paf.setHint(getResources().getString(R.string.search_helloRestaurant));
        this.paf.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                btnSearch.setEnabled(true);
                latitude[0] = place.getLatLng().latitude;
                longitude[0] = place.getLatLng().longitude;
            }

            @Override
            public void onError(Status status) {
                //Log.e("onError",status.getStatusMessage());
            }
        });
    }

    private void checkLocationPermissions(){
        final String METHOD_NAME = this.getClass().getName() + " - checkLocationPermissions";
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //Log.i(METHOD_NAME,"Requesting location permissions");
            String[] permission = new String[]{ android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this,permission,LOCATION_REQUEST_CODE);
        }
        else{
            //Log.i(METHOD_NAME,"Location permissions granted");
            //getLastKnownLocation();
            getAccurateLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        final String METHOD_NAME = this.getClass().getName()+" - onRequestPermissionResult";
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkLocationPermissions();
            }
            else{
                //Log.w(METHOD_NAME,"Permission was not granted");
                getRestaurants(-1);
            }
        }
    }

    private void getLastKnownLocation() {
        final String METHOD_NAME = this.getClass().getName() + " - getLastKnownLocation";
        this.lastKnown = LocationServices.FusedLocationApi.getLastLocation(this.gac);
        if(this.lastKnown != null){
            getRestaurants(0);
        }
    }

    private void getAccurateLocation() {
        final String METHOD_NAME = this.getClass().getName() + " - getLocation";

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Get the current location
        if(this.rv != null){
            RestaurantAdapter ra = new RestaurantAdapter(this.rv,this.pb,this);
            this.rv.setAdapter(ra);
            LinearLayoutManager llmVertical = new LinearLayoutManager(this);
            llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
            this.rv.setLayoutManager(llmVertical);
            if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                //Create a location request object first
                LocationRequest locationReq = new LocationRequest();
                locationReq.setInterval(LOCATION_UPDATE_TIME_MS);
                locationReq.setFastestInterval(LOCATION_UPDATE_FASTEST_TIME_MS);
                locationReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(this.gac,locationReq,new LocationListenerForRestaurants(this.gac,ra,this,this.paf,this.btnSearch,this.latitude,this.longitude));
            }
            else{
                getRestaurants(-1);
            }
        }
    }

    private void getRestaurants(int locationResult){
        final String METHOD_NAME = this.getClass().getName() + " - getMenus";
        final int LOCATION_LAST_KNOWN = 0;
        final int LOCATION_UNKNOWN = -1;
        RestaurantAdapter ra;
        switch (locationResult){
            case LOCATION_UNKNOWN: { //Fetch data from most recent to least recent, regardless of the location
                //Log.w(METHOD_NAME,"Location is unknown, I'm fetching according to most recent data first.");
                this.dbRoot = this.db.getReference("restaurant");
                if(rv != null){
                    ra = new RestaurantAdapter(this.rv, this.pb,this);
                    rv.setAdapter(ra);
                    LinearLayoutManager llmVertical = new LinearLayoutManager(this);
                    llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
                    this.rv.setLayoutManager(llmVertical);
                    Query restaurantQuery = this.dbRoot.limitToFirst(30); //Get the first 30 restaurants
                    restaurantQuery.addListenerForSingleValueEvent(new GetRestaurantListener(ra,this));
                }
                break;
            }
            case LOCATION_LAST_KNOWN:{ //Fetch data from most recent to least recent, but put nearest restaurants first.
                this.dbRoot = this.db.getReference("restaurant");
                if (rv != null) {
                    ra = new RestaurantAdapter(this.rv, this.pb,this);
                    rv.setAdapter(ra);
                    LinearLayoutManager llmVertical = new LinearLayoutManager(this);
                    llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
                    this.rv.setLayoutManager(llmVertical);
                    Query restaurantQuery = this.dbRoot.limitToFirst(30); //Get the first 30 restaurants
                    restaurantQuery.addListenerForSingleValueEvent(new GetRestaurantListener(ra,this.lastKnown,this));
                }
                break;
            }
            default:
                //Log.w(METHOD_NAME,"Entering 'default' case, display error message. Location code was: "+locationResult);
                this.pb.setVisibility(View.GONE);
                Toast
                        .makeText(
                                getApplicationContext(),
                                getResources().getString(R.string.toast_getMenusUnexpectedError),
                                Toast.LENGTH_LONG)
                        .show();
        }
    }

    @Override
    protected void onStart() {
        this.gac.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        this.gac.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Log.i("onConnected", "Successfully connected to google API");
        checkLocationPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Log.e("onConFail","GAPI con failed\n"+connectionResult.getErrorMessage());
    }
}
