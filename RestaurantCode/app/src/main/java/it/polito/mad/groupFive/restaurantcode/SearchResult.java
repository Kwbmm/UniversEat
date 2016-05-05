package it.polito.mad.groupFive.restaurantcode;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String METHOD_NAME = this.getClass().getName()+" - onCreate";
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_search_result, mlay);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            boolean isRestaurant = intent.getBooleanExtra(SearchResult.RESTAURANT_SEARCH,false);
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
            RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerView_DataView);
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
                for(String s : m.getTags()){
                    if(s.equalsIgnoreCase(this.query)){
                        this.menus.add(m);
                        break; //Go to next menu
                    }
                }
                //TODO Look also by name??
//                if(m.getName().toLowerCase().contains(this.query))
//                    this.menus.add(m);
            }
            RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerView_DataView);
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
            sort();
        }

        public void sort(){
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
            holder.menu_image.setImageBitmap(menu.getImageBitmap());
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
