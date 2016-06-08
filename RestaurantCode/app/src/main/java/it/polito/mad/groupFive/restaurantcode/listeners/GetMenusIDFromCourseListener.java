package it.polito.mad.groupFive.restaurantcode.listeners;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import it.polito.mad.groupFive.restaurantcode.Home.SearchMenuResults;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;

public class GetMenusIDFromCourseListener implements ValueEventListener {

    private SearchMenuResults.MenuAdapter ma;
    private String[] query;
    private Context context;

    public GetMenusIDFromCourseListener(SearchMenuResults.MenuAdapter ma, String[] query,Context context){
        this.ma = ma;
        this.query = query;
        this.context = context;
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
            menuRef.addListenerForSingleValueEvent(new GetMenuFromCourseListener(this.ma,weight,this.context));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}

class GetMenuFromCourseListener implements ValueEventListener{

    private SearchMenuResults.MenuAdapter ma;
    private int weight;
    private Context context;

    GetMenuFromCourseListener(SearchMenuResults.MenuAdapter ma, int weight, Context context){
        this.ma = ma;
        this.weight = weight;
        this.context = context;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final String METHOD_NAME = this.getClass().getName()+" - onDCh";
        try {
            final Menu m = new Menu(
                    (String) dataSnapshot.child("rid").getValue(),
                    (String) dataSnapshot.child("mid").getValue()
            );
            m.setBeverage((boolean) dataSnapshot.child("beverage").getValue())
                    .setDescription((String) dataSnapshot.child("description").getValue())
                    .setName((String) dataSnapshot.child("name").getValue())
                    .setServiceFee((boolean) dataSnapshot.child("serviceFee").getValue())
                    .setImageLocal((String) dataSnapshot.child("imageLocalPath").getValue())
                    .setSpicy((boolean)dataSnapshot.child("spicy").getValue())
                    .setGlutenfree((boolean)dataSnapshot.child("glutenfree").getValue())
                    .setVegan((boolean)dataSnapshot.child("vegan").getValue())
                    .setVegetarian((boolean)dataSnapshot.child("vegetarian").getValue());
            int type = ((Long) dataSnapshot.child("type").getValue()).intValue();
            m.setType(type);
            float price = Float.parseFloat(dataSnapshot.child("price").getValue().toString());
            m.setPrice(price);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRoot = storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/menus/");
            ContextWrapper cw = new ContextWrapper(this.context);
            File dir = cw.getDir("images", Context.MODE_PRIVATE);
            final File filePath = new File(dir,m.getMid());
            storageRoot.child(m.getMid()).getFile(filePath).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    m.setImageLocal(filePath.getAbsolutePath());
                    ma.addChild(m,weight);
                }
            });
        } catch (MenuException e) {
            Log.e(METHOD_NAME, e.getMessage());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
