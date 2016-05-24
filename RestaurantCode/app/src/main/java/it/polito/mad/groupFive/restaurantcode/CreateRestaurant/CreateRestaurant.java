package it.polito.mad.groupFive.restaurantcode.CreateRestaurant;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
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


    @Override
    public void onChangeFrag1(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName()+" - onChangeFrag1";
        SharedPreferences sp = this.getSharedPreferences(getString(R.string.user_pref),CreateRestaurant.MODE_PRIVATE);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        this.dbRoot = db.getReference().child("restaurant");
        String rid = this.dbRoot.push().getKey();
        String uid = sp.getString("uid",null);
        this.dbRoot = this.dbRoot.child(rid);

        this.restaurant = new Restaurant();
        this.restaurant.setRid(rid);
        this.restaurant.setUid(uid);
        this.restaurant.setName(r.getName());
        this.restaurant.setDescription(r.getDescription());
        this.restaurant.setImageLocal(r.getImageLocal());

        this.restaurant.setTelephone(r.getTelephone());
        this.restaurant.setWebsite(r.getWebsite());


        CreateRestaurant_2 cr2 = new CreateRestaurant_2();
        //Pass to CreateRestaurant_2 fragment the id of the restaurant
        Bundle b = new Bundle();
        b.putString("rid",this.restaurant.getRid());
        b.putString("uid",this.restaurant.getUid());
        cr2.setArguments(b);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_CreateRestaurant,cr2)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onChangeFrag2(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName()+" - onChangeFrag2";
        this.restaurant.setAddress(r.getAddress());

        //TODO set XCoord and YCoord in the future when we will know about GMaps
        //this.restaurant.setXcoord(r.getXcoord());
        //this.restaurant.setYcoord(r.getYcoord());
        this.restaurant.setZip(r.getZip());

        this.restaurant.setState(r.getState());
        this.restaurant.setCity(r.getCity());

        CreateRestaurant_3 cr3 = new CreateRestaurant_3();
        //Pass to CreateRestaurant_3 fragment the id of the restaurant
        Bundle b = new Bundle();
        b.putString("rid",this.restaurant.getRid());
        b.putString("uid",this.restaurant.getUid());
        cr3.setArguments(b);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_CreateRestaurant,cr3)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onChangeFrag3(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName()+" - onChangeFrag3";

        try {
            this.restaurant.setTimetableLunch(r.getTimetableLunch());
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }

        CreateRestaurant_4 cr4 = new CreateRestaurant_4();
        //Pass to CreateRestaurant_4 fragment the id of the restaurant
        Bundle b = new Bundle();
        b.putString("rid",this.restaurant.getRid());
        b.putString("uid",this.restaurant.getUid());
        cr4.setArguments(b);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_CreateRestaurant,cr4)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onChangeFrag4(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName()+" - onChangeFrag4";

        try {
            this.restaurant.setTimetableDinner(r.getTimetableDinner());
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }

        CreateRestaurant_5 cr5 = new CreateRestaurant_5();
        //Pass to CreateRestaurant_5 fragment the id of the restaurant
        Bundle b = new Bundle();
        b.putString("rid",this.restaurant.getRid());
        b.putString("uid",this.restaurant.getUid());
        cr5.setArguments(b);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_CreateRestaurant,cr5)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onChangeFrag5(Restaurant r) {
        final String METHOD_NAME = this.getClass().getName() + " - onChangeFrag5";
        this.restaurant.setTickets(r.getTickets());
        this.dbRoot.setValue(this.restaurant.toMap());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        this.storageRoot = storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/restaurant/"+this.restaurant.getRid());
        try {
            InputStream is = getContentResolver().openInputStream(Uri.parse(this.restaurant.getImageLocal()));
            this.storageRoot.putStream(is).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(METHOD_NAME+" - onSuccess","Image upload successful");
                    SharedPreferences sharedPreferences=getSharedPreferences(getString(R.string.user_pref),CreateRestaurant.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("rid",restaurant.getRid());
                    editor.apply();
                    finish();
                }
            });
        } catch (FileNotFoundException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD_NAME = this.getClass().getName()+" - onCreate";
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_create_restaurant, mlay);

        //Fetch data -> edit mode
        String rid;
        if((rid=this.getIntent().getExtras().getString("rid",null))!= null){
            this.restaurant=new Restaurant();
            edit=true;
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
        }
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
