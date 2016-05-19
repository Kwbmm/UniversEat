package it.polito.mad.groupFive.restaurantcode.CreateRestaurant;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

public class CreateRestaurant
        extends NavigationDrawer
        implements CreateRestaurant_1.onFragInteractionListener,
            CreateRestaurant_2.onFragInteractionListener,
            CreateRestaurant_3.onFragInteractionListener,
            CreateRestaurant_4.onFragInteractionListener,
            CreateRestaurant_5.onFragInteractionListener,
            CreateRestaurant_1.getRestaurant,
            CreateRestaurant_2.getRestaurant,
            CreateRestaurant_3.getRestaurant,
            CreateRestaurant_4.getRestaurant,
            CreateRestaurant_5.getRestaurant{

    private Restaurant restaurant = null;
    private boolean edit=false;


    @Override
    public void onChangeFrag1(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName()+" - onChangeFrag1";
        try {
            SharedPreferences sp = getSharedPreferences(getString(R.string.user_pref),CreateRestaurant.MODE_PRIVATE);
            this.restaurant = new Restaurant(getApplicationContext(),sp.getInt("rid",-1));
            SharedPreferences sharedPreferences=this.getSharedPreferences(getString(R.string.user_pref),CreateRestaurant.MODE_PRIVATE);
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putInt("rid",this.restaurant.getRid());
            editor.apply();
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }
        this.restaurant.setName(r.getName());
        this.restaurant.setDescription(r.getDescription());
        this.restaurant.setImageFromByteArray(r.getImageByteArray());

        this.restaurant.setTelephone(r.getTelephone());
        this.restaurant.setWebsite(r.getWebsite());


        CreateRestaurant_2 cr2 = new CreateRestaurant_2();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_CreateRestaurant,cr2)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onChangeFrag2(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName()+" - onChangeFrag2";
        this.restaurant.setAddress(r.getAddress());

        //TODO set XCoord and YCoord in the future when we will know about GMaps
        //this.restaurant.setXcoord(r.getXcoord());
        //this.restaurant.setYcoord(r.getYcoord());
        this.restaurant.setZIPCode(r.getZIPCode());

        this.restaurant.setState(r.getState());
        this.restaurant.setCity(r.getCity());

        CreateRestaurant_3 cr3 = new CreateRestaurant_3();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_CreateRestaurant,cr3)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onChangeFrag3(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName()+" - onChangeFrag3";

        this.restaurant.setTimetableLunch(r.getTimetableLunch());

        CreateRestaurant_4 cr4 = new CreateRestaurant_4();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_CreateRestaurant,cr4)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onChangeFrag4(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName()+" - onChangeFrag4";

        this.restaurant.setTimetableDinner(r.getTimetableDinner());

        CreateRestaurant_5 cr5 = new CreateRestaurant_5();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_CreateRestaurant,cr5)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onChangeFrag5(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName()+" - onChangeFrag5";
        this.restaurant.setTickets(r.getTickets());

        try {
            this.restaurant.saveData();
            finish();
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD_NAME = this.getClass().getName()+" - onCreate";
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_create_restaurant, mlay);

        //Fetch data -> edit mode
        int rid;
        if((rid=this.getIntent().getExtras().getInt("rid",-1))!=-1){
            try {
                this.restaurant=new Restaurant(getBaseContext(),rid);
                edit=true;
            } catch (RestaurantException e) {
                e.printStackTrace();
            }

        }
        if(findViewById(R.id.fragment_CreateRestaurant) != null){
            //For more info see: http://developer.android.com/training/basics/fragments/fragment-ui.html
            if(savedInstanceState != null)
                return;
            CreateRestaurant_1 cr1 = new CreateRestaurant_1();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_CreateRestaurant,cr1)
                    .commit();
        }
    }

    @Override
    public Restaurant getRest() {
        return this.restaurant;
    }

    @Override
    public Boolean editmode() {
        return this.edit;
    }
}