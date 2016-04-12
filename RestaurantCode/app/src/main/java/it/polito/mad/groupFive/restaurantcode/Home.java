package it.polito.mad.groupFive.restaurantcode;

import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;

public class Home extends NavigationDrawer {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Every activity should extend navigation drawer activity, no set content view must be called, layout must be inflated using inflate function

        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_home, mlay);

        int count=1;

        try {
            Restaurant rest = new User(this, 2, 2).getRestaurant();
            rest.setUid(2);
            rest.setXcoord(0.0f);
            rest.setYcoord(0.0f);
            rest.setName("Pippo");
            rest.setDescription("Figo");
            rest.setState("Bello");
            rest.setRating(3.5f);
            rest.setCity("Politia");
            rest.setAddress("Via vai");
            rest.saveData();

            rest.getData();
            ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Menu> ms = rest.getMenus();
            for (int i = 0; i < 5; i++) {

                it.polito.mad.groupFive.restaurantcode.datastructures.Menu mn = new it.polito.mad.groupFive.restaurantcode.datastructures.Menu(rest, count);
                mn.setName("Gatto");
                mn.setDescription("Gatto");
                mn.setPrice(1.5f);
                mn.setTicket(true);
                mn.setType(1);
                mn.saveData();
                ms.add(mn);
                count++;

            }
            rest.saveData();

        }catch (Exception e){}
    }}