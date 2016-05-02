package it.polito.mad.groupFive.restaurantcode.CreateSimpleMenu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;

import it.polito.mad.groupFive.restaurantcode.CreateMenu.Create_menu;
import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

public class Create_simple_menu extends NavigationDrawer implements Create_simple_menu1.OnFragmentInteractionListener,Create_simple_menu2.OnFragmentInteractionListener,Create_simple_menu1.shareData,Create_simple_menu2.shareData,Add_simple_dish.OnFragmentInteractionListener,Add_simple_dish.shareData,Simple_menu_add_tags.OnFragmentInteractionListener,Simple_menu_add_tags.shareData {

    Restaurant rest;
    MenuData menuData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_create_simple_menu, mlay);
        getData();
        Create_simple_menu1 csm1=new Create_simple_menu1();
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.fragment_holder,csm1).
                commit();


    }

    public void getData(){
        SharedPreferences sp=getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        int rid=sp.getInt("rid",-1);
        try {
            rest=new Restaurant(this,rid);
            rest.getData();
            menuData=new MenuData(rest);
            Menu menu=new Menu(rest);
            menuData.setMenu(menu);
            rest.getMenus().add(menu);
        } catch (RestaurantException e) {
            e.printStackTrace();
        } catch (MenuException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public MenuData getdata() {
        return menuData;
    }
}
