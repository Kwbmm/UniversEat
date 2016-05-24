package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.polito.mad.groupFive.restaurantcode.RestaurantView.User_info_view;

import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;

import it.polito.mad.groupFive.restaurantcode.datastructures.RestaurantOwner;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;


/**
 * Created by Cristiano on 05/05/2016.
 */
public class Profile extends NavigationDrawer {
    
    String uid;
    User user;
    FirebaseDatabase db;
    DatabaseReference myRef;
    
    SharedPreferences sharedPreferences;
    boolean restOwner;
    FrameLayout mlay;
    TextView name;
    TextView username;
    TextView email;
    TextView header;
    ImageView image;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mlay= (FrameLayout) findViewById(R.id.frame);
        sharedPreferences=this.getSharedPreferences("RestaurantCode.Userdata",this.MODE_PRIVATE);
        if((uid=sharedPreferences.getString("uid",null))==null) finish();
        restOwner=sharedPreferences.getBoolean("owner",Boolean.FALSE);
            db = FirebaseDatabase.getInstance();
            DatabaseReference Myref = db.getReference("User");

            //Myref.child("Owner").addValueEventListener(new UserDataListener(this));
            user =new User(uid);
            Myref.orderByChild("uid").equalTo(uid).addChildEventListener(new DataList(user));





        View load= LayoutInflater.from(this).inflate(R.layout.loading_bar,null);
        mlay.addView(load);



        //generadatifittizi();


    }



    private void showProfile() {
         name = (TextView) findViewById(R.id.user_name);
       username = (TextView) findViewById(R.id.user_username);
        email = (TextView) findViewById(R.id.user_email);
        header = (TextView) findViewById(R.id.listHeader);
         image= (ImageView) findViewById(R.id.user_image);
        list= (ListView) findViewById(R.id.profileList);

    }
    public class ProfileAdapter extends BaseAdapter {
        List<String> restaurantIDs;
        Context context;
        Restaurant restaurant;
        public ProfileAdapter(Context context, List<String> restaurantIDs){
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
            String restaurantID = restaurantIDs.get(position);
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
    public class DataList implements ChildEventListener {
        public User user;

        public DataList(User user){
            this.user=user;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            this.user=dataSnapshot.getValue(user.getClass());

            if(restOwner) {

            Log.v("name",user.getName());

                try {
                    FirebaseStorage storage=FirebaseStorage.getInstance();
                    StorageReference user_img=storage.getReference("Users");
                    StorageReference img = user_img.child(user.getUid());

                    final long ONE_MEGABYTE = 1024 * 1024;
                    img.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            mlay.removeAllViews();
                            mlay.inflate(getBaseContext(), R.layout.profile, mlay);
                            showProfile();

                                name.setText(user.getName()+" "+user.getSurname());
                                username.setText(user.getUserName());
                                email.setText(user.getEmail());
                                header.setText("My Restaurant");
                            image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                            ProfileAdapter profileAdapter = new ProfileAdapter(getBaseContext(),user.getRestaurantIDs());
                            list.setAdapter(profileAdapter);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });




                    //image.setImageBitmap(user.getImageBitmap());



                } catch (NullPointerException e){
                    Log.e("immagine non caricata"," ");
                }

            }
            else {

                try {
                    FirebaseStorage storage=FirebaseStorage.getInstance();
                    StorageReference user_img=storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com");
                    StorageReference img = user_img.child("Users").child(user.getUid()+".jpg");

                    final long ONE_MEGABYTE = 1024 * 1024;
                    img.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            mlay.removeAllViews();
                            mlay.inflate(getBaseContext(), R.layout.profile, mlay);
                            showProfile();
                            name.setText(user.getName()+" "+user.getSurname());
                            username.setText(user.getUserName());
                            email.setText(user.getEmail());
                            header.setText("My Favourites");
                            image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                            ProfileAdapter profileAdapter = new ProfileAdapter(getBaseContext(),user.getRestaurantIDs());
                            list.setAdapter(profileAdapter);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                    // image.setImageBitmap(user.getImageBitmap());
                } catch (NullPointerException e){
                    Log.e("immagine non caricata"," ");
                }

            }
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
