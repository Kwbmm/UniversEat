package it.polito.mad.groupFive.restaurantcode.SearchRestaurants;

import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.File;
import java.io.IOException;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.holders.RestaurantViewHolder;
import it.polito.mad.groupFive.restaurantcode.listeners.GetMenusIDFromRestaurantListener;
import it.polito.mad.groupFive.restaurantcode.listeners.GetRestaurantListener;
import it.polito.mad.groupFive.restaurantcode.listeners.LocationListenerForRestaurants;

public class SearchRestaurants extends NavigationDrawer implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int LOCATION_REQUEST_CODE = 1;
    private static final long LOCATION_UPDATE_TIME_MS = 3000;
    private static final long LOCATION_UPDATE_FASTEST_TIME_MS = 5000;
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
        mlay.inflate(this, R.layout.activity_search_restaurants, mlay);

        this.gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        this.db = FirebaseDatabase.getInstance();
        this.pb = (ProgressBar) findViewById(R.id.progressBar_loadingDataRestaurant);
        this.rv = (RecyclerView) findViewById(R.id.searchRestaurants_recyclerView);

        /**
         * These lines of code are for setting up the searchViewMenu and let it know about the activity
         * used to performed searches (SearchRestaurantResults.java).
         * More info:
         *  http://developer.android.com/guide/topics/search/search-dialog.html#UsingSearchWidget
         */
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchViewRestaurant = (SearchView) findViewById(R.id.search_viewRestaurant);
        if (searchViewRestaurant != null) {
            searchViewRestaurant.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchViewRestaurant.setIconifiedByDefault(false);
            searchViewRestaurant.setQuery("Current Location",false);
//            SearchRecentSuggestions srs = new SearchRecentSuggestions(this,RestaurantSearchSuggestionProvider.AUTHORITY,RestaurantSearchSuggestionProvider.MODE);
//            srs.saveRecentQuery("Current Location",null);
        }
    }

    private void checkLocationPermissions(){
        final String METHOD_NAME = this.getClass().getName() + " - checkLocationPermissions";
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.i(METHOD_NAME,"Requesting location permissions");
            String[] permission = new String[]{ android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this,permission,LOCATION_REQUEST_CODE);
        }
        else{
            Log.i(METHOD_NAME,"Location permissions granted");
            getAccurateLocation();
            //getLastKnownLocation();
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
                getRestaurants(-1);
            }
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
            //Create a location request object first
            LocationRequest locationReq = new LocationRequest();
            locationReq.setInterval(LOCATION_UPDATE_TIME_MS);
            locationReq.setFastestInterval(LOCATION_UPDATE_FASTEST_TIME_MS);
            locationReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(this.gac,locationReq,new LocationListenerForRestaurants(this.gac,ra,this));
        }
    }

    private void getRestaurants(int locationResult){
        final String METHOD_NAME = this.getClass().getName() + " - getMenus";
        final int LOCATION_LAST_KNOWN = 0;
        final int LOCATION_UNKNOWN_OR_NOT_GRANTED = -1;
        RestaurantAdapter ra;
        switch (locationResult){
            case LOCATION_UNKNOWN_OR_NOT_GRANTED: { //Fetch data from most recent to least recent, regardless of the location
                Log.w(METHOD_NAME,"Location is unknown, I'm fetching according to most recent data first.");
                this.dbRoot = this.db.getReference("restaurant");
                if(rv != null){
                    ra = new RestaurantAdapter(this.rv,this.pb,this);
                    rv.setAdapter(ra);
                    LinearLayoutManager llmVertical = new LinearLayoutManager(this);
                    llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
                    rv.setLayoutManager(llmVertical);
                    Query restaurantQuery = this.dbRoot.limitToFirst(30); //Get the first 30 restaurants
                    restaurantQuery.addListenerForSingleValueEvent(new GetRestaurantListener(ra,this));
                }
                break;
            }
            case LOCATION_LAST_KNOWN:{ //Fetch data from most recent to least recent, but put nearest restaurants first.
                Log.d(METHOD_NAME,"Location last known");
                this.dbRoot = this.db.getReference("restaurant");
                if (rv != null) {
                    ra = new RestaurantAdapter(this.rv, this.pb,this);
                    rv.setAdapter(ra);
                    LinearLayoutManager llmVertical = new LinearLayoutManager(this);
                    llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
                    rv.setLayoutManager(llmVertical);
                    Query restaurantQuery = this.dbRoot.limitToFirst(30); //Get the first 10 restaurants
                    restaurantQuery.addListenerForSingleValueEvent(new GetRestaurantListener(ra,this.lastKnown,this));
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

    public static class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder>{
        private class DistanceRestaurant extends Restaurant {

            private float distance;
            public DistanceRestaurant(Restaurant r, float distance) throws RestaurantException {
                super(r.getUid(),r.getRid());
                super
                        .setName(r.getName())
                        .setDescription(r.getDescription())
                        .setImageLocalPath(r.getImageLocalPath())
                        .setAddress(r.getAddress())
                        .setCity(r.getCity())
                        .setRating(r.getRating())
                        .setState(r.getState())
                        .setXCoord(r.getXCoord())
                        .setYCoord(r.getYCoord())
                        .setZip(r.getZip())
                        .setTelephone(r.getTelephone())
                        .setWebsite(r.getWebsite());
                this.distance = distance;
            }

            public float getDistance(){ return this.distance; }
        }

        private SortedList<DistanceRestaurant> restaurants;
        private RecyclerView rv;
        private ProgressBar pb;
        private Context context;

        private RestaurantAdapter(RecyclerView rv,ProgressBar pb,Context context){
            this.rv = rv;
            this.pb = pb;
            this.context = context;
            this.restaurants = new SortedList<DistanceRestaurant>(DistanceRestaurant.class,
                    new SortedList.Callback<DistanceRestaurant>() {
                        @Override
                        public int compare(DistanceRestaurant o1, DistanceRestaurant o2) {
                            if(o1.getDistance() == Float.MIN_VALUE || o2.getDistance() == Float.MIN_VALUE){
                                //If the distance is not set, we order by insertion time in the db.
                                return o2.getRid().compareTo(o1.getRid());
                            }
                            return (o2.getDistance() - o1.getDistance() >= 0) ? -1 : 1;
                        }

                        @Override
                        public void onInserted(int position, int count) {
                            notifyItemRangeInserted(position,count);
                        }

                        @Override
                        public void onRemoved(int position, int count) {
                            notifyItemRangeRemoved(position,count);
                        }

                        @Override
                        public void onMoved(int fromPosition, int toPosition) {
                            notifyItemMoved(fromPosition,toPosition);
                        }

                        @Override
                        public void onChanged(int position, int count) {
                            notifyItemRangeChanged(position,count);
                        }

                        @Override
                        public boolean areContentsTheSame(DistanceRestaurant oldItem, DistanceRestaurant newItem) {
                            return oldItem.getRid().equals(newItem.getRid());
                        }

                        @Override
                        public boolean areItemsTheSame(DistanceRestaurant item1, DistanceRestaurant item2) {
                            return item1.getRid().equals(item2.getRid());
                        }
                    });
        }

        /**
         * Add a new child to the adapter. Elements are added according to their weights: elements
         * with lower weight are displayed before those with higher weight.
         *
         * @param r Object to insert
         * @param distance Distance of the object with respect to your location
         */
        public void addChildWithDistance(Restaurant r,float distance){
            final String METHOD_NAME = this.getClass().getName()+" - addChildWithDistance";
            try {
                DistanceRestaurant dr = new DistanceRestaurant(r,distance);
                this.restaurants.add(dr);
                if(rv.getVisibility() == View.GONE){
                    pb.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                }
            } catch (RestaurantException e) {
                Log.e(METHOD_NAME,e.getMessage());
            }
        }

        public void addChildNoDistance(Restaurant r){
            final String METHOD_NAME = this.getClass().getName()+" - addChildWithDistance";
            try {
                DistanceRestaurant dr = new DistanceRestaurant(r,Float.MIN_VALUE);
                this.restaurants.add(dr);
                if(rv.getVisibility() == View.GONE){
                    pb.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                }
            } catch (RestaurantException e) {
                Log.e(METHOD_NAME,e.getMessage());
            }
        }

        @Override
        public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View menu_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_view,null);
            return new RestaurantViewHolder(menu_view);
        }

        @Override
        public void onBindViewHolder(final RestaurantViewHolder holder, int position) {
            final String METHOD_NAME = this.getClass().getName()+" - onBindViewHolder";
            final Restaurant restaurant = restaurants.get(position);
            holder.restaurant_address.setText(restaurant.getAddress());
            holder.restaurant_name.setText(restaurant.getName());
            holder.restaurant_rating.setRating(restaurant.getRating());
            File img = new File(restaurant.getImageLocalPath());
            try {
                holder.restaurant_image.setImageBitmap(new Picture(Uri.fromFile(img),context.getContentResolver(),300,300).getBitmap());
            } catch (IOException e) {
                Log.e(METHOD_NAME,e.getMessage());
            }
            /*
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent menu_view =new Intent(context,User_info_view.class);
                    menu_view.putExtra("mid",resta.getRid());
                    menu_view.putExtra("rid",menu.getRid());
                    context.startActivity(menu_view);
                }
            });
            */
        }

        @Override
        public int getItemCount() {
            return this.restaurants.size();
        }

        public SortedList<DistanceRestaurant> getRestaurants(){ return this.restaurants; }
    }

}
