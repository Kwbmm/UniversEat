package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.RestaurantView.User_info_view;
import it.polito.mad.groupFive.restaurantcode.datastructures.Customer;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.RestaurantOwner;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CustomerException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantOwnerException;

/**
 * Created by Cristiano on 05/05/2016.
 */
public class Profile extends NavigationDrawer {

    int uid;
    RestaurantOwner restaurantOwner;
    Customer customer;
    SharedPreferences sharedPreferences;
    boolean restOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = this.getSharedPreferences("RestaurantCode.Userdata", this.MODE_PRIVATE);
        if ((uid = sharedPreferences.getInt("uid", -1)) == -1) finish();
        restOwner = sharedPreferences.getBoolean("owner", Boolean.FALSE);
        if (restOwner) {
            try {
                restaurantOwner = new RestaurantOwner(getBaseContext(), uid);
            } catch (RestaurantOwnerException e) {
                e.printStackTrace();
            }
        } else {
            try {
                customer = new Customer(getBaseContext(), uid);
            } catch (CustomerException e) {
                e.printStackTrace();
            }
        }
        //generadatifittizi();
        FrameLayout mlay = (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.profile, mlay);
        showProfile();
    }

    private void generadatifittizi() {
        customer.setName("Cristiano");
        customer.setSurname("Ovio");
        customer.setUserName("creeovio");
        customer.setEmail("creeovio@gmail.com");
        ArrayList<Integer> alist = new ArrayList<>();
        alist.add(sharedPreferences.getInt("rid", -1));
        alist.add(sharedPreferences.getInt("rid", -1));
        alist.add(sharedPreferences.getInt("rid", -1));
        alist.add(sharedPreferences.getInt("rid", -1));
        alist.add(sharedPreferences.getInt("rid", -1));
        alist.add(sharedPreferences.getInt("rid", -1));
        try {
            customer.setFavourites(alist);
        } catch (CustomerException e) {
            e.printStackTrace();
        }
    }

    private void showProfile() {
        TextView name = (TextView) findViewById(R.id.user_name);
        TextView username = (TextView) findViewById(R.id.user_username);
        TextView email = (TextView) findViewById(R.id.user_email);
        ImageView image = (ImageView) findViewById(R.id.user_image);
        android.support.v7.widget.CardView cardView = (android.support.v7.widget.CardView) findViewById(R.id.profile_card2);
        ImageButton editButton = (ImageButton) findViewById(R.id.editprofile);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO EDIT PROFILE
            }
        });
        if (restOwner) {
            name.setText(restaurantOwner.getName() + " " + restaurantOwner.getSurname());
            username.setText(restaurantOwner.getUserName());
            email.setText(restaurantOwner.getEmail());
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), Restaurant_management.class);
                    startActivity(intent);
                }
            });
            try {
                image.setImageBitmap(restaurantOwner.getImageBitmap());
            } catch (NullPointerException e) {
                Log.e("immagine non caricata", " ");
            }
        } else {
            name.setText(customer.getName() + " " + customer.getSurname());
            username.setText(customer.getUserName());
            email.setText(customer.getEmail());
            cardView.setVisibility(View.GONE);
            try {
                image.setImageBitmap(customer.getImageBitmap());
            } catch (NullPointerException e) {
                Log.e("immagine non caricata", " ");
            }
        }
    }
}
