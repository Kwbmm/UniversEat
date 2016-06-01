package it.polito.mad.groupFive.restaurantcode.listeners;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.groupFive.restaurantcode.Home;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;

public class GetMenusIDFromRestaurantListener implements ValueEventListener {

    private Home.MenuAdapter ma;

    public GetMenusIDFromRestaurantListener(Home.MenuAdapter ma){
        this.ma = ma;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final String METHOD_NAME = this.getClass().getName()+" - onDCh";
        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            String rid = (String) ds.child("rid").getValue();
            float x = (float) ds.child("xcoord").getValue();
            float y = (float) ds.child("ycoord").getValue();
            /**
             * Get the coords of your location and compute the distance from each restaurant.
             * Keep the distance and supply it to the query for fetching the menus, together with
             * the mid.
             * That distance will be used to order the menus.
             */
            //float distance;

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference menuRef = db.getReference("menu");
            Query menuQuery = menuRef.orderByChild("rid").equalTo(rid);
            menuQuery.addListenerForSingleValueEvent(new GetMenuFromRestaurantListener(ma, -1)); //HERE pass the distance
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}

class GetMenuFromRestaurantListener implements ValueEventListener{

    private Home.MenuAdapter ma;
    private float distance;

    GetMenuFromRestaurantListener(Home.MenuAdapter ma, float distance){
        this.ma = ma;
        this.distance = distance;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final String METHOD_NAME = this.getClass().getName()+" - onDCh";
        try {
            Menu m = new Menu(
                    (String) dataSnapshot.child("rid").getValue(),
                    (String) dataSnapshot.child("mid").getValue()
            );
            m.setBeverage((boolean) dataSnapshot.child("beverage").getValue())
                    .setDescription((String) dataSnapshot.child("description").getValue())
                    .setName((String) dataSnapshot.child("name").getValue())
                    .setServiceFee((boolean) dataSnapshot.child("serviceFee").getValue());
            int type = ((Long) dataSnapshot.child("type").getValue()).intValue();
            m.setType(type);
            float price = ((Long)dataSnapshot.child("price").getValue()).floatValue();
            m.setPrice(price);
            ma.addChild(m,this.distance);
        } catch (MenuException e) {
            Log.e(METHOD_NAME, e.getMessage());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
