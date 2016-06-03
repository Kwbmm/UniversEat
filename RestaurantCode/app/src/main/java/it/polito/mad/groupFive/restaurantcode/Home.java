package it.polito.mad.groupFive.restaurantcode;

import android.*;
import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.groupFive.restaurantcode.RestaurantView.User_info_view;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.holders.MenuViewHolder;
import it.polito.mad.groupFive.restaurantcode.listeners.GetMenusIDFromRestaurantListener;
import it.polito.mad.groupFive.restaurantcode.listeners.LocationListener;

public class Home extends NavigationDrawer {
    private ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Menu> menusshared;
    private MenuAdapter adp;
    private Restaurant rest;
    private SharedPreferences sharedPreferences;
    private View parent;
    private String user;
    private LinearLayout dropdown;
    private boolean drop_visible;
    private RecyclerView rv;
    private ProgressBar pb;
    private FirebaseDatabase db;
    private DatabaseReference dbRoot;
    private StorageReference storageRoot;
    private FrameLayout mlay;
    private View home;
    private static final int GPS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Every activity should extend navigation drawer activity, no set content view must be called, layout must be inflated using inflate function

        super.onCreate(savedInstanceState);
        mlay = (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_home, mlay);
        parent = mlay;
        this.db = FirebaseDatabase.getInstance();

        //Get location
        getLocation();
        getMenus();

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

        dropdown = (LinearLayout) findViewById(R.id.dropdown_option);
        drop_visible = false;
        ImageButton option = (ImageButton) findViewById(R.id.opt);
        if (option != null) {
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!drop_visible) {
                        View options = LayoutInflater.from(getBaseContext()).inflate(R.layout.dropdown_options, null);
                        dropdown.addView(options);
                        drop_visible = true;

                    } else {
                        drop_visible = false;
                        dropdown.removeAllViews();
                    }
                }
            });
        }
    }

    @Override
    public void startActivity(Intent intent) {
        final String METHOD_NAME = this.getClass().getName() + " - startActivity";
        /**
         * After spending 3 hours just by trying to send extra parameters to SearchMenuResults activity
         * as explained by the android documentation with no success, I found out that the method
         * onSearchRequested is not available for AppCompat activities. So we need to override
         * startActivity to catch the intent, check if it's an ACTION_SEARCH intent and, if so, add
         * extra data.
         * For more info, see: http://stackoverflow.com/q/26991594/5261306
         */
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            CheckBox cb = (CheckBox) findViewById(R.id.checkBox_searchByRestaurant);
            if (cb != null && cb.isChecked()) {
                intent.putExtra(SearchMenuResults.RESTAURANT_SEARCH, true);
            }
        }
        super.startActivity(intent);
    }

    private void getMenus() {
        this.pb = (ProgressBar) findViewById(R.id.progressBar_loadingSearchViewData);
        this.rv = (RecyclerView) findViewById(R.id.recyclerView_DataView);
        this.dbRoot = this.db.getReference("restaurant");
        if (rv != null) {
            MenuAdapter ma = new MenuAdapter(this.rv, this.pb);
            rv.setAdapter(ma);
            LinearLayoutManager llmVertical = new LinearLayoutManager(this);
            llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llmVertical);
            Query menuQuery = this.dbRoot.limitToFirst(10); //Get the first 10 restaurants
            menuQuery.addListenerForSingleValueEvent(new GetMenusIDFromRestaurantListener(ma));
        }
    }

    public void getLocation() {
        final String METHOD_NAME = this.getClass().getName() + " - getLocation";
        //Request permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v(METHOD_NAME, "Requesting permission..");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_REQUEST_CODE);
        } else {
            Log.v(METHOD_NAME, "Permission already granted");
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener());
            Location loc = getLastKnownLocation(lm);
            if(loc != null)
                Log.d(METHOD_NAME, "Lat: " + loc.getLatitude() + "\nLong: " + loc.getLongitude());
        }
    }

    private Location getLastKnownLocation(LocationManager mLocationManager) {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        final String METHOD_NAME = this.getClass().getName()+" - onRequestPermissionResult";
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == GPS_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.v(METHOD_NAME,"GPS Permission granted");
                getLocation();
            }
            else{
                Log.v(METHOD_NAME,"GPS Permission not granted");
            }
        }
    }

    public static class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder>{
        private class DistanceMenu extends Menu{

            private float distance;
            public DistanceMenu(Menu m,float distance) throws MenuException {
                super(m.getRid(), m.getMid());
                super.setBeverage(m.isBeverage())
                        .setDescription(m.getDescription())
                        .setName(m.getName())
                        .setPrice(m.getPrice())
                        .setServiceFee(m.isServiceFee()).setType(m.getType());
                this.distance = distance;
            }

            public float getDistance(){ return this.distance; }
        }

        private SortedList<DistanceMenu> menus;
        private RecyclerView rv;
        private ProgressBar pb;

        private MenuAdapter(RecyclerView rv,ProgressBar pb){
            this.rv = rv;
            this.pb = pb;
            this.menus = new SortedList<DistanceMenu>(DistanceMenu.class,
                    new SortedList.Callback<DistanceMenu>() {
                        @Override
                        public int compare(DistanceMenu o1, DistanceMenu o2) {
                            return (o1.getDistance() - o2.getDistance() >= 0) ? -1 : 1;
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
                        public boolean areContentsTheSame(DistanceMenu oldItem, DistanceMenu newItem) {
                            return oldItem.getMid().equals(newItem.getMid());
                        }

                        @Override
                        public boolean areItemsTheSame(DistanceMenu item1, DistanceMenu item2) {
                            return item1.getMid().equals(item2.getMid());
                        }
                    });
        }

        /**
         * Add a new child to the adapter. Elements are added according to their weights: elements
         * with lower weight are displayed before those with higher weight.
         *
         * @param m Object to insert
         * @param distance Distance of the object with respect to your location
         */
        public void addChild(Menu m,float distance){
            final String METHOD_NAME = this.getClass().getName()+" - filterByTicket";
            try {
                DistanceMenu wm = new DistanceMenu(m,distance);
                this.menus.add(wm);
                if(rv.getVisibility() == View.GONE){
                    pb.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                }
            } catch (MenuException e) {
                Log.e(METHOD_NAME,e.getMessage());
            }
        }

        @Override
        public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View menu_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_view,null);
            return new MenuViewHolder(menu_view);
        }

        @Override
        public void onBindViewHolder(final MenuViewHolder holder, int position) {
            Menu menu = menus.get(position);
            holder.menu_description.setText(menu.getDescription());
            holder.menu_name.setText(menu.getName());
            holder.menu_price.setText(menu.getPrice()+"€");
        }

        @Override
        public int getItemCount() {
            return this.menus.size();
        }

        public SortedList<DistanceMenu> getMenus(){ return this.menus; }
    }

    public class onCardClick implements View.OnClickListener{
        private int position;
        private String rid;
        private String mid;

        public onCardClick(int position, String rid, String mid){
            this.position=position;
            this.rid=rid;
            this.mid=mid;
        }

        @Override
        public void onClick(View v) {
            Intent restinfo=new Intent(getBaseContext(),User_info_view.class);
            Log.v("rid",rid+"");
            restinfo.putExtra("rid",this.rid);
            restinfo.putExtra("mid",this.mid);
            startActivity(restinfo);
        }
    }
}