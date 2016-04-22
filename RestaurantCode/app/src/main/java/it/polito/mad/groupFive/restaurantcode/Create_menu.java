package it.polito.mad.groupFive.restaurantcode;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.FrameLayout;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.UserException;

public class Create_menu extends NavigationDrawer implements Create_menu_frag.OnFragmentInteractionListener,Create_menu_frag2.OnFragmentInteractionListener,Add_dish.OnFragmentInteractionListener,Add_dish.new_dish,Create_menu_frag2.shareDish,Delete_dish.removeDish,Delete_dish.OnFragmentInteractionListener
{
    private Restaurant restaurant=null;
    private User user=null;
    private Menu menu=null;

    ArrayList<Option> options;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD_NAME =this.getClass().getName()+" - OnCreate";
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_create_menu, mlay);
        Create_menu_frag fragment= new Create_menu_frag();
        getSupportFragmentManager().beginTransaction().add(R.id.acm_1,fragment).commit();

        int dimension=3;
        options =new ArrayList<>();
        for (int i=0;i<dimension;i++){
            Option opt=new Option();
            options.add(opt);
        }


        SharedPreferences sp=getSharedPreferences(getString(R.string.user_pref), Create_menu.MODE_PRIVATE);

        try {
            user = new User(getApplicationContext(),sp.getInt("rid",-1),sp.getInt("uid",-1));
            restaurant= user.getRestaurant();
            restaurant.getData();
            this.menu=new Menu(restaurant);
        } catch (UserException e) {
            e.printStackTrace();
        } catch (RestaurantException e) {
            e.printStackTrace();
        } catch (MenuException e) {
            e.printStackTrace();
        }


    }
    @Override
    public void onChangeFrag(Menu m) {
        final String METHOD_NAME = this.getClass().getName() + " - onChangeFrag";
        this.menu.setName(m.getName());
        this.menu.setDescription(m.getDescription());
        this.menu.setType(m.getType());
        this.menu.setChoiceAmount(m.getChoiceAmount());

        //this.menu.setImage64(m.getImage64());
        Log.d(METHOD_NAME,"Name: "+menu.getName());
        Log.d(METHOD_NAME,"Description: "+menu.getDescription());



        Create_menu_frag2 frag2 = new Create_menu_frag2();
        getSupportFragmentManager().beginTransaction().replace(R.id.acm_1,frag2).addToBackStack(null).commit();

    }
    public void onChangeFrag1(Menu m){
        final String METHOD_NAME = this.getClass().getName() + " - onChangeFrag1";
        this.menu.setPrice(m.getPrice());
        Log.d(METHOD_NAME,"Price: "+ String.valueOf(menu.getPrice()));
        this.menu.setBeverage(m.isBeverage());
        this.menu.setServiceFee(m.isServiceFee());

        try {
            restaurant.getMenus().add(this.menu);
            restaurant.saveData();
            finish();
        } catch (RestaurantException e) {
            e.printStackTrace();
        }


//TODO return to menu view of the restaurant
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public ArrayList<Option> add_new_dish() {
        return options;
    }

    @Override
    public ArrayList<Option> getdish() {
        return options;
    }

    @Override
    public ArrayList<Option> remdish() {
        return options;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
    public static int randInt() {
        Random rand= new Random();

        return rand.nextInt(Integer.MAX_VALUE -1 );
    }

}
