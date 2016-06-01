package it.polito.mad.groupFive.restaurantcode.RestaurantView;

import android.app.FragmentManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Course;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

public class User_info_view extends NavigationDrawer implements Restaurant_info_user.OnFragmentInteractionListener,Restaurant_info_user.restaurantData,Restaurant_menu_user.OnFragmentInteractionListener,Restaurant_menu_user.restaurantData,Review_user_view.OnFragmentInteractionListener,Review_user_view.restaurantData{
    Restaurant restaurant;
    ImageButton rest_i;
    ImageButton rest_m;
    ImageButton rest_r;
    String mid;
    String rid;
    FirebaseDatabase db;
    ArrayList<Menu> menus;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {


        try {
            rid=getIntent().getExtras().getString("rid");
            restaurant=new Restaurant(rid);
            menus=new ArrayList<>();
            db=FirebaseDatabase.getInstance();
            DatabaseReference reference=db.getReference("restaurant");
            reference.child(rid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    restaurant
                            .setName((String)dataSnapshot.child("name").getValue())
                            .setDescription((String)dataSnapshot.child("description").getValue())
                            .setAddress((String)dataSnapshot.child("address").getValue())
                            .setState((String)dataSnapshot.child("state").getValue())
                            .setCity((String)dataSnapshot.child("city").getValue())
                            .setWebsite((String)dataSnapshot.child("website").getValue())
                            .setTelephone((String)dataSnapshot.child("telephone").getValue())
                            .setZip((String)dataSnapshot.child("zip").getValue())
                            .setImageLocalPath((String)dataSnapshot.child("imageLocalPath").getValue());
                    float rating = Float.parseFloat(dataSnapshot.child("rating").getValue().toString());
                    restaurant.setRating(rating);
                    double xcoord = Double.parseDouble(dataSnapshot.child("xcoord").getValue().toString());
                    restaurant.setXCoord(xcoord);
                    double ycoord = Double.parseDouble(dataSnapshot.child("ycoord").getValue().toString());
                    restaurant.setYCoord(ycoord);
                    restaurant.setTimetableDinner((HashMap)dataSnapshot.child("timetableDinner").getValue());
                    restaurant.setTimetableLunch((HashMap)dataSnapshot.child("timetableLunch").getValue());
                    restaurant.setRatingNumber(Float.parseFloat(dataSnapshot.child("ratingNumber").getValue().toString()));
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



                    if(mid.equals("-1")){
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
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (RestaurantException e) {
            e.printStackTrace();
        }
        mid=getIntent().getExtras().getString("mid");

        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_user_info_view, mlay);
        }

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
    public String getMid() {
        return mid;
    }

    @Override
    public ArrayList<Menu> getMenus() {
        return menus;
    }
}
