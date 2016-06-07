package it.polito.mad.groupFive.restaurantcode.Home;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.RestaurantView.User_info_view;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.holders.MenuViewHolder;
import it.polito.mad.groupFive.restaurantcode.holders.RestaurantViewHolder;
import it.polito.mad.groupFive.restaurantcode.listeners.GetMenusIDFromCourseListener;

public class SearchMenuResults extends NavigationDrawer {
    private SortedList<MenuAdapter.WeightedMenu> menus;
    private ArrayList<Restaurant> restaurants;
    private String[] query;
    private RecyclerView rv;
    private ProgressBar pb;
    private int lastSortMethod;
    private boolean isRestaurant;
    private FirebaseDatabase db;
    private DatabaseReference dbRoot;
    private StorageReference storageRoot;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String METHOD_NAME = this.getClass().getName()+" - onCreate";
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_search_result, mlay);
        // Get the intent, verify the action and get the query
        this.context=this;
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            this.query = intent.getStringExtra(SearchManager.QUERY).trim().toLowerCase().split(" ");
            this.db = FirebaseDatabase.getInstance();
//            this.db.setPersistenceEnabled(true);
            //Either show the menus or the restaurants, based on the value of isRestaurant
            if(this.isRestaurant){
                this.dbRoot = this.db.getReference("restaurant");
                this.restaurants = new ArrayList<>();
                showRestaurants();
            }
            else{
                this.dbRoot = this.db.getReference("course");
                showMenus();
            }
        }
    }

    private void showRestaurants(){
        final String METHOD_NAME = this.getClass().getName()+" - showRestaurants";
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        this.dbRoot = db.getReference().child("restaurant");
        Query restaurantQuery = this.dbRoot.orderByChild("tickets");
        restaurantQuery.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(METHOD_NAME,"DS count: "+dataSnapshot.getChildrenCount());
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            Log.d(METHOD_NAME,"Key: "+ds.getKey()+" Value: "+ds.getValue());
                        }
                        if(restaurants.size() == 0)
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.SearchResult_toastNoRestaurants),
                                    Toast.LENGTH_LONG)
                                    .show();
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
    }

    private void showMenus(){
        final String METHOD_NAME = this.getClass().getName()+" - showMenus";
        this.pb = (ProgressBar) findViewById(R.id.progressBar_loadingSearchViewData);
        this.rv = (RecyclerView) findViewById(R.id.recyclerView_DataView);
        if (rv != null){
            MenuAdapter ma = MenuAdapter.getInstanceSortedByWeight(this.rv,this.pb,context);
            rv.setAdapter(ma);
            LinearLayoutManager llmVertical = new LinearLayoutManager(this);
            llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llmVertical);
            Query menuQuery = this.dbRoot.orderByChild(this.query[0]).equalTo(true);
            menuQuery.addListenerForSingleValueEvent(new GetMenusIDFromCourseListener(ma, this.query,getApplicationContext()));
            lastSortMethod = 4;
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
                    AlertDialog.Builder sortCurtain = new AlertDialog.Builder(SearchMenuResults.this);
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
                    AlertDialog.Builder filterCurtain = new AlertDialog.Builder(SearchMenuResults.this);
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
                    AlertDialog.Builder sortCurtain = new AlertDialog.Builder(SearchMenuResults.this);
                    sortCurtain.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    if(menus == null){
                                        menus = ((MenuAdapter)rv.getAdapter()).getMenus();
                                    }
                                    MenuAdapter maByNameAsc = MenuAdapter.getInstanceSortedByName(true,menus,rv,context);
                                    rv.swapAdapter(maByNameAsc,false);
                                    lastSortMethod = 0;
                                    break;
                                case 1:
                                    if(menus == null){
                                        menus = ((MenuAdapter)rv.getAdapter()).getMenus();
                                    }
                                    MenuAdapter maByNameDesc = MenuAdapter.getInstanceSortedByName(false,menus,rv,context);
                                    rv.swapAdapter(maByNameDesc,false);
                                    lastSortMethod = 1;
                                    break;
                                case 2:
                                    if(menus == null){
                                        menus = ((MenuAdapter)rv.getAdapter()).getMenus();
                                    }
                                    MenuAdapter maByPriceAsc = MenuAdapter.getInstanceSortedByPrice(true,menus,rv,context);
                                    rv.swapAdapter(maByPriceAsc,false);
                                    lastSortMethod = 2;
                                    break;
                                case 3:
                                    if(menus == null){
                                        menus = ((MenuAdapter)rv.getAdapter()).getMenus();
                                    }
                                    MenuAdapter maByPriceDesc = MenuAdapter.getInstanceSortedByPrice(false,menus,rv,context);
                                    rv.swapAdapter(maByPriceDesc,false);
                                    lastSortMethod = 3;
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
                    final boolean[] selectedItems = new boolean[items.length];
                    AlertDialog.Builder filterCurtain = new AlertDialog.Builder(SearchMenuResults.this);
                    filterCurtain
                            .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    selectedItems[which] = isChecked;
                                }
                            })
                            .setPositiveButton(getResources().getString(R.string.SearchResult_filterConfirmButtonText), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(menus == null){ //Save the original set
                                        menus = ((MenuAdapter)rv.getAdapter()).getMenus();
                                    }
                                    MenuAdapter ma = MenuAdapter.getInstanceFromLastSortMethod(lastSortMethod,menus,rv,context);
                                    for (int i = 0; i < selectedItems.length; i++) {
                                        if(selectedItems[i]){
                                            ma.filter(i);
                                        }
                                    }
                                    rv.swapAdapter(ma,false);
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.SearchResult_filterCancelButtonText), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Arrays.fill(selectedItems,false);
                                }
                            })
                            .show();
                    return true;
                }
            }).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

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

    public static class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder>{
        private class WeightedMenu extends Menu{

            private int weight;
            public WeightedMenu(Menu m,int weight) throws MenuException {
                super(m.getRid(), m.getMid());
                super.setBeverage(m.isBeverage())
                        .setDescription(m.getDescription())
                        .setName(m.getName())
                        .setPrice(m.getPrice())
                        .setImageLocal(m.getImageLocalPath())
                        .setServiceFee(m.isServiceFee())
                        .setType(m.getType());
                this.weight = weight;
            }

            public int getWeight(){ return this.weight; }
        }

        private SortedList<WeightedMenu> menus;
        private RecyclerView rv;
        private ProgressBar pb;
        private Context context;

        public static MenuAdapter getInstanceSortedByWeight(RecyclerView rv,ProgressBar pb,Context context){
            return new MenuAdapter(0,false,null,rv,pb,context);
        }

        public static MenuAdapter getInstanceSortedByWeight(SortedList<WeightedMenu> menus,RecyclerView rv,Context context){
            return new MenuAdapter(4,false,menus,rv,null,context);
        }

        public static MenuAdapter getInstanceSortedByName(boolean asc,SortedList<WeightedMenu> menus,RecyclerView rv,Context context){
            return new MenuAdapter(1,asc,menus,rv,null,context);
        }

        public static MenuAdapter getInstanceSortedByPrice(boolean asc,SortedList<WeightedMenu> menus,RecyclerView rv,Context context){
            return new MenuAdapter(2,asc,menus,rv,null,context);
        }

        public static MenuAdapter getInstanceSortedByType(SortedList<WeightedMenu> menus, RecyclerView rv,Context context){
            return new MenuAdapter(3,false,menus,rv,null,context);
        }

        private MenuAdapter(int sortType, boolean asc, SortedList<WeightedMenu> menus,RecyclerView rv,ProgressBar pb,Context context){
            this.rv = rv;
            this.pb = pb;
            this.context=context;
            switch (sortType){
                case 0:{//Sort by weight
                    this.menus = new SortedList<WeightedMenu>(WeightedMenu.class,
                            new SortedList.Callback<WeightedMenu>() {
                                @Override
                                public int compare(WeightedMenu o1, WeightedMenu o2) {
                                    return o1.getWeight() <= o2.getWeight() ? -1 : 1;
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
                                public boolean areContentsTheSame(WeightedMenu oldItem, WeightedMenu newItem) {
                                    return oldItem.getMid().equals(newItem.getMid());
                                }

                                @Override
                                public boolean areItemsTheSame(WeightedMenu item1, WeightedMenu item2) {
                                    return item1.getMid().equals(item2.getMid());
                                }
                            });
                    break;
                }
                case 1:{ //Sort by name
                    if(asc){
                        this.menus = new SortedList<WeightedMenu>(WeightedMenu.class,
                                new SortedList.Callback<WeightedMenu>() {
                                    @Override
                                    public int compare(WeightedMenu o1, WeightedMenu o2) {
                                        return o1.getName().compareToIgnoreCase(o2.getName());
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
                                    public boolean areContentsTheSame(WeightedMenu oldItem, WeightedMenu newItem) {
                                        return oldItem.getMid().equals(newItem.getMid());
                                    }

                                    @Override
                                    public boolean areItemsTheSame(WeightedMenu item1, WeightedMenu item2) {
                                        return item1.getMid().equals(item2.getMid());
                                    }
                                });
                    }
                    else{
                        this.menus = new SortedList<WeightedMenu>(WeightedMenu.class,
                                new SortedList.Callback<WeightedMenu>() {
                                    @Override
                                    public int compare(WeightedMenu o1, WeightedMenu o2) {
                                        return o2.getName().compareToIgnoreCase(o1.getName());
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
                                    public boolean areContentsTheSame(WeightedMenu oldItem, WeightedMenu newItem) {
                                        return oldItem.getMid().equals(newItem.getMid());
                                    }

                                    @Override
                                    public boolean areItemsTheSame(WeightedMenu item1, WeightedMenu item2) {
                                        return item1.getMid().equals(item2.getMid());
                                    }
                                });
                    }
                    this.menus.beginBatchedUpdates();
                    for (int i = 0; i < menus.size(); i++) {
                        this.menus.add(menus.get(i));
                    }
                    this.menus.endBatchedUpdates();
                    break;
                }
                case 2:{ //Sort by price
                    if(asc){
                        this.menus = new SortedList<WeightedMenu>(WeightedMenu.class,
                                new SortedList.Callback<WeightedMenu>() {
                                    @Override
                                    public int compare(WeightedMenu o1, WeightedMenu o2) {
                                        return o2.getPrice() - o1.getPrice() >= 0 ? -1 : 1;
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
                                    public boolean areContentsTheSame(WeightedMenu oldItem, WeightedMenu newItem) {
                                        return oldItem.getMid().equals(newItem.getMid());
                                    }

                                    @Override
                                    public boolean areItemsTheSame(WeightedMenu item1, WeightedMenu item2) {
                                        return item1.getMid().equals(item2.getMid());
                                    }
                                });
                    }
                    else{
                        this.menus = new SortedList<WeightedMenu>(WeightedMenu.class,
                                new SortedList.Callback<WeightedMenu>() {
                                    @Override
                                    public int compare(WeightedMenu o1, WeightedMenu o2) {
                                        return o2.getPrice() - o1.getPrice() >= 0 ? 1 : -1;
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
                                    public boolean areContentsTheSame(WeightedMenu oldItem, WeightedMenu newItem) {
                                        return oldItem.getMid().equals(newItem.getMid());
                                    }

                                    @Override
                                    public boolean areItemsTheSame(WeightedMenu item1, WeightedMenu item2) {
                                        return item1.getMid().equals(item2.getMid());
                                    }
                                });
                    }
                    this.menus.beginBatchedUpdates();
                    for (int i = 0; i < menus.size(); i++) {
                        this.menus.add(menus.get(i));
                    }
                    this.menus.endBatchedUpdates();
                    break;
                }
                case 3:{ //Sort by type
                    this.menus = new SortedList<WeightedMenu>(WeightedMenu.class,
                            new SortedList.Callback<WeightedMenu>() {
                                @Override
                                public int compare(WeightedMenu o1, WeightedMenu o2) {
                                    return o2.getType() - o1.getType();
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
                                public boolean areContentsTheSame(WeightedMenu oldItem, WeightedMenu newItem) {
                                    return oldItem.getMid().equals(newItem.getMid());
                                }

                                @Override
                                public boolean areItemsTheSame(WeightedMenu item1, WeightedMenu item2) {
                                    return item1.getMid().equals(item2.getMid());
                                }
                            });
                    this.menus.beginBatchedUpdates();
                    for (int i = 0; i < menus.size(); i++) {
                        this.menus.add(menus.get(i));
                    }
                    this.menus.endBatchedUpdates();
                    break;
                }
                case 4:{ //Sort by weight again
                    this.menus = new SortedList<WeightedMenu>(WeightedMenu.class,
                            new SortedList.Callback<WeightedMenu>() {
                                @Override
                                public int compare(WeightedMenu o1, WeightedMenu o2) {
                                    return o1.getWeight() <= o2.getWeight() ? -1 : 1;
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
                                public boolean areContentsTheSame(WeightedMenu oldItem, WeightedMenu newItem) {
                                    return oldItem.getMid().equals(newItem.getMid());
                                }

                                @Override
                                public boolean areItemsTheSame(WeightedMenu item1, WeightedMenu item2) {
                                    return item1.getMid().equals(item2.getMid());
                                }
                            });
                    this.menus.beginBatchedUpdates();
                    for (int i = 0; i < menus.size(); i++) {
                        this.menus.add(menus.get(i));
                    }
                    this.menus.endBatchedUpdates();
                    break;
                }
            }
        }

        /**
         * Add a new child to the adapter. Elements are added according to their weights: elements
         * with lower weight are displayed before those with higher weight.
         *
         * @param m Object to insert
         * @param weight Weight of the object
         */
        public void addChild(Menu m,int weight){
            final String METHOD_NAME = this.getClass().getName()+" - filterByTicket";
            try {
                WeightedMenu wm = new WeightedMenu(m,weight);
                this.menus.add(wm);
                if(rv.getVisibility() == View.GONE){
                    pb.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                }
            } catch (MenuException e) {
                Log.e(METHOD_NAME,e.getMessage());
            }
        }

        //Filtering methods
        public static MenuAdapter getInstanceFromLastSortMethod(int lastSort,SortedList<WeightedMenu> menus, RecyclerView rv,Context context){
            switch (lastSort){
                case 0:
                    return MenuAdapter.getInstanceSortedByName(true,menus,rv,context);
                case 1:
                    return MenuAdapter.getInstanceSortedByName(false,menus,rv,context);
                case 2:
                    return MenuAdapter.getInstanceSortedByPrice(true,menus,rv,context);
                case 3:
                    return MenuAdapter.getInstanceSortedByPrice(false,menus,rv,context);
                case 4:
                    return MenuAdapter.getInstanceSortedByWeight(menus,rv,context);
            }
            return null;
        }

        public void filter(int choice){
            switch (choice){
                case 0: //By beverage
                    this.filterByBeverage();
                    break;
                case 1: //By ticket
                    this.filterByServiceFee();
                    break;
            }
        }

        private void filterByBeverage(){
            this.menus.beginBatchedUpdates();
            for (int i = 0; i < this.menus.size(); i++) {
                WeightedMenu wm = this.menus.get(i);
                if(!wm.isBeverage())
                    this.menus.removeItemAt(i);
            }
            this.menus.endBatchedUpdates();
        }

        private void filterByServiceFee() {
            this.menus.beginBatchedUpdates();
            for (int i = 0; i < this.menus.size(); i++) {
                WeightedMenu wm = this.menus.get(i);
                if(wm.isServiceFee())
                    this.menus.remove(wm);
            }
            this.menus.endBatchedUpdates();
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
            if(!menu.isSpicy()) holder.spicy_icon.setVisibility(View.INVISIBLE);
            if(!menu.isVegan()) holder.vegan_icon.setVisibility(View.INVISIBLE);
            if(!menu.isVegetarian()) holder.vegetarian_icon.setVisibility(View.INVISIBLE);
            if(!menu.isGlutenfree()) holder.glutenfree_icon.setVisibility(View.INVISIBLE);
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

        public SortedList<WeightedMenu> getMenus(){ return this.menus; }
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
