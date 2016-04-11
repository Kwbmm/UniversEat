package it.polito.mad.groupFive.restaurantcode;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;

public class Create_menu extends NavigationDrawer implements Create_menu_frag.OnFragmentInteractionListener
         {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_create_menu, mlay);
        Create_menu_frag fragment= new Create_menu_frag();
        getSupportFragmentManager().beginTransaction().add(R.id.acm_1,fragment).commit();






    }

             @Override
             public void onFragmentInteraction(Uri uri) {

             }
         }
