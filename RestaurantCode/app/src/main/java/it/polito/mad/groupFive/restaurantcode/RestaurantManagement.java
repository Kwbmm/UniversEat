package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import it.polito.mad.groupFive.restaurantcode.CreateRestaurant.CreateRestaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

public class RestaurantManagement extends NavigationDrawer {


    /* Shared preference note

    File name shared in string.xml
    can be used with:-> getString(R.string.user_pref)
    file contains:
    int uid (user id)
    int rid (restaurant id)

    Example:
    SharedPreferences sharedPreferences=this.getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putInt("uid",1);
            editor.apply();

    */
    private static final int CREATE_RESTAURANT = 1;
    private Restaurant restaurant;
    private DatabaseReference dbRoot;
    private StorageReference storageRoot;
    private GetRestaurantDataListener grdl;

    private String uid,rid;
    private FrameLayout mlay;

    private SharedPreferences sharedPreferences;
    private Boolean visible=true;
    private View v;
    private MenuItem plus;
    private View load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences=this.getSharedPreferences(getString(R.string.user_pref),RestaurantManagement.MODE_PRIVATE);
        getSupportActionBar().setTitle(R.string.actionBar_restaurantManagement);
        mlay= (FrameLayout) findViewById(R.id.frame);
        v=mlay.inflate(getBaseContext(), R.layout.restaurant_view_edit, mlay);
        load= LayoutInflater.from(this).inflate(R.layout.loading_bar,null);

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference myref=db.getReference("restaurant");
        uid=sharedPreferences.getString("uid",null);
        myref.orderByChild("uid").equalTo(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mlay.addView(load);
                if(plus!=null){
                    plus.setVisible(false);}
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("rid",(String)dataSnapshot.child("rid").getValue());
                editor.commit();
                showRestaurant();


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
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("actvity","Req: "+requestCode+" res"+resultCode);
        if(!visible){
            showRestaurant();
        }
        if (requestCode==3){
            update();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_add, menu);
        MenuItem item=menu.findItem(R.id.add_ab);
        plus=item;
        return true;
    }

