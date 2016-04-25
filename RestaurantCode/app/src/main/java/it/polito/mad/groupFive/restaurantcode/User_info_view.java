package it.polito.mad.groupFive.restaurantcode;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toolbar;

import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

public class User_info_view extends NavigationDrawer implements Restaurant_info_user.OnFragmentInteractionListener,Restaurant_info_user.restaurantData,Restaurant_menu_user.OnFragmentInteractionListener,Restaurant_menu_user.restaurantData{
    Restaurant restaurant;
    ImageButton rest_i;
    ImageButton rest_m;
    ImageButton rest_r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            restaurant=new Restaurant(getBaseContext(),getIntent().getExtras().getInt("rid"));
            restaurant.getData();
        } catch (RestaurantException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_user_info_view, mlay);
        rest_i= (ImageButton) findViewById(R.id.info_b);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rest_i.getDrawable().setTint(Color.parseColor("#ffffff"));
        }
        rest_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    rest_i.getDrawable().setTint(Color.parseColor("#ffffff"));
                    rest_r.getDrawable().setTint(Color.parseColor("#000000"));
                    rest_m.getDrawable().setTint(Color.parseColor("#000000"));
                    Restaurant_info_user rest_view = new Restaurant_info_user();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.uif_fragment,rest_view)
                            .commit();

                }
            }
        });
        rest_m= (ImageButton) findViewById(R.id.menu_b);
        rest_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    rest_m.getDrawable().setTint(Color.parseColor("#ffffff"));
                    rest_i.getDrawable().setTint(Color.parseColor("#000000"));
                    rest_r.getDrawable().setTint(Color.parseColor("#000000"));
                    Restaurant_menu_user rest_view = new Restaurant_menu_user();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.uif_fragment,rest_view)
                            .commit();
                }

            }
        });
        rest_r=(ImageButton)findViewById(R.id.review_b);
        rest_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    rest_r.getDrawable().setTint(Color.parseColor("#ffffff"));
                    rest_m.getDrawable().setTint(Color.parseColor("#000000"));
                    rest_i.getDrawable().setTint(Color.parseColor("#000000"));
                }
        }});


        Restaurant_info_user rest_view = new Restaurant_info_user();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.uif_fragment,rest_view)
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }



    @Override
    public Restaurant getRestaurant() {
        return this.restaurant;
    }
}
