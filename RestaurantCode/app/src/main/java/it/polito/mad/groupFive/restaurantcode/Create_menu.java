package it.polito.mad.groupFive.restaurantcode;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;

public class Create_menu extends NavigationDrawer implements Create_menu_frag.OnFragmentInteractionListener,Create_menu_frag2.OnFragmentInteractionListener,Add_dish.OnFragmentInteractionListener,Create_menu_frag2.shareDish,Add_dish.new_dish
         {
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





    }
    @Override
    public void onChangeFrag(Menu menu) {
        final String METHOD_NAME = this.getClass().getName() + " - onChangeFrag";
        //Create_menu_frag2 frag2 = new Create_menu_frag2();
        //getSupportFragmentManager().beginTransaction().replace(R.id.acm_2,frag2).addToBackStack(null).commit();

    }

             @Override
             public void onFragmentInteraction(Uri uri) {

             }


             @Override
             public ArrayList<Option> getdish() {
                 return options;
             }

             @Override
             public ArrayList<Option> add_new_dish() {
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

}
