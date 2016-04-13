package it.polito.mad.groupFive.restaurantcode;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;

import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;

public class Create_menu extends NavigationDrawer implements Create_menu_frag.OnFragmentInteractionListener,Create_menu_frag2.OnFragmentInteractionListener
         {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD_NAME =this.getClass().getName()+" - OnCreate";
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_create_menu, mlay);
        Create_menu_frag fragment= new Create_menu_frag();
        getSupportFragmentManager().beginTransaction().add(R.id.acm_1,fragment).commit();






    }
    @Override
    public void onChangeFrag(Menu menu) {
        final String METHOD_NAME = this.getClass().getName() + " - onChangeFrag";
        Create_menu_frag2 frag2 = new Create_menu_frag2();
        getSupportFragmentManager().beginTransaction().replace(R.id.acm_2,frag2).addToBackStack(null).commit();

    }

             @Override
             public void onFragmentInteraction(Uri uri) {

             }

             public interface OnFragmentInteractionListener {
                 // TODO: Update argument type and name
                 void onFragmentInteraction(Uri uri);
             }

}
