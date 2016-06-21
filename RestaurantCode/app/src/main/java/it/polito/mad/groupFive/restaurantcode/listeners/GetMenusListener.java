package it.polito.mad.groupFive.restaurantcode.listeners;

import android.content.Context;
import android.content.ContextWrapper;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import it.polito.mad.groupFive.restaurantcode.Home.MenuAdapter;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;

public class GetMenusListener implements ValueEventListener {
    private MenuAdapter ma;
    private Context context;

    public GetMenusListener(MenuAdapter ma,Context context) {
        this.ma = ma;
        this.context = context;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final String METHOD_NAME = this.getClass().getName() + " - onDataChange";
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            try {
                final Menu m = new Menu(
                        (String) ds.child("rid").getValue(),
                        (String) ds.child("mid").getValue()
                );
                m
                        .setBeverage((boolean)ds.child("beverage").getValue())
                        .setDescription((String)ds.child("description").getValue())
                        .setImageLocal((String)ds.child("imageLocalPath").getValue())
                        .setName((String)ds.child("name").getValue())
                        .setServiceFee((boolean)ds.child("serviceFee").getValue())
                        .setSpicy((boolean)ds.child("spicy").getValue())
                        .setGlutenfree((boolean)ds.child("glutenfree").getValue())
                        .setVegetarian((boolean)ds.child("vegetarian").getValue())
                        .setVegan((boolean)ds.child("vegan").getValue());
                int type = ((Long) ds.child("type").getValue()).intValue();
                m.setType(type);
                float price = Float.parseFloat(ds.child("price").getValue().toString());
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
                        ma.addChildNoDistance(m);
                    }
                });
            } catch (MenuException e) {
                //Log.e(METHOD_NAME,e.getMessage());
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }
}
