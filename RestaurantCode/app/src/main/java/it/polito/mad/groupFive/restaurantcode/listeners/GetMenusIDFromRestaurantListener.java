package it.polito.mad.groupFive.restaurantcode.listeners;

import android.content.Context;
import android.content.ContextWrapper;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import it.polito.mad.groupFive.restaurantcode.Home;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;

public class GetMenusIDFromRestaurantListener implements ValueEventListener {

    private Home.MenuAdapter ma;
    private Location location;
    private Context context;

    public GetMenusIDFromRestaurantListener(Home.MenuAdapter ma, Location location, Context context){
        this.ma = ma;
        this.location = location;
        this.context = context;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final String METHOD_NAME = this.getClass().getName()+" - onDCh";
        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            String rid = (String) ds.child("rid").getValue();
            float x = ((Double)ds.child("xcoord").getValue()).floatValue();
            float y = ((Double)ds.child("xcoord").getValue()).floatValue();
            Location here = new Location("dummy location");
            here.setLatitude(x);
            here.setLongitude(y);
            float distance = this.location.distanceTo(here);

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference menuRef = db.getReference("menu");
            Query menuQuery = menuRef.orderByChild("rid").equalTo(rid);
            menuQuery.addListenerForSingleValueEvent(new GetMenuFromRestaurantListener(ma, distance,this.context));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}

class GetMenuFromRestaurantListener implements ValueEventListener{

    private Home.MenuAdapter ma;
    private float distance;
    private Context context;

    GetMenuFromRestaurantListener(Home.MenuAdapter ma, float distance,Context context){
        this.ma = ma;
        this.distance = distance;
        Log.d("constructor","D: "+distance);
        this.context = context;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final String METHOD_NAME = this.getClass().getName()+" - onDCh";
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            try {
                final Menu m = new Menu(
                        (String) ds.child("rid").getValue(),
                        (String) ds.child("mid").getValue()
                );
                m.setBeverage((boolean) ds.child("beverage").getValue())
                        .setDescription((String) ds.child("description").getValue())
                        .setName((String) ds.child("name").getValue())
                        .setServiceFee((boolean) ds.child("serviceFee").getValue())
                        .setImageLocal((String) ds.child("imageLocalPath").getValue());
                Log.d("constructor","Distance: "+this.distance+" Name: "+m.getName());
                int type = ((Long) ds.child("type").getValue()).intValue();
                m.setType(type);
                float price = ((Long)ds.child("price").getValue()).floatValue();
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
                        ma.addChildWithDistance(m,distance);
                    }
                });
            } catch (MenuException e) {
                Log.e(METHOD_NAME, e.getMessage());
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
