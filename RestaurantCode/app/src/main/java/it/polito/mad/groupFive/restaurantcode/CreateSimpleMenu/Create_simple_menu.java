package it.polito.mad.groupFive.restaurantcode.CreateSimpleMenu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;

public class Create_simple_menu extends NavigationDrawer implements Create_simple_menu1.OnFragmentInteractionListener,Create_simple_menu2.OnFragmentInteractionListener,Create_simple_menu1.shareData,Create_simple_menu2.shareData,Add_simple_dish.OnFragmentInteractionListener,Add_simple_dish.shareData,Simple_menu_add_tags.OnFragmentInteractionListener,Simple_menu_add_tags.shareData {

    Restaurant rest;
    MenuData menuData;
    int mid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_create_simple_menu, mlay);
        if (getIntent().getExtras().getInt("mid",-1)!=-1){
            mid=getIntent().getExtras().getInt("mid",-1);
           fetchData();
        }else{
        getData();}
        Create_simple_menu1 csm1=new Create_simple_menu1();
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.fragment_holder,csm1).
                commit();


    }

    public void getData(){
        SharedPreferences sp=getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
    }

    public void fetchData(){

        SharedPreferences sp=getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        int rid=sp.getInt("rid",-1);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public MenuData getdata() {
        return menuData;
    }
}
