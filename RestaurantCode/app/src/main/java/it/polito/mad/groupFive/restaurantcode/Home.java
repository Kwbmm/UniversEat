package it.polito.mad.groupFive.restaurantcode;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
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
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.polito.mad.groupFive.restaurantcode.RestaurantView.User_info_view;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.holders.MenuViewHolder;
import it.polito.mad.groupFive.restaurantcode.listeners.GetMenusIDFromRestaurantListener;
import it.polito.mad.groupFive.restaurantcode.listeners.GetMenusListener;
import it.polito.mad.groupFive.restaurantcode.listeners.LocationListener;

public class Home extends NavigationDrawer {
    private ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Menu> menusshared;
    private View parent;
    private RecyclerView rv;
    private ProgressBar pb;
    private FirebaseDatabase db;
    private DatabaseReference dbRoot;
    private FrameLayout mlay;

    private static final int GPS_REQUEST_CODE = 1;
    private Location lastKnown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mlay = (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_home, mlay);
        parent = mlay;

        this.db = FirebaseDatabase.getInstance();
        this.pb = (ProgressBar) findViewById(R.id.progressBar_loadingDataHome);
        this.rv = (RecyclerView) findViewById(R.id.home_recyclerViewHome);

        //Get location
        checkGPSPermission();

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

    private void checkGPSPermission(){
        final String METHOD_NAME = this.getClass().getName() + " - checkGPSPermission";
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(METHOD_NAME, "Requesting permission..");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_REQUEST_CODE);
        }
        else{
            Log.i(METHOD_NAME, "Permission granted");
            getAccurateLocation();
            getLastKnownLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        final String METHOD_NAME = this.getClass().getName()+" - onRequestPermissionResult";
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == GPS_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.v(METHOD_NAME,"GPS Permission granted");
                checkGPSPermission();
            }
            else{
                Log.v(METHOD_NAME,"GPS Permission not granted");
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
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener(lm,ma,this));
        }
    }

    private void getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        this.lastKnown = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (this.lastKnown == null || l.getAccuracy() < this.lastKnown.getAccuracy()) {
                // Found best last known location: %s", l);
                this.lastKnown = l;
            }
        }

        if(this.lastKnown != null){ //If we have a last known location, return 0
            getMenus(0);
        }
        else{ //Otherwise return -1
            getMenus(-1);
        }
    }

    private void getMenus(int locationResult) {
        final String METHOD_NAME = this.getClass().getName() + " - getMenus";
        final int LOCATION_LAST_KNOWN = 0;
        final int LOCATION_UNKNOWN_OR_NOT_GRANTED = -1;
        MenuAdapter ma;
        switch (locationResult){
            case LOCATION_UNKNOWN_OR_NOT_GRANTED: { //Fetch data from most recent to least recent, regardless of the location
                Log.w(METHOD_NAME,"Location is unknown, I'm fetching according to most recent data first.");
                this.dbRoot = this.db.getReference("menu");
                if(rv != null){
                    ma = new MenuAdapter(this.rv,this.pb,this);
                    rv.setAdapter(ma);
                    LinearLayoutManager llmVertical = new LinearLayoutManager(this);
                    llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
                    rv.setLayoutManager(llmVertical);
                    Query menuQuery = this.dbRoot.limitToFirst(30); //Get the first 10 menus
                    menuQuery.addListenerForSingleValueEvent(new GetMenusListener(ma,this));
                }
                break;
            }
            case LOCATION_LAST_KNOWN:{ //Fetch data from most recent to least recent, but put nearest menus first.
                Log.d(METHOD_NAME,"Location last known");
                this.dbRoot = this.db.getReference("restaurant");
                if (rv != null) {
                    ma = new MenuAdapter(this.rv, this.pb,this);
                    rv.setAdapter(ma);
                    LinearLayoutManager llmVertical = new LinearLayoutManager(this);
                    llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
                    rv.setLayoutManager(llmVertical);
                    Query menuQuery = this.dbRoot.limitToFirst(10); //Get the first 10 restaurants
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

    public static class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder>{
        private class DistanceMenu extends Menu{

            private float distance;
            public DistanceMenu(Menu m,float distance) throws MenuException {
                super(m.getRid(), m.getMid());
                super
                        .setBeverage(m.isBeverage())
                        .setDescription(m.getDescription())
                        .setImageLocal(m.getImageLocalPath())
                        .setName(m.getName())
                        .setServiceFee(m.isServiceFee())
                        .setPrice(m.getPrice())
                        .setType(m.getType());
                this.distance = distance;
            }

            public float getDistance(){ return this.distance; }
        }

        private SortedList<DistanceMenu> menus;
        private RecyclerView rv;
        private ProgressBar pb;
        private Context context;

        private MenuAdapter(RecyclerView rv,ProgressBar pb,Context context){
            this.rv = rv;
            this.pb = pb;
            this.context = context;
            this.menus = new SortedList<DistanceMenu>(DistanceMenu.class,
                    new SortedList.Callback<DistanceMenu>() {
                        @Override
                        public int compare(DistanceMenu o1, DistanceMenu o2) {
                            if(o1.getDistance() == Float.MIN_VALUE || o2.getDistance() == Float.MIN_VALUE){
                                //If the distance is not set, we order by insertion time in the db.
                                return o2.getMid().compareTo(o1.getMid());
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
        public void addChildWithDistance(Menu m,float distance){
            final String METHOD_NAME = this.getClass().getName()+" - addChildWithDistance";
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

        public void addChildNoDistance(Menu m){
            final String METHOD_NAME = this.getClass().getName()+" - addChildWithDistance";
            try {
                DistanceMenu wm = new DistanceMenu(m,Float.MIN_VALUE);
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
            final String METHOD_NAME = this.getClass().getName()+" - onBindViewHolder";
            final Menu menu = menus.get(position);
            holder.menu_description.setText(menu.getDescription());
            holder.menu_name.setText(menu.getName());
            holder.menu_price.setText(menu.getPrice()+"€");
            holder.menu_price.setText(menu.getPrice()+"€");
            File img = new File(menu.getImageLocalPath());
            try {
                holder.menu_image.setImageBitmap(new Picture(Uri.fromFile(img),context.getContentResolver(),300,300).getBitmap());
            } catch (IOException e) {
                Log.e(METHOD_NAME,e.getMessage());
            }
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent menu_view =new Intent(context,User_info_view.class);
                    menu_view.putExtra("mid",menu.getMid());
                    menu_view.putExtra("rid",menu.getRid());
                    context.startActivity(menu_view);
                }
            });
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