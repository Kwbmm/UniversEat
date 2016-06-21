package it.polito.mad.groupFive.restaurantcode.RestaurantView;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RatingBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.Review;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;


public class Create_review extends NavigationDrawer {
    Restaurant rest;
    String title;
    EditText comments;
    RatingBar rating_pricequality;
    RatingBar rating_place;
    RatingBar rating_food;
    RatingBar rating_service;
    String uid;
    String rid;
    Float ratingValue;
    Float ratingNumber;
    HashMap<Integer, Float> ratings;
    float sum;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.actionBar_makeReview);
        FrameLayout mlay = (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_create_review, mlay);
        ratings = new HashMap<>();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance();
        User user = new User(uid);
        DatabaseReference Myref = db.getReference("User");
        Myref.orderByChild("uid").equalTo(uid).addChildEventListener(new DataList(user));
        getData();

        Button create = (Button) findViewById(R.id.rev_create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Float i : ratings.values()) {
                    sum = sum + i;
                }
                Review rev = new Review(uid, rid);
                rev.setRating(sum);
                rev.setTitle(title);
                rev.setReviewText(comments.getText().toString());
                rev.setFood(rating_food.getRating());
                rev.setPlace(rating_place.getRating());
                rev.setPricequality(rating_pricequality.getRating());
                rev.setService(rating_service.getRating());
                Calendar date = Calendar.getInstance();
                rev.setDate(date.get(Calendar.DAY_OF_MONTH) + "/" + date.get(Calendar.MONTH) + "/" + date.get(Calendar.YEAR));
                //Log.v("DATE", date.get(Calendar.DAY_OF_MONTH) + "/" + date.get(Calendar.MONTH) + "/" + date.get(Calendar.YEAR));

                DatabaseReference ref = db.getReference("review");
                ref.push().setValue(rev).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference reference = db.getReference("restaurant");
                        ratingNumber++;
                        reference.child(rid).child("ratingNumber").setValue((ratingNumber.toString())).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                DatabaseReference reference = db.getReference("restaurant");
                                Float tot = ratingValue + sum;
                                reference.child(rid).child("rating").setValue(tot.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void getData() {
        rid = getIntent().getExtras().getString("rid");
        String restName = getIntent().getExtras().getString("restName");
        ratingValue = getIntent().getExtras().getFloat("rat");
        //Log.v("rating", ratingValue.toString());
        ratingNumber = getIntent().getExtras().getFloat("ratingNumber");
        comments = (EditText) findViewById(R.id.review_text);
        rating_food = (RatingBar) findViewById(R.id.rev_rate_food);
        rating_place = (RatingBar) findViewById(R.id.rev_rate_place);
        rating_pricequality = (RatingBar) findViewById(R.id.rev_rate_pqr);
        rating_service = (RatingBar) findViewById(R.id.rev_rate_service);
        rating_food.setOnRatingBarChangeListener(new RatingsListener(1));
        rating_place.setOnRatingBarChangeListener(new RatingsListener(2));
        rating_pricequality.setOnRatingBarChangeListener(new RatingsListener(3));
        rating_service.setOnRatingBarChangeListener(new RatingsListener(4));

    }

    public class RatingsListener implements RatingBar.OnRatingBarChangeListener {
        int num;

        public RatingsListener(int num) {
            this.num = num;
        }


        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            ratings.put(num, rating);
        }
    }


    public class DataList implements ChildEventListener {
        public User user;

        public DataList(User user) {
            this.user = user;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            this.user = dataSnapshot.getValue(user.getClass());
            title = user.getName() + " " + user.getSurname();
        }


        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

}
