package it.polito.mad.groupFive.restaurantcode.RestaurantView;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

public class User_info_view extends NavigationDrawer implements Restaurant_info_user.OnFragmentInteractionListener,Restaurant_info_user.restaurantData,Restaurant_menu_user.OnFragmentInteractionListener,Restaurant_menu_user.restaurantData,Review_user_view.OnFragmentInteractionListener,Review_user_view.restaurantData{
    Restaurant restaurant;
    ImageButton rest_i;
    ImageButton rest_m;
    ImageButton rest_r;
    int mid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            restaurant=new Restaurant(getBaseContext(),getIntent().getExtras().getInt("rid"));
            mid=getIntent().getExtras().getInt("mid");
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
                rest_i.setColorFilter(Color.parseColor("#ffffff"));
                rest_r.setColorFilter(Color.parseColor("#000000"));
                rest_m.setColorFilter(Color.parseColor("#000000"));
                    Restaurant_info_user rest_view = new Restaurant_info_user();
                    getSupportFragmentManager()
                            .beginTransaction().addToBackStack(null)
                            .add(R.id.uif_fragment,rest_view)
                            .commit();


            }
        });
        rest_m= (ImageButton) findViewById(R.id.menu_b);
        rest_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rest_m.setColorFilter(Color.parseColor("#ffffff"));
                rest_i.setColorFilter(Color.parseColor("#000000"));
                rest_r.setColorFilter(Color.parseColor("#000000"));
                    Restaurant_menu_user rest_view = new Restaurant_menu_user();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.uif_fragment,rest_view)
                            .commit();


            }
        });
        rest_r=(ImageButton)findViewById(R.id.review_b);
        rest_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rest_m.setColorFilter(Color.parseColor("#000000"));
                rest_i.setColorFilter(Color.parseColor("#000000"));
                rest_r.setColorFilter(Color.parseColor("#ffffff"));

                    Review_user_view review_user_view=new Review_user_view();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.uif_fragment,review_user_view)
                            .commit();

        }});

        if(mid==-1){
            rest_i.setColorFilter(Color.parseColor("#ffffff"));
            rest_r.setColorFilter(Color.parseColor("#000000"));
            rest_m.setColorFilter(Color.parseColor("#000000"));
            Restaurant_info_user rest_view = new Restaurant_info_user();
            getSupportFragmentManager()
                    .beginTransaction().addToBackStack(null)
                    .add(R.id.uif_fragment,rest_view)
                    .commit();

        }

        else{
            rest_m.setColorFilter(Color.parseColor("#ffffff"));
            rest_i.setColorFilter(Color.parseColor("#000000"));
            rest_r.setColorFilter(Color.parseColor("#000000"));

        Restaurant_menu_user menu_view = new Restaurant_menu_user();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.uif_fragment,menu_view)
                .commit();
    }}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public Restaurant getRestaurant() {
        return this.restaurant;
    }

    @Override
    public int getMid() {
        return mid;
    }
}
