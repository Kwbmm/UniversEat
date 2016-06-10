package it.polito.mad.groupFive.restaurantcode.Home;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
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
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.listeners.GetMenusIDFromRestaurantListener;
import it.polito.mad.groupFive.restaurantcode.listeners.GetMenusListener;
import it.polito.mad.groupFive.restaurantcode.listeners.LocationListenerForMenus;

public class Home extends NavigationDrawer implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int LOCATION_REQUEST_CODE = 1;
    private static final long LOCATION_UPDATE_TIME_MS = 3000;
    private static final long LOCATION_UPDATE_FASTEST_TIME_MS = 5000;

    private static boolean isDBPersistanceEnabled = false;

    private RecyclerView rv;
    private ProgressBar pb;
    private FirebaseDatabase db;
    private DatabaseReference dbRoot;
    private FrameLayout mlay;

    private GoogleApiClient gac;
    private Location lastKnown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mlay = (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_home, mlay);
        ProgressBar progressBar =(ProgressBar)findViewById(R.id.progressBar_loadingDataHome);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        this.gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        this.db = FirebaseDatabase.getInstance();
        this.pb = (ProgressBar) findViewById(R.id.progressBar_loadingDataHome);
        this.rv = (RecyclerView) findViewById(R.id.home_recyclerViewHome);

        /**
         * These lines of code are for setting up the searchViewMenu and let it know about the activity
         * used to performed searches (SearchMenuResults.java).
         * More info:
         *  http://developer.android.com/guide/topics/search/search-dialog.html#UsingSearchWidget
         */
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchViewMenu = (SearchView) findViewById(R.id.search_viewMenu);
        if (searchViewMenu != null) {
            searchViewMenu.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchViewMenu.setIconifiedByDefault(false);
        }
    }

    private void checkLocationPermissions(){
        final String METHOD_NAME = this.getClass().getName() + " - checkLocationPermissions";
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.i(METHOD_NAME,"Requesting location permissions");
            String[] permission = new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this,permission,LOCATION_REQUEST_CODE);
        }
        else{
            Log.i(METHOD_NAME,"Location permissions granted");
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
                Log.w(METHOD_NAME,"Permission was not granted");
                getMenus(-1);
            }
        }
    }

    private void getAccurateLocation() {
        final String METHOD_NAME = this.getClass().getName() + " - getLocation";
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Get the current location
        if(this.rv != null){
            MenuAdapter ma = new MenuAdapter(this.rv,this.pb,this);
            this.rv.setAdapter(ma);
            LinearLayoutManager llmVertical = new LinearLayoutManager(this);
            llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
            this.rv.setLayoutManager(llmVertical);
            if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                //Create a location request object first
                LocationRequest locationReq = new LocationRequest();
                locationReq.setInterval(LOCATION_UPDATE_TIME_MS);
                locationReq.setFastestInterval(LOCATION_UPDATE_FASTEST_TIME_MS);
                locationReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(this.gac,locationReq,new LocationListenerForMenus(this.gac,ma,this));
            }
            else{
                getMenus(-1);
            }
        }
    }

    private void getLastKnownLocation() {
        final String METHOD_NAME = this.getClass().getName() + " - getLastKnownLocation";
        this.lastKnown = LocationServices.FusedLocationApi.getLastLocation(this.gac);
        if(this.lastKnown != null){
            getMenus(0);
        }
    }

    private void getMenus(int locationResult) {
        final String METHOD_NAME = this.getClass().getName() + " - getMenus";
        final int LOCATION_LAST_KNOWN = 0;
        final int LOCATION_UNKNOWN = -1;
        MenuAdapter ma;
        switch (locationResult){
            case LOCATION_UNKNOWN: { //Fetch data from most recent to least recent, regardless of the location
                Log.w(METHOD_NAME,"Location is unknown, I'm fetching according to most recent data first.");
                if(rv != null){
                    this.dbRoot = this.db.getReference("menu");
                    ma = new MenuAdapter(this.rv, this.pb,this);
                    rv.setAdapter(ma);
                    LinearLayoutManager llmVertical = new LinearLayoutManager(this);
                    llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
                    this.rv.setLayoutManager(llmVertical);
                    Query menuQuery = this.dbRoot.limitToLast(30); //Get the newest 10 menus
                    menuQuery.addListenerForSingleValueEvent(new GetMenusListener(ma,this));
                }
                break;
            }
            case LOCATION_LAST_KNOWN:{ //Fetch data from most recent to least recent, but put nearest menus first.
                Log.i(METHOD_NAME,"Location last known");
                if (this.rv != null) {
                    this.dbRoot = this.db.getReference("restaurant");
                    ma = new MenuAdapter(this.rv, this.pb,this);
                    rv.setAdapter(ma);
                    LinearLayoutManager llmVertical = new LinearLayoutManager(this);
                    llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
                    this.rv.setLayoutManager(llmVertical);
                    Log.i(METHOD_NAME,"Preparing query for fetching data..");
                    Query menuQuery = this.dbRoot.limitToLast(10); //Get the newest 10 restaurants
                    menuQuery.addListenerForSingleValueEvent(new GetMenusIDFromRestaurantListener(ma,this.lastKnown,this));
                }
                break;
            }
            default:
                Log.w(METHOD_NAME,"Entering 'default' case, display error message. Location code was: "+locationResult);
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
        super.onStart();
        this.gac.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.gac.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("onConnected", "Successfully connected to google API");
        checkLocationPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("onConFail","GAPI con failed\n"+connectionResult.getErrorMessage());
    }

}