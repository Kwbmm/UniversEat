package it.polito.mad.groupFive.restaurantcode.listeners;

import android.content.Context;
import android.content.ContextWrapper;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Map;

import it.polito.mad.groupFive.restaurantcode.SearchRestaurants.RestaurantAdapter;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

public class GetRestaurantListener implements ValueEventListener {

    private RestaurantAdapter ra;
    private Context context;
    private Location lastKnow;

    public GetRestaurantListener(RestaurantAdapter ra, Context context) {
        this(ra,null,context);
    }

    public GetRestaurantListener(RestaurantAdapter ra, Location lastKnown, Context context) {
        this.ra = ra;
        this.lastKnow = lastKnown;
        this.context = context;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final String METHOD_NAME = this.getClass().getName() + " - onDCh";
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            try {
                final Restaurant r = new Restaurant(
                        (String) ds.child("uid").getValue(),
                        (String) ds.child("rid").getValue()
                );
                r.setAddress((String)ds.child("address").getValue())
                        .setCity((String)ds.child("city").getValue())
                        .setDescription((String)ds.child("description").getValue())
                        .setImageLocalPath((String)ds.child("imageLocalPath").getValue())
                        .setName((String)ds.child("name").getValue())
                        .setState((String)ds.child("state").getValue())
                        .setTelephone((String)ds.child("telephone").getValue())
                        .setWebsite((String)ds.child("website").getValue())
                        .setZip((String)ds.child("zip").getValue())
                        .setXCoord((Double)ds.child("xcoord").getValue())
                        .setYCoord((Double)ds.child("ycoord").getValue());
                float rating = Float.parseFloat(ds.child("rating").getValue().toString());
                r.setRating(rating);
                float ratingNum = Float.parseFloat(ds.child("ratingNumber").getValue().toString());
                r.setRatingNumber(ratingNum);
                Map<String,Boolean> tickets = (Map<String,Boolean>) ds.child("tickets").getValue();
                r.setTickets(tickets);
                Map<String,Map<String,String>> timetableLunch = (Map<String,Map<String,String>>) ds.child("timetableLunch").getValue();
                r.setTimetableLunch(timetableLunch);
                Map<String,Map<String,String>> timetableDinner = (Map<String,Map<String,String>>) ds.child("timetableDinner").getValue();
                r.setTimetableDinner(timetableDinner);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRoot = storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/restaurant/");
                ContextWrapper cw = new ContextWrapper(this.context);
                File dir = cw.getDir("images", Context.MODE_PRIVATE);
                final File filePath = new File(dir,r.getRid());
                storageRoot.child(r.getRid()).getFile(filePath).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        r.setImageLocalPath(filePath.getAbsolutePath());
                        if(lastKnow == null){
                            ra.addChildNoDistance(r);
                        }
                        else{
                            Location here = new Location("dummy location");
                            here.setLatitude(r.getXCoord());
                            here.setLongitude(r.getYCoord());
                            float distance = lastKnow.distanceTo(here);
                            ra.addChildWithDistance(r,distance);
                        }
                    }
                });
            } catch (RestaurantException e) {
                Log.e(METHOD_NAME,e.getMessage());
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        final String METHOD_NAME = this.getClass().getName() + " - onCanc";
        Log.e(METHOD_NAME,databaseError.getMessage());

    }
}
