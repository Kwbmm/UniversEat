package it.polito.mad.groupFive.restaurantcode;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import it.polito.mad.groupFive.restaurantcode.RestaurantView.User_info_view;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Order;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.RestaurantOwner;
import it.polito.mad.groupFive.restaurantcode.datastructures.Review;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.OrderException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantOwnerException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.ReviewException;
import it.polito.mad.groupFive.restaurantcode.holders.MenuViewHolder;

public class Home extends NavigationDrawer {
    private ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Menu> menusshared;
    private MenuAdapter adp;
    private Restaurant rest;
    private SharedPreferences sharedPreferences;
    private View parent;
    private String user;
    private SearchView searchView;
    private LinearLayout dropdown;
    private boolean drop_visible;

    private RestaurantOwner owner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Every activity should extend navigation drawer activity, no set content view must be called, layout must be inflated using inflate function

        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_home, mlay);
        parent=mlay;
        getMenus();
        adapterData();


        /**
         * These lines of code are for setting up the searchView and let it know about the activity
         * used to performed searches (SearchResult.java).
         * More info:
         *  http://developer.android.com/guide/topics/search/search-dialog.html#UsingSearchWidget
         */
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        dropdown= (LinearLayout) findViewById(R.id.dropdown_option);
        drop_visible=false;
        if(searchView != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        ImageButton option= (ImageButton)findViewById(R.id.opt);
        if(option != null)
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!drop_visible){
                        View options=LayoutInflater.from(getBaseContext()).inflate(R.layout.dropdown_options,null);
                        dropdown.addView(options);
                        drop_visible=true;

                    }else {
                        drop_visible=false;
                        dropdown.removeAllViews();
                    }
                }
            });
    }

    private void  adapterData(){
        if(menusshared != null){
            adp= new MenuAdapter(menusshared);
            RecyclerView recyclerView=(RecyclerView)findViewById(R.id.home_menu_rw);
            recyclerView.setAdapter(adp);
            LinearLayoutManager llm=new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        final String METHOD_NAME = this.getClass().getName()+" - startActivity";
        /**
         * After spending 3 hours just by trying to send extra parameters to SearchResult activity
         * as explained by the android documentation with no success, I found out that the method
         * onSearchRequested is not available for AppCompat activities. So we need to override
         * startActivity to catch the intent, check if it's an ACTION_SEARCH intent and, if so, add
         * extra data.
         * For more info, see: http://stackoverflow.com/q/26991594/5261306
         */
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            CheckBox cb = (CheckBox) findViewById(R.id.checkBox_searchByRestaurant);
            if(cb != null && cb.isChecked())
                intent.putExtra(SearchResult.RESTAURANT_SEARCH,true);
        }
        super.startActivity(intent);
    }

    private void getMenus(){
        sharedPreferences = this.getSharedPreferences(getString(R.string.user_pref), this.MODE_PRIVATE);
        String rid, uid;
        uid = sharedPreferences.getString("uid", null);
        rid = sharedPreferences.getString("rid", null);
        user=uid;
        Log.v("uid",uid+" ");
        try {if(uid!=null){
            menusshared = new Restaurant(getBaseContext(),rid).getMenus();}else menusshared=null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder>{
        private ArrayList<Menu> menus;

        public MenuAdapter(ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Menu> menus){
            this.menus=menus;
            sort();
        }

        public void sort(){
            Collections.sort(this.menus, new Comparator<Menu>() {
                @Override
                public int compare(it.polito.mad.groupFive.restaurantcode.datastructures.Menu lhs, it.polito.mad.groupFive.restaurantcode.datastructures.Menu rhs) {
                    return rhs.getType()-lhs.getType();
                }});
        }

        public void update(){
            menus=menusshared;
            sort();
        }

        @Override
        public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View menu_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_view,null);
            return new MenuViewHolder(menu_view);
        }

        @Override
        public void onBindViewHolder(final MenuViewHolder holder, int position) {
            it.polito.mad.groupFive.restaurantcode.datastructures.Menu menu = menus.get(position);
            holder.menu_description.setText(menu.getDescription());
            holder.menu_name.setText(menu.getName());
            holder.menu_price.setText(menu.getPrice()+"€");
            try {
                holder.menu_image.setImageBitmap(menu.getImageBitmap());
            } catch (NullPointerException e){
                Log.e("immagine non caricata"," ");
            }
            String rid =menus.get(position).getRid();
            holder.card.setOnClickListener(new onCardClick(position,rid,menu.getMid()));
        }

        @Override
        public int getItemCount() {
            return menus.size();
        }
    }

    public class onCardClick implements View.OnClickListener{
        private int position;
        private String rid;
        private int mid;

        public onCardClick(int position, String rid, int mid){
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