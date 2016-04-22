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

/**
 * Created by Giovanni on 22/04/16.
 */
public class CreateLogin extends NavigationDrawer implements Createlog_frag.OnFragmentInteractionListener, Createlog_frag2.OnFragmentInteractionListener{
    User user=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD_NAME =this.getClass().getName()+" - OnCreate";
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_createlogin, mlay);
        Createlog_frag fragment= new Createlog_frag();
        getSupportFragmentManager().beginTransaction().add(R.id.ac_login,fragment).commit();

        SharedPreferences sp=getSharedPreferences(getString(R.string.user_pref), CreateLogin.MODE_PRIVATE);

        try {
            user = new User(getApplicationContext(),sp.getInt("rid",-1),sp.getInt("uid",-1));
        } catch (UserException e) {
            e.printStackTrace();
        }


    }
    public void onChangeFrag(User u) {
        final String METHOD_NAME = this.getClass().getName() + " - onChangeFrag";
        user.setName(u.getName());
        user.setSurname(u.getSurname());
        user.setMail(u.getMail());
        user.setNickname(u.getNickname());

        Createlog_frag2 frag2 = new Createlog_frag2();
        getSupportFragmentManager().beginTransaction().replace(R.id.ac_login,frag2).addToBackStack(null).commit();

    }
    public void onChangeFrag1(User u){
        final String METHOD_NAME = this.getClass().getName() + " - onChangeFrag1";
        user.setPassword(u.getPassword());
        user.setRestaurantowner(u.isRestaurantowner());

        // TODO: 22/04/16 salvare dati utente
        finish();
    }


    public void onFragmentInteraction(Uri uri) {

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