package it.polito.mad.groupFive.restaurantcode.CreateRestaurant;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

public class CreateRestaurant
        extends NavigationDrawer
        implements CreateRestaurant_1.onFragInteractionListener,
        CreateRestaurant_2.onFragInteractionListener,
        CreateRestaurant_3.onFragInteractionListener,
        CreateRestaurant_4.onFragInteractionListener,
        CreateRestaurant_5.onFragInteractionListener,
        CreateRestaurant_1.getRestaurant,
        CreateRestaurant_2.getRestaurant,
        CreateRestaurant_3.getRestaurant,
        CreateRestaurant_4.getRestaurant,
        CreateRestaurant_5.getRestaurant{

    private Restaurant restaurant = null;
    private boolean edit=false;
    private DatabaseReference dbRoot;
    private StorageReference storageRoot;
    FirebaseDatabase db;
    String rid,uid;



    @Override
    public void onChangeFrag1(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName()+" - onChangeFrag1";
        CreateRestaurant_2 cr2 = new CreateRestaurant_2();
            //Pass to CreateRestaurant_2 fragment the id of the restaurant
            Bundle b = new Bundle();
            b.putString("rid",this.restaurant.getRid());
            b.putString("uid",this.restaurant.getUid());
            cr2.setArguments(b);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_left_in,R.anim.slide_left_out,R.anim.slide_right_in,R.anim.slide_right_out)
                    .replace(R.id.fragment_CreateRestaurant,cr2)
                    .addToBackStack(null)
                    .commit();

    }

    @Override
    public void onChangeFrag2(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName()+" - onChangeFrag2";


        CreateRestaurant_3 cr3 = new CreateRestaurant_3();
        //Pass to CreateRestaurant_3 fragment the id of the restaurant
        Bundle b = new Bundle();
        b.putString("rid",this.restaurant.getRid());
        b.putString("uid",this.restaurant.getUid());
        cr3.setArguments(b);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left_in,R.anim.slide_left_out,R.anim.slide_right_in,R.anim.slide_right_out)
                .replace(R.id.fragment_CreateRestaurant,cr3)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onChangeFrag3(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName()+" - onChangeFrag3";

        this.restaurant.setTimetableLunch(r.getTimetableLunch());

        CreateRestaurant_4 cr4 = new CreateRestaurant_4();
        //Pass to CreateRestaurant_4 fragment the id of the restaurant
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left_in,R.anim.slide_left_out,R.anim.slide_right_in,R.anim.slide_right_out)
                .replace(R.id.fragment_CreateRestaurant,cr4)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onChangeFrag4(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName()+" - onChangeFrag4";


        CreateRestaurant_5 cr5 = new CreateRestaurant_5();
        //Pass to CreateRestaurant_5 fragment the id of the restaurant
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left_in,R.anim.slide_left_out,R.anim.slide_right_in,R.anim.slide_right_out)
                .replace(R.id.fragment_CreateRestaurant,cr5)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onChangeFrag5(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName() + " - onChangeFrag5";
        this.dbRoot.setValue(this.getRest().toMap());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        this.storageRoot = storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/restaurant/"+this.restaurant.getRid());
        try {
            Bitmap image = new Picture(Uri.parse(this.restaurant.getImageLocalPath()),getContentResolver(),300,300).getBitmap();
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG,20,outputStream);
            ByteArrayInputStream inputStream=new ByteArrayInputStream(outputStream.toByteArray());
            this.storageRoot.putStream(inputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Log.i(METHOD_NAME,"Image upload successful");
                    SharedPreferences sharedPreferences=getSharedPreferences(getString(R.string.user_pref),CreateRestaurant.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("rid",restaurant.getRid());
                    editor.apply();
                    finish();
                }
            });
        } catch (IOException e) {
            //Log.e(METHOD_NAME,e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD_NAME = this.getClass().getName()+" - onCreate";
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.actionBar_createRestaurant);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_create_restaurant, mlay);
        SharedPreferences sp = this.getSharedPreferences(getString(R.string.user_pref),CreateRestaurant.MODE_PRIVATE);
        this.db = FirebaseDatabase.getInstance();
        this.dbRoot = this.db.getReference().child("restaurant");
        this.rid = this.dbRoot.push().getKey();
        this.uid = sp.getString("uid",null);
        this.dbRoot = this.dbRoot.child(rid);


       if(!this.getIntent().getExtras().getString("rid",null).equals("-1")){
            rid=this.getIntent().getExtras().getString("rid",null);


               FirebaseDatabase db = FirebaseDatabase.getInstance();
               this.dbRoot = db.getReference().child("restaurant").child(rid);
               FirebaseStorage storage = FirebaseStorage.getInstance();
               this.storageRoot = storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/restaurant/");
               this.dbRoot.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       try {
                           restaurant = new Restaurant(
                                   (String)dataSnapshot.child("uid").getValue(),
                                   (String)dataSnapshot.child("rid").getValue()
                           );
                       } catch (RestaurantException e) {
                           //Log.e(METHOD_NAME,e.getMessage());
                       }
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

                       if(restaurant.getTimetableLunch()==null){
                           restaurant.setTimetableLunch(new HashMap<String, Map<String, String>>());
                       }
                       if(restaurant.getTimetableDinner()==null){
                           restaurant.setTimetableDinner(new HashMap<String, Map<String, String>>());
                       }

                       edit=true;

                       if(findViewById(R.id.fragment_CreateRestaurant) != null){
                           //For more info see: http://developer.android.com/training/basics/fragments/fragment-ui.html

                           CreateRestaurant_1 cr1 = new CreateRestaurant_1();
                           Bundle b = new Bundle();
                           b.putString("rid",rid);
                           b.putString("uid",uid);
                           cr1.setArguments(b);
                           getSupportFragmentManager()
                                   .beginTransaction()
                                   .add(R.id.fragment_CreateRestaurant,cr1)
                                   .commit();
                   }}

                   @Override
                   public void onCancelled(DatabaseError databaseError) {
                       //Log.w(METHOD_NAME+" - onCancelled",databaseError.getMessage());
                   }
               });



       }

       else{
           try {
               this.restaurant=new Restaurant(uid,rid);
               restaurant.setRating(0);
               restaurant.setRatingNumber(0);
           } catch (RestaurantException e) {
               e.printStackTrace();
           }
           if(findViewById(R.id.fragment_CreateRestaurant) != null){
            //For more info see: http://developer.android.com/training/basics/fragments/fragment-ui.html
            if(savedInstanceState != null)
                return;
            CreateRestaurant_1 cr1 = new CreateRestaurant_1();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_CreateRestaurant,cr1)
                    .commit();
        }}
    }

    @Override
    public Restaurant getRest() {
        return this.restaurant;
    }

    @Override
    public Boolean editmode() {
        return this.edit;
    }
}