    private boolean showRestaurant() {
        final SharedPreferences sharedPreferences=this.getSharedPreferences(getString(R.string.user_pref),RestaurantManagement.MODE_PRIVATE);
        final String METHOD_NAME = this.getClass().getName() + " - showRestaurant";
        this.rid = sharedPreferences.getString("rid",null);
        if ( this.rid != null){
            this.uid=sharedPreferences.getString("uid",null);
            LinearLayout rview= (LinearLayout) findViewById(R.id.fl_redit);
            v=rview.inflate(this,R.layout.resturant_view_edit_fragment,rview);
            ImageButton modify = (ImageButton) findViewById(R.id.restaurant_edit);
            modify.setOnClickListener(new onEditClick(1));
            visible=true;
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.restaurant_view_layout);
            if (rl != null) {
                rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent menuView =new Intent(v.getContext(),Menu_view_edit.class);
                        startActivity(menuView);
                    }
                });
            }
            getRestaurantData();
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor= sharedPreferences.edit();
        if(item.getItemId()==R.id.add_ab){
            Log.v("intent","newRest");
            Intent intent=new Intent(getApplicationContext(),CreateRestaurant.class);
            intent.putExtra("rid","-1");
            item.setVisible(false);
            startActivityForResult(intent,CREATE_RESTAURANT);

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean getRestaurantData(){
        final String METHOD_NAME = this.getClass().getName()+" - getRestaurantData";
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        this.dbRoot = db.getReference().child("restaurant").child(rid);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        this.storageRoot = storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/restaurant/");
        this.grdl = new GetRestaurantDataListener();
        this.dbRoot.addListenerForSingleValueEvent(this.grdl);
        return true;
    }

    private void getFromNetwork(StorageReference storageRoot, final String id, final ImageView imView) throws FileNotFoundException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        final File dir = cw.getDir("images",Context.MODE_PRIVATE);
        File filePath = new File(dir,id);
        storageRoot.child(id).getFile(filePath).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                File img = new File(dir, id);
                Uri imgPath = Uri.fromFile(img);
                try {
                    Bitmap b = new Picture(imgPath,getContentResolver()).getBitmap();
                    imView.setImageBitmap(b);
                } catch (IOException e) {
                    Log.e("getFromNet",e.getMessage());
                }
            }
        });
    }

    private void update()  {
        final String METHOD_NAME = this.getClass().getName()+" - readFiles";

        TextView rname= (TextView)findViewById(R.id.restaurant_name);
        TextView raddress= (TextView)findViewById(R.id.restaurant_address);
        RatingBar rbar=(RatingBar)findViewById(R.id.restaurant_rating);
        ImageView rmimw = (ImageView) findViewById(R.id.restaurant_image);
        try {
            getFromNetwork(storageRoot,restaurant.getRid(),rmimw);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        rname.setText(restaurant.getName());
        raddress.setText(restaurant.getCity());
        rbar.setRating(restaurant.getRating());
    }

    public class onEditClick implements View.OnClickListener{
        private int position;
        public onEditClick(int position){
            this.position=position;
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder dialog=new AlertDialog.Builder(RestaurantManagement.this);
            final CharSequence[] items = { "Edit", "Delete","Cancel" };
            dialog.setTitle("Edit Options");
            dialog.setItems(items,new onPositionClickDialog(position));
            dialog.show();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(this.grdl != null)
            this.dbRoot.removeEventListener(this.grdl);
    }

    private class GetRestaurantDataListener implements ValueEventListener{

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mlay.removeView(load);
            try {
                restaurant = new Restaurant(
                        (String)dataSnapshot.child("uid").getValue(),
                        (String)dataSnapshot.child("rid").getValue()
                );
            } catch (RestaurantException e) {
                Log.e("onDataChange",e.getMessage());
            }
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
            float rating = Float.parseFloat(dataSnapshot.child("rating").getValue().toString());
            restaurant.setRating(rating);
            double xcoord = Double.parseDouble(dataSnapshot.child("xcoord").getValue().toString());
            restaurant.setXCoord(xcoord);
            double ycoord = Double.parseDouble(dataSnapshot.child("ycoord").getValue().toString());
            restaurant.setYCoord(ycoord);
            restaurant.setRatingNumber(Float.parseFloat(dataSnapshot.child("ratingNumber").getValue().toString()));

            TextView rname= (TextView)findViewById(R.id.restaurant_name);
            TextView raddress= (TextView)findViewById(R.id.restaurant_address);
            RatingBar rbar=(RatingBar)findViewById(R.id.restaurant_rating);
            ImageView rmimw = (ImageView) findViewById(R.id.restaurant_image);
            if (rname != null) {
                rname.setText(restaurant.getName());
            }
            if (raddress != null) {
                raddress.setText(restaurant.getCity());
            }
            if (rbar != null) {
                rbar.setRating(restaurant.getRating());
            }
            if (rmimw != null ){
                try {
                    getFromNetwork(storageRoot,restaurant.getRid(),rmimw);
                } catch (FileNotFoundException e) {
                    Log.e("onDataChange",e.getMessage());
                }
            }
            Log.i("onDataChange","Restaurant data loaded");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w("onCancelled",databaseError.getMessage());
        }
    }

    public class onPositionClickDialog implements DialogInterface.OnClickListener{
        private class SuccessDeleteImageListener implements OnSuccessListener<Void>{

            @Override
            public void onSuccess(Void aVoid) {
                Log.i("onSuccess","Image was deleted successfully!");
            }
        }

        private int position;

        public onPositionClickDialog(int position){
            this.position=position;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case 0:{
                    Intent edit_menu=new Intent(getBaseContext(),CreateRestaurant.class);
                    edit_menu.putExtra("rid",restaurant.getRid());
                    startActivityForResult(edit_menu,3);
                    break;
                }
                case 1:{
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.remove("rid");
                    editor.apply();
                    final FirebaseDatabase db=FirebaseDatabase.getInstance();
                    DatabaseReference ref=db.getReference("restaurant");
                    ref.child(rid).removeValue();

                    //Delete image
                    SuccessDeleteImageListener sdil = new SuccessDeleteImageListener();
                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    StorageReference storageReference = firebaseStorage.getReference("restaurant");
                    storageReference.child(rid).delete().addOnSuccessListener(sdil);

                    ref=db.getReference("menu");
                    ref.orderByChild("rid").equalTo(rid).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            String mid=dataSnapshot.child("mid").getValue().toString();
                            DatabaseReference ref=db.getReference("menu");
                            ref.child(mid).removeValue();
                            ref=db.getReference("course");
                            ref.orderByChild("mid").equalTo(mid).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    String cid=dataSnapshot.child("cid").getValue().toString();
                                    DatabaseReference ref=db.getReference("course");
                                    ref.child(cid).removeValue();

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
                            });
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
                    });
                    v.setVisibility(View.INVISIBLE);
                    plus.setVisible(true);
                    break;
                }
                default:{
                    dialog.cancel();
                }
            }
        }
    }
}
