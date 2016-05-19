package it.polito.mad.groupFive.restaurantcode.Login;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.Random;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Customer;
import it.polito.mad.groupFive.restaurantcode.datastructures.RestaurantOwner;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CustomerException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantOwnerException;

/**
 * Created by Giovanni on 22/04/16.
 */
public class CreateLogin extends NavigationDrawer implements Createlog_frag.OnFragmentInteractionListener, Createlog_frag1.OnFragmentInteractionListener{
    private int uid;
    private boolean owner;
    private RestaurantOwner user_r=null;
    private Customer user=null;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD_NAME =this.getClass().getName()+" - OnCreate";
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_createlogin, mlay);
        Createlog_frag fragment= new Createlog_frag();
        getSupportFragmentManager().beginTransaction().add(R.id.ac_login,fragment).commit();
    }

    public void onChangeFrag0(boolean o){
        final String METHOD_NAME = this.getClass().getName() + " - onChangeFrag0";
        SharedPreferences sp=getSharedPreferences(getString(R.string.user_pref), CreateLogin.MODE_PRIVATE);
        uid=sp.getInt("uid",-1);
        this.owner=o;
        try {
            if (owner)
                user_r = new RestaurantOwner(getApplicationContext(),uid);
            else
                user = new Customer(getApplicationContext(),uid);
        } catch (RestaurantOwnerException | CustomerException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }
        bundle = new Bundle();
        bundle.putBoolean("owner", owner);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean("owner",owner);
        editor.apply();
        Createlog_frag1 frag1 = new Createlog_frag1();
        frag1.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.ac_login,frag1).addToBackStack(null).commit();
    }

    public void onChangeFrag(Customer u, RestaurantOwner u_r) {
        final String METHOD_NAME = this.getClass().getName() + " - onChangeFrag";
        if(owner){
            user_r.setName(u_r.getName());
            user_r.setSurname(u_r.getSurname());
            user_r.setEmail(u_r.getEmail());
            user_r.setUserName(u_r.getUserName());
            user_r.setImageFromBitmap(u_r.getImageBitmap());
            user_r.setPassword(u_r.getPassword());
            try {
                user_r.saveData();
            } catch (RestaurantOwnerException e) {
                Log.e(METHOD_NAME,e.getMessage());
            }
        }
        else {
            user.setName(u.getName());
            user.setSurname(u.getSurname());
            user.setEmail(u.getEmail());
            user.setUserName(u.getUserName());
            user.setImageFromBitmap(u.getImageBitmap());
            user.setPassword(u.getPassword());
            try {
                user.saveData();
            } catch (CustomerException e) {
                Log.e(METHOD_NAME,e.getMessage());
            }
        }
        finish();
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
}