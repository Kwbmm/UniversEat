package it.polito.mad.groupFive.restaurantcode;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.UserException;

public class CreateRestaurant
        extends NavigationDrawer
        implements CreateRestaurant_1.onFragInteractionListener, CreateRestaurant_2.onFragInteractionListener  {

    private Restaurant restaurant = null;

    @Override
    public void onChangeFrag1(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName()+" - onChangeFrag";
        this.restaurant = r;

        CreateRestaurant_2 cr2 = new CreateRestaurant_2();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_CreateRestaurant,cr2)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onChangeFrag2(Restaurant r) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD_NAME = this.getClass().getName()+" - onCreate";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_restaurant);
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
        SharedPreferences sp=getSharedPreferences(getString(R.string.user_pref), CreateRestaurant.MODE_PRIVATE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                this.restaurant = new Restaurant(getApplicationContext(), ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE),sp.getInt("uid",-1));
            else //TODO Move randInt inside dataStructures classes
                this.restaurant = new Restaurant(getApplicationContext(),randInt(),sp.getInt("uid",-1));
        } catch (RestaurantException |
                UserException |
                JSONException e) {
            Log.e(METHOD_NAME,e.getMessage());
        } catch (IOException e) {
            Log.e(METHOD_NAME, e.getMessage());
        }
    }

    //TODO Move randInt inside the dataStructures classes
    public static int randInt() {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand= new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt(Integer.MAX_VALUE -1 );
    }
}
