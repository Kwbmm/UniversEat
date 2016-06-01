package it.polito.mad.groupFive.restaurantcode.RestaurantView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.Review;


public class Create_review extends NavigationDrawer {
    Restaurant rest;
    EditText title;
    EditText comments;
    RatingBar rating_pricequality;
    RatingBar rating_place;
    RatingBar rating_food;
    RatingBar rating_service;
    RatingBar ratingB;
    String uid;
    String rid;
    HashMap<Integer,Float> ratings;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_create_review, mlay);
        ratings=new HashMap<>();
        preferences=this.getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        getData();






        Button create=(Button)findViewById(R.id.rev_create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().length()>0){

                        Review rev=new Review(rid,uid);
                        rev.setRating(ratingB.getRating());
                        rev.setTitle(title.getText().toString());
                        rev.setReviewText(comments.getText().toString());
                        rev.setFood(rating_food.getRating());
                        rev.setPlace(rating_place.getRating());
                        rev.setPricequality(rating_pricequality.getRating());
                        rev.setService(rating_service.getRating());
                        rev.setDate(new Date());

                    FirebaseDatabase db=FirebaseDatabase.getInstance();
                    DatabaseReference ref=db.getReference("review");
                    ref.push().setValue(rev).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                        }
                    });


                }else{
                    Toast.makeText(getBaseContext(),getResources().getString(R.string.toastFail),Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
    private void getData(){
        rid=getIntent().getExtras().getString("rid");
        String restName=getIntent().getExtras().getString("restName");
        TextView rest_name =(TextView)findViewById(R.id.rev_rest_name);
        rest_name.setText(restName);
        title=(EditText)findViewById(R.id.review_title);
        comments=(EditText)findViewById(R.id.review_text);
        ratingB=(RatingBar)findViewById(R.id.rev_rate);
        ratingB.setIsIndicator(true);
        rating_food=(RatingBar) findViewById(R.id.rev_rate_food);
        rating_place=(RatingBar) findViewById(R.id.rev_rate_place);
        rating_pricequality=(RatingBar) findViewById(R.id.rev_rate_pqr);
        rating_service=(RatingBar) findViewById(R.id.rev_rate_service);
        rating_food.setOnRatingBarChangeListener(new RatingsListener(1));
        rating_place.setOnRatingBarChangeListener(new RatingsListener(2));
        rating_pricequality.setOnRatingBarChangeListener(new RatingsListener(3));
        rating_service.setOnRatingBarChangeListener(new RatingsListener(4));

    }

    public class RatingsListener implements RatingBar.OnRatingBarChangeListener{
        int num;
        public RatingsListener(int num){
            this.num=num;
        }


        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            ratings.put(num,rating);
            int count=0;
            float sum=0;
            for (Float i:ratings.values()){
                sum=sum+i;
                count++;

            }
            sum=sum/count;
            ratingB.setRating(sum);


        }
    }

}
