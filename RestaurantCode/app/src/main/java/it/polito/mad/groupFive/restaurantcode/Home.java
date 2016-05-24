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
    private int user;
    private SearchView searchView;
    private LinearLayout dropdown;
    private boolean drop_visible;



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
                        Button fakedata=(Button)findViewById(R.id.fake_data);
                        if (fakedata != null) {
                            fakedata.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String METHOD_NAME = this.getClass().getName()+" - onClick";
                                    int count=1;
                                    try {
                                        Restaurant rest = new Restaurant(v.getContext());
                                        RestaurantOwner ro= new RestaurantOwner(getBaseContext(),2);
                                        ro.setName("Owner");
                                        ro.setEmail("owner@rest.com");
                                        ro.setImageFromDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                                        ro.setUserName("1");
                                        ro.setPassword("1");
                                        ro.saveData();
                                        Order order =new Order(rest,2);
                                        order.setDate(new Date());
                                        order.setMid(14);
                                        order.setUid(22);
                                        rest.setImageFromDrawable(getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));
                                        rest.setUid(2);
                                        rest.setXcoord(0.0f);
                                        rest.setYcoord(0.0f);
                                        rest.setName("Pippo");
                                        rest.setDescription("Figo");
                                        rest.setState("Bello");
                                        rest.setRating(3.5f);
                                        rest.setCity("Politia");
                                        rest.setAddress("Via vai");
                                        rest.setTelephone("011667788");
                                        rest.getOrders().add(order);
                                        Log.v("create",String.valueOf(rest.getOrders().size()));
                                        rest.saveData();

                                        ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Menu> ms = rest.getMenus();
                                        for (int i = 0; i < 5; i++) {
                                            it.polito.mad.groupFive.restaurantcode.datastructures.Menu mn = new it.polito.mad.groupFive.restaurantcode.datastructures.Menu(rest);
                                            mn.setName("Menu " +i);
                                            mn.setDescription("Description");
                                            mn.setImageFromDrawable(getResources().getDrawable(R.mipmap.ic_pizza));
                                            mn.setPrice((float)(3*(i+1)));
                                            mn.setTicket(true);
                                            mn.setServiceFee(true);
                                            mn.setBeverage(false);
                                            mn.setType(1);
                                            rest.getMenus().add(mn);
                                            User user =new RestaurantOwner(v.getContext());
                                            Review review=new Review(rest,user);
                                            review.setRating(4.3f);
                                            review.setDate(new Date());
                                            review.setReviewText("Molto Buonissimo");
                                            review.setTitle("Il Massimo della Pizza");
                                            rest.addReview(review);
                                        }
                                        it.polito.mad.groupFive.restaurantcode.datastructures.Menu mn = new it.polito.mad.groupFive.restaurantcode.datastructures.Menu(rest);
                                        mn.setName("Orecchiette tris");
                                        mn.setDescription("orecchiette, patate, pollo");
                                        mn.setPrice(1.5f);
                                        mn.setTicket(false);
                                        mn.setType(2);
                                        mn.setBeverage(true);
                                        mn.setServiceFee(false);
                                        rest.getMenus().add(mn);
                                        rest.saveData();
                                        SharedPreferences sharedPreferences=v.getContext().getSharedPreferences(getString(R.string.user_pref),v.getContext().MODE_PRIVATE);
                                        SharedPreferences.Editor editor= sharedPreferences.edit();
                                        editor.putInt("uid",2);
                                        editor.putInt("rid",rest.getRid());
                                        editor.putBoolean("owner",true);
                                        editor.apply();
                                    } catch (RestaurantException |
                                            MenuException |
                                            OrderException |
                                            ReviewException |
                                            RestaurantOwnerException e) {
                                        Log.e(METHOD_NAME, e.getMessage());
                                    }
                                }
                            });
                        }
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
        int rid, uid;
        uid = sharedPreferences.getInt("uid", -1);
        rid = sharedPreferences.getInt("rid", -1);
        user=uid;
        Log.v("uid",uid+" ");
        try {if(uid>0){
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
            if(!menu.isGlutenFree())
                holder.glutenfree_icon.setVisibility(View.GONE);
            if(!menu.isVegetarian())
                holder.vegetarian_icon.setVisibility(View.GONE);
            if(!menu.isVegan())
                holder.vegan_icon.setVisibility(View.GONE);
            if(!menu.isSpicy())
                holder.spicy_icon.setVisibility(View.GONE);
            try {
                holder.menu_image.setImageBitmap(menu.getImageBitmap());
            } catch (NullPointerException e){
                Log.e("immagine non caricata"," ");
            }
            int rid =menus.get(position).getRid();
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