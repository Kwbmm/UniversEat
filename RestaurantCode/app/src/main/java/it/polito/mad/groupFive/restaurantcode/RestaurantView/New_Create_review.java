package it.polito.mad.groupFive.restaurantcode.RestaurantView;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Review;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;

/**
 * Created by Cristiano on 11/06/2016.
 */
public class New_Create_review extends Fragment {

    String title;
    EditText comments;
    RatingBar rating_pricequality;
    RatingBar rating_place;
    RatingBar rating_food;
    RatingBar rating_service;
    TextView create;
    ProgressBar progressBar;
    String uid;
    String rid;
    Float ratingValue;
    Float ratingNumber;
    float sum;
    private ValueAnimator valueAnimator1;
    private ValueAnimator valueAnimator2;
    private RelativeLayout background;
    FirebaseDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.activity_create_review, container, false);
        setAnimators();
        rid=getArguments().getString("rid");
        ratingNumber=getArguments().getFloat("ratingNumber");
        ratingValue=getArguments().getFloat("ratingValue");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance();
        User user = new User(uid);
        DatabaseReference Myref = db.getReference("User");
        Myref.orderByChild("uid").equalTo(uid).addChildEventListener(new DataList(user));
        comments = (EditText) v.findViewById(R.id.review_text);
        rating_food = (RatingBar) v.findViewById(R.id.rev_rate_food);
        rating_place = (RatingBar) v.findViewById(R.id.rev_rate_place);
        rating_pricequality = (RatingBar) v.findViewById(R.id.rev_rate_pqr);
        rating_service = (RatingBar) v.findViewById(R.id.rev_rate_service);
        progressBar=(ProgressBar)v.findViewById(R.id.progressBar3);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        background=(RelativeLayout)v.findViewById(R.id.background);
        background.postDelayed(new Runnable() {
            @Override
            public void run() {
                valueAnimator1.start();
            }
        }, 300);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        create = (TextView) v.findViewById(R.id.rev_create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                sum=rating_food.getRating()+rating_place.getRating()+rating_service.getRating()+rating_pricequality.getRating();
                sum=sum/4;
                Review rev = new Review(uid, rid);
                rev.setRating(sum);
                rev.setTitle(title);
                rev.setReviewText(comments.getText().toString());
                rev.setFood(rating_food.getRating());
                rev.setPlace(rating_place.getRating());
                rev.setPricequality(rating_pricequality.getRating());
                rev.setService(rating_service.getRating());
                Calendar date = Calendar.getInstance();
                int day=date.get(Calendar.DAY_OF_MONTH);
                int month=date.get(Calendar.MONTH)+1;
                int year=date.get(Calendar.YEAR);
                rev.setDate(day + "/" + month + "/" + year);
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
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    return v;
    }

    private void setAnimators() {
        valueAnimator1 = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(R.color.fragmentTransparent), getResources().getColor(R.color.fragmentWhite));
        valueAnimator1.setDuration(300);
        valueAnimator1.setInterpolator(new DecelerateInterpolator(2));
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                background.setBackgroundColor((int) animation.getAnimatedValue());
            }
        });
        valueAnimator2 = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(R.color.fragmentWhite), getResources().getColor(R.color.fragmentTransparent));
        valueAnimator2.setDuration(100);
        //valueAnimator2.setInterpolator(new DecelerateInterpolator(2));
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                background.setBackgroundColor((int) animation.getAnimatedValue());
            }
        });
    }
    @Override
    public void onDestroyView(){
        valueAnimator2.start();
        super.onDestroyView();
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
