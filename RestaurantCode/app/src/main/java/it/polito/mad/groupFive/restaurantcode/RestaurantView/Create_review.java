package it.polito.mad.groupFive.restaurantcode.RestaurantView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RatingBar;
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
    EditText text;
    RatingBar rating;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_create_review, mlay);
        getData();
        Button create=(Button)findViewById(R.id.rev_create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().length()>0&&
                        text.getText().length()>0&&rating.getRating()>0){
                    String uid=preferences.getString("uid",null);
                    try {
                        User user=new User(uid);
                        Review rev=new Review(rest,user);
                        rev.setRating(rating.getRating());
                        rev.setTitle(title.getText().toString());
                        rev.setReviewText(text.getText().toString());
                        rest.addReview(rev);
                        rest.saveData();
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
        String rid=getIntent().getExtras().getString("rid");
        try {
            rest=new Restaurant(this,rid);
        } catch (RestaurantException e) {
            e.printStackTrace();
        }
        preferences=this.getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        TextView rest_name =(TextView)findViewById(R.id.rev_rest_name);
        rest_name.setText(rest.getName());
        title=(EditText)findViewById(R.id.review_title);
        text=(EditText)findViewById(R.id.review_text);
        rating=(RatingBar)findViewById(R.id.rev_rate);
    }
}
