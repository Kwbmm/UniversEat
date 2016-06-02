package it.polito.mad.groupFive.restaurantcode.listeners;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.groupFive.restaurantcode.SearchMenuResults;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;

public class GetMenusIDFromCourseListener implements ValueEventListener {

    private SearchMenuResults.MenuAdapter ma;
    private String[] query;

    public GetMenusIDFromCourseListener(SearchMenuResults.MenuAdapter ma, String[] query){
        this.ma = ma;
        this.query = query;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final String METHOD_NAME = this.getClass().getName()+" - onDCh";
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            int weight = 0;
            for(String keyword : this.query){
                if(ds.child(keyword).getValue() == null){
                    //If the keyword is not found, the element has less relevance, so we increase its weight
                    weight++;
                }
            }
            String menuID = (String)ds.child("mid").getValue();
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference menuRef = db.getReference("menu").child(menuID);
            menuRef.addListenerForSingleValueEvent(new GetMenuFromCourseListener(this.ma,weight));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}

class GetMenuFromCourseListener implements ValueEventListener{

    private SearchMenuResults.MenuAdapter ma;
    private int weight;
    GetMenuFromCourseListener(SearchMenuResults.MenuAdapter ma, int weight){
        this.ma = ma;
        this.weight = weight;
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
            ma.addChild(m,this.weight);
        } catch (MenuException e) {
            Log.e(METHOD_NAME, e.getMessage());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
