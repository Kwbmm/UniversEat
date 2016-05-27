package it.polito.mad.groupFive.restaurantcode.listeners;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.groupFive.restaurantcode.SearchResult;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;

public class GetMenusIDFromCourseListener implements ValueEventListener {

    private SearchResult.MenuAdapter ma;

    public GetMenusIDFromCourseListener(SearchResult.MenuAdapter ma){
        this.ma = ma;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final String METHOD_NAME = this.getClass().getName()+" - onDCh";
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            String menuID = (String)ds.child("mid").getValue();
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference menuRef = db.getReference("menu").child(menuID);
            menuRef.addListenerForSingleValueEvent(new GetMenuListener(this.ma));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}

class GetMenuListener implements ValueEventListener{

    private SearchResult.MenuAdapter ma;
    GetMenuListener(SearchResult.MenuAdapter ma){
        this.ma = ma;
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
            ma.onAddChild(m);
        } catch (MenuException e) {
            Log.e(METHOD_NAME, e.getMessage());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
