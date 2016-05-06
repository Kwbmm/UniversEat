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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polito.mad.groupFive.restaurantcode.RestaurantView.User_info_view;
import it.polito.mad.groupFive.restaurantcode.datastructures.DataManager;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.DataManagerException;
import it.polito.mad.groupFive.restaurantcode.holders.MenuViewHolder;
import it.polito.mad.groupFive.restaurantcode.holders.RestaurantViewHolder;

public class SearchResult extends NavigationDrawer {
    public static final String RESTAURANT_SEARCH = "restaurant";
    private DataManager dm;
    private ArrayList<Menu> menus;
    private ArrayList<Restaurant> restaurants;
    private String query;
    private RecyclerView rv;
    private boolean isRestaurant;

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
            try {
                this.dm = new DataManager(getApplicationContext());
                if(isRestaurant) //Restaurant search
                    this.showRestaurants();
                else{ //Menu search
                    this.showMenus();
                }
            } catch (DataManagerException e) {
                Log.e(METHOD_NAME,e.getMessage());
                Toast.makeText(getApplicationContext(),
                        getString(R.string.SearchResult_toastFailLoadDM),
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search_data, menu);
        if(isRestaurant){ //Setup the buttons of the toolbar for the restaurant
            final MenuItem filterRestaurantButton = menu.findItem(R.id.filterRestaurant);
            final MenuItem sortRestaurantButton = menu.findItem(R.id.sortRestaurant);

            sortRestaurantButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    final String METHOD_NAME = this.getClass().getName()+" - onMenuItemClick";
                    String[] items = getResources().getStringArray(R.array.sortCurtainItems);
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
                            }
                        }
                    }).show();
                    return true;
                }
            }).setVisible(true);
            filterRestaurantButton.setVisible(true);


        }
        else{ //Setup the buttons of the toolbar for the menu
            final MenuItem filterMenuButton = menu.findItem(R.id.filterMenu);
            final MenuItem sortMenuButton = menu.findItem(R.id.sortMenu);

            //Setup sortMenuButton
            sortMenuButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    final String METHOD_NAME = this.getClass().getName()+" - onMenuItemClick";
                    String[] items = getResources().getStringArray(R.array.sortCurtainItems);
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
                    return true;
                }
            }).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void showRestaurants(){
        final String METHOD_NAME = this.getClass().getName()+" - showRestaurants";
        if(dm.getRestaurants().size() == 0)
            Toast.makeText(getApplicationContext(),
                    getString(R.string.SearchResult_toastNoRestaurants),
                    Toast.LENGTH_LONG)
                    .show();
        else{
            this.restaurants = new ArrayList<>();
            for(Restaurant r : this.dm.getRestaurants()){
                if(r.getName().toLowerCase().contains(this.query))
                    this.restaurants.add(r);
            }
            this.rv = (RecyclerView) findViewById(R.id.recyclerView_DataView);
            if (rv != null){
                rv.setAdapter(new RestaurantAdapter(this.restaurants));
                LinearLayoutManager llmVertical = new LinearLayoutManager(this);
                llmVertical.setOrientation(LinearLayoutManager.VERTICAL);
                rv.setLayoutManager(llmVertical);
            }
        }
    }

    private void showMenus(){
        final String METHOD_NAME = this.getClass().getName()+" - showMenus";
        if(dm.getMenus().size() == 0)
            Toast.makeText(getApplicationContext(),
                    getString(R.string.SearchResult_toastNoMenus),
                    Toast.LENGTH_LONG)
                    .show();
        else{
            this.menus = new ArrayList<>();
            for(Menu m : this.dm.getMenus()){
                for(String s : m.getTags().values()){
                    if(s.equalsIgnoreCase(this.query)){
                        this.menus.add(m);
                        break; //Go to next menu
                    }
                }
                //TODO Look also by name??
                if(m.getName().toLowerCase().contains(this.query))
                    this.menus.add(m);
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

    public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder>{
        private ArrayList<Restaurant> restaurants;

        public RestaurantAdapter(ArrayList<Restaurant> r){
            this.restaurants = r;
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

        @Override
        public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View restaurant_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_view,null);
            return new RestaurantViewHolder(restaurant_view);
        }

        @Override
        public void onBindViewHolder(RestaurantViewHolder holder, int position) {
            Restaurant restaurant = restaurants.get(position);
            holder.restaurant_name.setText(restaurant.getName());
            holder.restaurant_address.setText(restaurant.getAddress());
            holder.restaurant_rating.setRating(restaurant.getRating());
            holder.restaurant_image.setImageBitmap(restaurant.getImageBitmap());
            int rid = restaurants.get(position).getRid();
            holder.card.setOnClickListener(new onCardClick(position,rid));
        }

        @Override
        public int getItemCount() {
            return this.restaurants.size();
        }
    }

    public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder>{
        private ArrayList<Menu> menus;

        public MenuAdapter(ArrayList<Menu> menus){
            this.menus=menus;
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
            //TODO remove comment here
//            holder.menu_image.setImageBitmap(menu.getImageBitmap());
            int rid = menus.get(position).getRid();
            holder.card.setOnClickListener(new onCardClick(position,rid));
        }

        @Override
        public int getItemCount() {
            return menus.size();
        }
    }

    public class onCardClick implements View.OnClickListener{
        private int position;
        private int rid;

        public onCardClick(int position,int rid){
            this.position=position;
            this.rid=rid;
        }

        @Override
        public void onClick(View v) {
            Intent restinfo=new Intent(getBaseContext(),User_info_view.class);
            Log.v("rid",rid+"");
            restinfo.putExtra("rid",this.rid);
            startActivity(restinfo);
        }
    }
}
