package it.polito.mad.groupFive.restaurantcode.RestaurantView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.RestaurantOwner;
import it.polito.mad.groupFive.restaurantcode.datastructures.Review;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantOwnerException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.ReviewException;

public class Create_review extends NavigationDrawer {
    Restaurant rest;
    EditText title;
    EditText comments;
    RatingBar rating_pricequality;
    RatingBar rating_place;
    RatingBar rating_food;
    RatingBar rating_service;
    RatingBar rating;
    float rating_fl;
    float rating_pricequality_fl=0;
    float rating_place_fl=0;
    float rating_service_fl=0;
    float rating_food_fl=0;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView mlay= (ScrollView) findViewById(R.id.sv1);
        mlay.inflate(this, R.layout.activity_create_review, mlay);
        getData();
        calculaterating();
        rating_service.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                    rating_service_fl= rating_service.getRating();
                    calculaterating();

            }
        });

        rating_place.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                rating_place_fl= rating_place.getRating();
                calculaterating();

            }
        });

        rating_food.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                rating_food_fl= rating_food.getRating();
                calculaterating();

            }
        });

        rating_pricequality.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                rating_pricequality_fl= rating_pricequality.getRating();
                calculaterating();

            }
        });




        Button create=(Button)findViewById(R.id.rev_create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().length()>0){
                    int uid=preferences.getInt("uid",-1);
                    try {
                        RestaurantOwner user=new RestaurantOwner(getBaseContext(),uid);
                        Review rev=new Review(rest,user);
                        rev.setRating(rating_fl);
                        rev.setTitle(title.getText().toString());
                        rev.setReviewText(comments.getText().toString());
                        rev.setFood(rating_food_fl);
                        rev.setPlace(rating_place_fl);
                        rev.setPricequality(rating_pricequality_fl);
                        rev.setService(rating_service_fl);
                        rest.addReview(rev);
                        rest.saveData();
                    } catch (RestaurantOwnerException e) {
                        e.printStackTrace();
                    } catch (ReviewException e) {
                        e.printStackTrace();
                    } catch (RestaurantException e) {
                        e.printStackTrace();
                    }
                    finish();

                }else{
                    Toast.makeText(getBaseContext(),getResources().getString(R.string.toastFail),Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
    private void getData(){
        int rid=getIntent().getExtras().getInt("rid");
        try {
            rest=new Restaurant(this,rid);
        } catch (RestaurantException e) {
            e.printStackTrace();
        }
        preferences=this.getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        TextView rest_name =(TextView)findViewById(R.id.rev_rest_name);
        rest_name.setText(rest.getName());
        title=(EditText)findViewById(R.id.review_title);
        comments=(EditText)findViewById(R.id.review_text);
        rating=(RatingBar)findViewById(R.id.rev_rate);
        rating_food=(RatingBar) findViewById(R.id.rev_rate_food);
        rating_place=(RatingBar) findViewById(R.id.rev_rate_place);
        rating_pricequality=(RatingBar) findViewById(R.id.rev_rate_pqr);
        rating_service=(RatingBar) findViewById(R.id.rev_rate_service);

    }

    private void calculaterating(){
        rating_fl=rating_food_fl+rating_service_fl+rating_place_fl+rating_pricequality_fl;
        rating_fl=rating_fl/4;
        rating.setRating(rating_fl);

    }
}
