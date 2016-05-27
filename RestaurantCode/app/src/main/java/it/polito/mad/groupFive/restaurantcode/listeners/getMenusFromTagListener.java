package it.polito.mad.groupFive.restaurantcode.listeners;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;

public class GetMenusFromTagListener implements ValueEventListener {

    private ArrayList<Menu> menus;
    public GetMenusFromTagListener(ArrayList<Menu> menus) {
        this.menus = menus;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            FirebaseDatabase menuDB = FirebaseDatabase.getInstance();
            DatabaseReference dr = menuDB.getReference("menu/"+ds.getKey());
            GetMenuListener getMenuListener = new GetMenuListener(this.menus);
            dr.addListenerForSingleValueEvent(getMenuListener);
            this.menus = getMenuListener.getMenus();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}

class GetMenuListener implements ValueEventListener{
    private ArrayList<Menu> menus;
    GetMenuListener(ArrayList<Menu> menus){
        this.menus = menus;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final String METHOD_NAME = this.getClass().getName()+" - onDataChange";
        try {
            Menu m = new Menu(
                    (String) dataSnapshot.child("rid").getValue(),
                    (String) dataSnapshot.child("mid").getValue()
            );
            m.setBeverage((boolean) dataSnapshot.child("beverage").getValue())
                    .setDescription((String) dataSnapshot.child("description").getValue())
                    .setName((String) dataSnapshot.child("name").getValue())
                    .setServiceFee((boolean) dataSnapshot.child("serviceFee").getValue())
                    .setType((int) dataSnapshot.child("type").getValue());
            float price = ((Long)dataSnapshot.child("price").getValue()).floatValue();
            m.setPrice(price);
        } catch (MenuException e) {
            Log.e(METHOD_NAME, e.getMessage());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public ArrayList<Menu> getMenus(){ return this.menus; }
}
