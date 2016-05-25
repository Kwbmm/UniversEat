package it.polito.mad.groupFive.restaurantcode.CreateSimpleMenu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

public class Create_simple_menu extends NavigationDrawer implements Create_simple_menu1.OnFragmentInteractionListener,Create_simple_menu2.OnFragmentInteractionListener,Create_simple_menu1.shareData,Create_simple_menu2.shareData,Add_simple_dish.OnFragmentInteractionListener,Add_simple_dish.shareData,Simple_menu_add_tags.OnFragmentInteractionListener,Simple_menu_add_tags.shareData {

    Restaurant rest;
    MenuData menuData;
    String mid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_create_simple_menu, mlay);
        if (getIntent().getExtras().getInt("mid",-1)!=-1){
            mid=getIntent().getExtras().getString("mid",null);
           fetchData();
        }else{
        getData();}
        Create_simple_menu1 csm1=new Create_simple_menu1();
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.fragment_holder,csm1).
                commit();


    }

    public void getData(){
        SharedPreferences sp=getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        String rid=sp.getString("rid",null);
        try {
            FirebaseDatabase db=FirebaseDatabase.getInstance();
            DatabaseReference ref=db.getReference("menu");
            String mid=ref.push().getKey();
            menuData=new MenuData(rid);
            Menu menu=new Menu(rid,mid);
            menuData.setMenu(menu);
        }  catch (MenuException e) {
            e.printStackTrace();
        }

    }

    public void fetchData(){

        SharedPreferences sp=getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        String rid=sp.getString("rid",null);

            /*
            FirebaseDatabase db=FirebaseDatabase.getInstance();
            DatabaseReference ref=db.getReference("restaurant");
            rest=new Restaurant(rid);
            ref.orderByKey().equalTo(rid).addChildEventListener(new fetchRest(rest));
            */

            menuData=new MenuData(rid);
            FirebaseDatabase db=FirebaseDatabase.getInstance();
            DatabaseReference ref=db.getReference("menu");
        Menu menu= null;
        try {
            menu = new Menu(mid);
        } catch (MenuException e) {
            e.printStackTrace();
        }
        ref.orderByKey().equalTo(mid).addChildEventListener(new menuFetch(menu));



    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public MenuData getdata() {
        return menuData;
    }

    public class fetchRest implements ChildEventListener{

        Restaurant restaurant;

        public fetchRest(Restaurant restaurant){
            this.restaurant=restaurant;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
            float rating = ((Long)dataSnapshot.child("rating").getValue()).floatValue();
            restaurant.setRating(rating);
            double xcoord = ((Long)dataSnapshot.child("xcoord").getValue()).doubleValue();
            restaurant.setXCoord(xcoord);
            double ycoord = ((Long)dataSnapshot.child("ycoord").getValue()).doubleValue();
            restaurant.setYCoord(ycoord);

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

    public class menuFetch implements ChildEventListener{
        private Menu menu;

        public menuFetch (Menu menu){
            this.menu=menu;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            menu.setRid((String)dataSnapshot.child("rid").getValue());
            menu.setName((String) dataSnapshot.child("name").getValue());
            menu.setBeverage((Boolean) dataSnapshot.child("beverage").getValue());
            menu.setDescription((String) dataSnapshot.child("description").getValue());
            menu.setPrice((Float) dataSnapshot.child("price").getValue());
            menu.setServiceFee((Boolean) dataSnapshot.child("serviceFee").getValue());
            menu.setType((Integer) dataSnapshot.child("type").getValue());
            menu.setImageLocal((String) dataSnapshot.child("imageLocalPath").getValue());

            menuData.setEdit(true);
            menuData.setMenu(menu);

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
