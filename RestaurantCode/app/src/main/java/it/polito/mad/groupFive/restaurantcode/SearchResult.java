package it.polito.mad.groupFive.restaurantcode;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polito.mad.groupFive.restaurantcode.RestaurantView.User_info_view;
import it.polito.mad.groupFive.restaurantcode.datastructures.DataManager;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.DataManagerException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.holders.MenuViewHolder;
import it.polito.mad.groupFive.restaurantcode.holders.RestaurantViewHolder;

public class SearchResult extends NavigationDrawer {
    public static final String RESTAURANT_SEARCH = "restaurant";
    private ArrayList<Menu> menus;
    private ArrayList<Restaurant> restaurants;
    private String query;
    private RecyclerView rv;
    private boolean isRestaurant;
    private DatabaseReference dbRoot;
    private StorageReference storageRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String METHOD_NAME = this.getClass().getName()+" - onCreate";
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_search_result, mlay);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            this.isRestaurant = intent.getBooleanExtra(SearchResult.RESTAURANT_SEARCH,false);
            this.query = intent.getStringExtra(SearchManager.QUERY).trim().toLowerCase();
            //Either show the menus or the restaurants, based on the value of isRestaurant
            if(this.isRestaurant){
                this.restaurants = new ArrayList<>();
                showRestaurants();
            }
            else{
                this.menus = new ArrayList<>();
//                showMenus();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search_data, menu);
        if(isRestaurant){ //Setup the buttons of the toolbar for the restaurant
            final MenuItem filterRestaurantButton = menu.findItem(R.id.filterRestaurant);
            final MenuItem sortRestaurantButton = menu.findItem(R.id.sortRestaurant);

            sortRestaurantButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    final String METHOD_NAME = this.getClass().getName()+" - onMenuItemClick";
                    String[] items = getResources().getStringArray(R.array.sortCurtainRestaurantItems);
                    AlertDialog.Builder sortCurtain = new AlertDialog.Builder(SearchResult.this);
                    sortCurtain.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    ((RestaurantAdapter)rv.getAdapter()).sortByName(true);
                                    break;
                                case 1:
                                    ((RestaurantAdapter)rv.getAdapter()).sortByName(false);
                                    break;
                                case 2:
                                    ((RestaurantAdapter)rv.getAdapter()).sortByRating(true);
                                    break;
                                case 3:
                                    ((RestaurantAdapter)rv.getAdapter()).sortByRating(false);
                                    break;
                                case 4:
                                    //TODO Uncomment when GMaps API are implemented
                                    //((RestaurantAdapter)rv.getAdapter()).sortByNearest();
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.featureUnavail),
                                            Toast.LENGTH_LONG)
                                            .show();
                                    break;
                            }
                        }
                    }).show();
                    return true;
                }
            }).setVisible(true);
            filterRestaurantButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    final String METHOD_NAME = this.getClass().getName()+" - onMenuItemClick";
                    String[] items = getResources().getStringArray(R.array.filterCurtainRestaurantItems);
                    AlertDialog.Builder filterCurtain = new AlertDialog.Builder(SearchResult.this);
                    filterCurtain.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    ((RestaurantAdapter)rv.getAdapter()).filterByTicket(true);
                                    break;
                                case 1:
                                    ((RestaurantAdapter)rv.getAdapter()).filterByTicket(false);
                                    break;
                                case 2:
                                    ((RestaurantAdapter)rv.getAdapter()).filterByBeverage(true);
                                    break;
                                case 3:
                                    ((RestaurantAdapter)rv.getAdapter()).filterByBeverage(false);
                                    break;
                                case 4:
                                    ((RestaurantAdapter)rv.getAdapter()).filterByServiceFee(true);
                                    break;
                                case 5:
                                    ((RestaurantAdapter)rv.getAdapter()).filterByServiceFee(false);
                                    break;
                                case 6:
                                    ((RestaurantAdapter)rv.getAdapter()).filterByOpenNow();
                                    break;
                            }
                        }
                    }).show();
                    return true;
                }
            }).setVisible(true);
        }
        else{ //Setup the buttons of the toolbar for the menu
            final MenuItem filterMenuButton = menu.findItem(R.id.filterMenu);
            final MenuItem sortMenuButton = menu.findItem(R.id.sortMenu);

            //Setup sortMenuButton
            sortMenuButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    final String METHOD_NAME = this.getClass().getName()+" - onMenuItemClick";
                    String[] items = getResources().getStringArray(R.array.sortCurtainMenuItems);
                    AlertDialog.Builder sortCurtain = new AlertDialog.Builder(SearchResult.this);
                    sortCurtain.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    ((MenuAdapter)rv.getAdapter()).sortByName(true);
                                    break;
                                case 1:
                                    ((MenuAdapter)rv.getAdapter()).sortByName(false);
                                    break;
                                case 2:
                                    ((MenuAdapter)rv.getAdapter()).sortByPrice(true);
                                    break;
                                case 3:
                                    ((MenuAdapter)rv.getAdapter()).sortByPrice(false);
                                    break;
                            }
                        }
                    }).show();
                    return true;
                }
            }).setVisible(true);

            filterMenuButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    final String METHOD_NAME = this.getClass().getName()+" - onMenuItemClick";
                    String[] items = getResources().getStringArray(R.array.filterCurtainMenuItems);
                    final AlertDialog.Builder filterCurtain = new AlertDialog.Builder(SearchResult.this);
                    filterCurtain.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    ((MenuAdapter)rv.getAdapter()).filterByTicket(true);
                                    break;
                                case 1:
                                    ((MenuAdapter)rv.getAdapter()).filterByTicket(false);
                                    break;
                                case 2:
                                    ((MenuAdapter)rv.getAdapter()).filterByBeverage(true);
                                    break;
                                case 3:
                                    ((MenuAdapter)rv.getAdapter()).filterByBeverage(false);
                                    break;
                                case 4:
                                    ((MenuAdapter)rv.getAdapter()).filterByServiceFee(true);
                                    break;
                                case 5:
                                    ((MenuAdapter)rv.getAdapter()).filterByServiceFee(false);
                                    break;
                            }
                        }
                    }).show();
                    return true;
                }
            }).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void showRestaurants(){
        final String METHOD_NAME = this.getClass().getName()+" - showRestaurants";
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        this.dbRoot = db.getReference().child("restaurant");
        Log.d(METHOD_NAME,"Query is: "+this.query);
        Query restaurantQuery = this.dbRoot.orderByChild("name").equalTo("zia lalla");
        restaurantQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(METHOD_NAME,"String is: "+s);
                Log.d(METHOD_NAME,"DS count: "+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(this.restaurants.size() == 0)
            Toast.makeText(getApplicationContext(),
                    getString(R.string.SearchResult_toastNoRestaurants),
                    Toast.LENGTH_LONG)
                    .show();
        else{
            this.rv = (RecyclerView) findViewById(R.id.recyclerView_DataView);
            if (rv != null){
                rv.setAdapter(new RestaurantAdapter(this.restaurants));
                LinearLayoutManager llmVertical = new LinearLayoutManager(this);
                llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
                rv.setLayoutManager(llmVertical);
            }
        }
    }
/*
    private void showMenus(){
        final String METHOD_NAME = this.getClass().getName()+" - showMenus";
        this.dbRoot = db.getReference().child("menu");
        this.dbRoot.addListenerForSingleValueEvent();
        if(dm.getMenus().size() == 0)
            Toast.makeText(getApplicationContext(),
                    getString(R.string.SearchResult_toastNoMenus),
                    Toast.LENGTH_LONG)
                    .show();
        else{
            this.menus = new ArrayList<>();
            for(Menu m : this.dm.getMenus()){
                for(String s : m.getTags()){
                    if(s.equalsIgnoreCase(this.query)){
                        this.menus.add(m);
                        break; //Go to next menu
                    }
                }
                //TODO Look also by name??
                //if(m.getName().toLowerCase().contains(this.query))
                //    this.menus.add(m);
            }
            this.rv = (RecyclerView) findViewById(R.id.recyclerView_DataView);
            if (rv != null){
                rv.setAdapter(new MenuAdapter(this.menus));
                LinearLayoutManager llmVertical = new LinearLayoutManager(this);
                llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
                rv.setLayoutManager(llmVertical);
            }
        }
    }
*/
    public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder>{
        private ArrayList<Restaurant> restaurants;
        private ArrayList<Restaurant> queryResult;

        public RestaurantAdapter(ArrayList<Restaurant> r){
            this.queryResult = r;
            this.restaurants = new ArrayList<>(this.queryResult);
        }

        public void sortByName(boolean asc){
            final String METHOD_NAME = this.getClass().getName()+" - sortByName";
            if(asc){ //A-Z sorting
                Collections.sort(this.restaurants, new Comparator<Restaurant>() {
                    @Override
                    public int compare(Restaurant lhs, Restaurant rhs) {
                        return lhs.getName().compareToIgnoreCase(rhs.getName());
                    }
                });
            }
            else{ //Z-A sorting
                Collections.sort(this.restaurants, new Comparator<Restaurant>() {
                    @Override
                    public int compare(Restaurant lhs, Restaurant rhs) {
                        return rhs.getName().compareToIgnoreCase(lhs.getName());
                    }
                });
            }
            notifyDataSetChanged();
        }

        public void sortByRating(boolean asc){
            final String METHOD_NAME = this.getClass().getName()+" - sortByRating";
            if(asc){ //Sort by less expensive to most expensive
                Collections.sort(this.restaurants, new Comparator<Restaurant>() {
                    @Override
                    public int compare(Restaurant lhs, Restaurant rhs) {
                        if(rhs.getRating() - lhs.getRating() >= 0)
                            return -1;
                        else
                            return 1;
                    }
                });
            }
            else{
                Collections.sort(this.restaurants, new Comparator<Restaurant>() {
                    @Override
                    public int compare(Restaurant lhs, Restaurant rhs) {
                        if(rhs.getRating() - lhs.getRating() >= 0)
                            return 1;
                        else
                            return -1;
                    }
                });
            }
            notifyDataSetChanged();
        }

        public void sortByNearest() {
            //TODO Implement GMaps API
        }

        public void filterByTicket(boolean ticket){
            final String METHOD_NAME = this.getClass().getName()+" - filterByTicket";
            this.restaurants = new ArrayList<>(this.queryResult);
            if(ticket){
                for(int i = 0; i < this.getItemCount(); i++){
                    boolean hasMenusWithTicket = false;
                    ArrayList<Menu> menus = this.restaurants.get(i).getMenus();
                    for(int j = 0; j < menus.size(); j++){
                        Menu m = menus.get(j);
                    }
                    if(!hasMenusWithTicket){
                        this.restaurants.remove(i);
                        i--;
                    }
                }
            }
            else {
                for (int i = 0; i < this.getItemCount(); i++) {
                    boolean hasMenusWithTicket = false;
                    ArrayList<Menu> menus = this.restaurants.get(i).getMenus();
                    for (int j = 0; j < menus.size(); j++) {
                        Menu m = menus.get(j);
                    }
                    if (hasMenusWithTicket) {
                        this.restaurants.remove(i);
                        i--;
                    }
                }
            }
            notifyDataSetChanged();
        }

        public void filterByBeverage(boolean beverageIncluded) {
            this.restaurants = new ArrayList<>(this.queryResult);
            if(beverageIncluded){
                for(int i = 0; i < this.getItemCount(); i++){
                    boolean hasMenuWithBeverageIncl = false;
                    ArrayList<Menu> menus = this.restaurants.get(i).getMenus();
                    for(int j = 0; j < menus.size(); j++){
                        Menu m = menus.get(j);
                        if(m.isBeverage()){
                            hasMenuWithBeverageIncl = true;
                            break; //Go to next restaurant
                        }
                        else hasMenuWithBeverageIncl = false;
                    }
                    if(!hasMenuWithBeverageIncl){
                        this.restaurants.remove(i);
                        i--;
                    }
                }
            }
            else {
                for (int i = 0; i < this.getItemCount(); i++) {
                    boolean hasMenuWithBeverageIncl = false;
                    ArrayList<Menu> menus = this.restaurants.get(i).getMenus();
                    for (int j = 0; j < menus.size(); j++) {
                        Menu m = menus.get(j);
                        if (!m.isBeverage()) {
                            hasMenuWithBeverageIncl = false;
                            break; //Go to next restaurant
                        } else hasMenuWithBeverageIncl = true;
                    }
                    if (hasMenuWithBeverageIncl) {
                        this.restaurants.remove(i);
                        i--;
                    }
                }
            }
            notifyDataSetChanged();
        }

        public void filterByServiceFee(boolean fee) {
            this.restaurants = new ArrayList<>(this.queryResult);
            if(fee){
                for(int i = 0; i < this.getItemCount(); i++){
                    boolean hasMenuWithFee = false;
                    ArrayList<Menu> menus = this.restaurants.get(i).getMenus();
                    for(int j = 0; j < menus.size(); j++){
                        Menu m = menus.get(j);
                        if(m.isServiceFee()){
                            hasMenuWithFee = true;
                            break; //Go to next restaurant
                        }
                        else hasMenuWithFee = false;
                    }
                    if(!hasMenuWithFee){
                        this.restaurants.remove(i);
                        i--;
                    }
                }
            }
            else {
                for (int i = 0; i < this.getItemCount(); i++) {
                    boolean hasMenuWithFee = false;
                    ArrayList<Menu> menus = this.restaurants.get(i).getMenus();
                    for (int j = 0; j < menus.size(); j++) {
                        Menu m = menus.get(j);
                        if (!m.isServiceFee()) {
                            hasMenuWithFee = false;
                            break; //Go to next restaurant
                        } else hasMenuWithFee = true;
                    }
                    if (hasMenuWithFee) {
                        this.restaurants.remove(i);
                        i--;
                    }
                }
            }
            notifyDataSetChanged();
        }

        public void filterByOpenNow(){
            this.restaurants = new ArrayList<>(this.queryResult);
            for(int i = 0; i < this.getItemCount(); i++){
                Restaurant r = this.restaurants.get(i);
                if(!r.isOpen()){
                    this.restaurants.remove(i);
                    i--;
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View restaurant_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_view,null);
            return new RestaurantViewHolder(restaurant_view);
        }

        @Override
        public void onBindViewHolder(RestaurantViewHolder holder, int position) {
            final String METHOD_NAME = this.getClass().getName()+" - onBindViewHolder";
            Restaurant restaurant = restaurants.get(position);
            holder.restaurant_name.setText(restaurant.getName());
            holder.restaurant_address.setText(restaurant.getAddress());
            holder.restaurant_rating.setRating(restaurant.getRating());
        }

        @Override
        public int getItemCount() {
            return this.restaurants.size();
        }

    }

    public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder>{
        private ArrayList<Menu> menus;
        private ArrayList<Menu> queryResult;

        public MenuAdapter(ArrayList<Menu> menus){
            this.queryResult = menus;
            this.menus = new ArrayList<>(this.queryResult);
            sortByType();
        }

        public void sortByName(boolean asc){
            final String METHOD_NAME = this.getClass().getName()+" - sortByName";
            if(asc){ //A-Z sorting
                Collections.sort(this.menus, new Comparator<Menu>() {
                    @Override
                    public int compare(Menu lhs, Menu rhs) {
                        return lhs.getName().compareToIgnoreCase(rhs.getName());
                    }
                });
            }
            else{ //Z-A sorting
                Collections.sort(this.menus, new Comparator<Menu>() {
                    @Override
                    public int compare(Menu lhs, Menu rhs) {
                        return rhs.getName().compareToIgnoreCase(lhs.getName());
                    }
                });
            }
            notifyDataSetChanged();
        }

        public void sortByPrice(boolean asc){
            final String METHOD_NAME = this.getClass().getName()+" - sortByPrice";
            if(asc){ //Sort by less expensive to most expensive
                Collections.sort(this.menus, new Comparator<Menu>() {
                    @Override
                    public int compare(Menu lhs, Menu rhs) {
                        if(rhs.getPrice() - lhs.getPrice() >= 0)
                            return -1;
                        else
                            return 1;
                    }
                });
            }
            else{
                Collections.sort(this.menus, new Comparator<Menu>() {
                    @Override
                    public int compare(Menu lhs, Menu rhs) {
                        if(rhs.getPrice() - lhs.getPrice() >= 0)
                            return 1;
                        else
                            return -1;
                    }
                });
            }
            notifyDataSetChanged();
        }

        public void sortByType(){
            Collections.sort(this.menus, new Comparator<Menu>() {
                @Override
                public int compare(Menu lhs, Menu rhs) {
                    return rhs.getType()-lhs.getType();
                }});
        }

        public void filterByTicket(boolean ticket){
            final String METHOD_NAME = this.getClass().getName()+" - filterByTicket";
            this.menus = new ArrayList<>(this.queryResult);
            if(ticket){
                for(int i = 0; i < this.getItemCount(); i++){
                    Menu m = this.menus.get(i);
                }
            }
            else{
                for(int i = 0; i < this.getItemCount(); i++){
                    Menu m = this.menus.get(i);
                }
            }
            notifyDataSetChanged();
        }

        public void filterByBeverage(boolean beverageIncluded) {
            this.menus = new ArrayList<>(this.queryResult);
            if(beverageIncluded){
                for(int i = 0; i < this.getItemCount(); i++){
                    Menu m = this.menus.get(i);
                    if(!m.isBeverage()){
                        this.menus.remove(i);
                        i--;
                    }
                }
            }
            else {
                for(int i = 0; i < this.getItemCount(); i++){
                    Menu m = this.menus.get(i);
                    if(m.isBeverage()){
                        this.menus.remove(i);
                        i--;
                    }
                }
            }
            notifyDataSetChanged();
        }

        public void filterByServiceFee(boolean fee) {
            this.menus = new ArrayList<>(this.queryResult);
            if(fee){
                for(int i = 0; i < this.getItemCount(); i++){
                    Menu m = this.menus.get(i);
                    if(!m.isServiceFee()){
                        this.menus.remove(i);
                        i--;
                    }
                }
            }
            else {
                for(int i = 0; i < this.getItemCount(); i++){
                    Menu m = this.menus.get(i);
                    if(m.isServiceFee()){
                        this.menus.remove(i);
                        i--;
                    }
                }
            }
            notifyDataSetChanged();
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
    }

    public class onCardClick implements View.OnClickListener{
        private int position;
        private int rid;
        private int mid;

        public onCardClick(int position,int rid,int mid){
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
