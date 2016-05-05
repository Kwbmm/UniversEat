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
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        sharedPreferences=this.getSharedPreferences("RestaurantCode.Userdata",this.MODE_PRIVATE);
        if((uid=sharedPreferences.getInt("uid",-1))==-1) finish();
        restOwner=sharedPreferences.getBoolean("owner",Boolean.FALSE);
        if(restOwner) {
            try {
                restaurantOwner = new RestaurantOwner(getBaseContext(),uid);
            } catch (RestaurantOwnerException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                customer = new Customer(getBaseContext(),uid);
            } catch (CustomerException e) {
                e.printStackTrace();
            }
        }
        //generadatifittizi();
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.profile, mlay);
        showProfile();
    }

    private void generadatifittizi(){
        customer.setName("Cristiano");
        customer.setSurname("Ovio");
        customer.setUserName("creeovio");
        customer.setEmail("creeovio@gmail.com");
        ArrayList<Integer> alist =new ArrayList<>();
        alist.add(sharedPreferences.getInt("rid",-1));
        alist.add(sharedPreferences.getInt("rid",-1));
        alist.add(sharedPreferences.getInt("rid",-1));
        alist.add(sharedPreferences.getInt("rid",-1));
        alist.add(sharedPreferences.getInt("rid",-1));
        alist.add(sharedPreferences.getInt("rid",-1));
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
        TextView header = (TextView) findViewById(R.id.listHeader);
        ImageView image = (ImageView) findViewById(R.id.user_image);
        ListView list = (ListView) findViewById(R.id.profileList);
        if(restOwner) {
            name.setText(restaurantOwner.getName()+" "+restaurantOwner.getSurname());
            username.setText(restaurantOwner.getUserName());
            email.setText(restaurantOwner.getEmail());
            header.setText("My Restaurant");
            try {
                image.setImageBitmap(restaurantOwner.getImageBitmap());
            } catch (NullPointerException e){
                Log.e("immagine non caricata"," ");
            }
            ProfileAdapter profileAdapter = new ProfileAdapter(this,restaurantOwner.getRestaurantIDs());
            list.setAdapter(profileAdapter);
        }
        else {
            name.setText(customer.getName()+" "+customer.getSurname());
            username.setText(customer.getUserName());
            email.setText(customer.getEmail());
            header.setText("My Favourites");
            try {
                image.setImageBitmap(customer.getImageBitmap());
            } catch (NullPointerException e){
                Log.e("immagine non caricata"," ");
            }
            ProfileAdapter profileAdapter = new ProfileAdapter(this,customer.getFavourites());
            list.setAdapter(profileAdapter);
        }
    }
    public class ProfileAdapter extends BaseAdapter {
        ArrayList<Integer> restaurantIDs;
        Context context;
        Restaurant restaurant;
        public ProfileAdapter(Context context, ArrayList<Integer> restaurantIDs){
            this.restaurantIDs=restaurantIDs;
            this.context=context;
        }

        @Override
        public int getCount() { return restaurantIDs.size(); }

        @Override
        public Object getItem(int position) {
            return restaurantIDs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.restaurant_view, null);
            int restaurantID = restaurantIDs.get(position);
            try {
                restaurant = new Restaurant(getBaseContext(),restaurantID);
                restaurant.getData();
            } catch (RestaurantException e) {
                e.printStackTrace();
            }
            TextView name= (TextView)convertView.findViewById(R.id.restaurant_name);
            TextView address= (TextView)convertView.findViewById(R.id.restaurant_address);
            RatingBar rbar=(RatingBar)convertView.findViewById(R.id.restaurant_rating);
            ImageView img = (ImageView) convertView.findViewById(R.id.restaurant_image);
            CardView card = (CardView) convertView.findViewById(R.id.restaurant_card);
            name.setText(restaurant.getName());
            address.setText(restaurant.getAddress());
            rbar.setRating(restaurant.getRating());
            try {
                img.setImageBitmap(restaurant.getImageBitmap());
            } catch (NullPointerException e){
                Log.e("immagine non caricata"," ");
            }
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), User_info_view.class);
                    intent.putExtra("rid",restaurant.getRid());
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }
}
