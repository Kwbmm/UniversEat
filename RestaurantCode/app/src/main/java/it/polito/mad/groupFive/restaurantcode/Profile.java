package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.ArrayList;
import java.util.List;

import it.polito.mad.groupFive.restaurantcode.RestaurantView.User_info_view;

import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;

import it.polito.mad.groupFive.restaurantcode.datastructures.User;

import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.holders.RestaurantViewHolder;


/**
 * Created by Cristiano on 05/05/2016.
 */
public class Profile extends NavigationDrawer {

    String uid;
    User user;
    FirebaseDatabase db;
    DatabaseReference myRef;
    ArrayList<String> favourite;
    SharedPreferences sharedPreferences;
    boolean restOwner;
    FrameLayout mlay;
    TextView name;
    TextView username;
    TextView email;
    ImageView image;
    ListView lv;
    ProfileAdapter profileAdapter;
    List<Restaurant> re;
    ImageButton edit;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.actionBar_profile);
        mlay= (FrameLayout) findViewById(R.id.frame);
        sharedPreferences=this.getSharedPreferences("RestaurantCode.Userdata",this.MODE_PRIVATE);
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        re=new ArrayList<Restaurant>();
        favourite=new ArrayList<>();
        if(uid==null) finish();
        restOwner=sharedPreferences.getBoolean("owner",Boolean.FALSE);
        db = FirebaseDatabase.getInstance();
        DatabaseReference Myref = db.getReference("User");

        user =new User(uid);
        Myref.orderByChild("uid").equalTo(uid).addChildEventListener(new DataList(user));





        View load= LayoutInflater.from(this).inflate(R.layout.loading_bar,null);
        mlay.addView(load);
        ProgressBar pb=(ProgressBar)findViewById(R.id.progressBar_loading);
        pb.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);



        //generadatifittizi();


    }



    private void showProfile() {
        name = (TextView) findViewById(R.id.user_name);
        username = (TextView) findViewById(R.id.user_username);
        email = (TextView) findViewById(R.id.user_email);
        image= (ImageView) findViewById(R.id.user_image);
        lv=(ListView) findViewById(R.id.recycler_favourite);
        edit= (ImageButton) findViewById(R.id.editprofile);

    }
    public class ProfileAdapter extends BaseAdapter {
        Context context;
        Restaurant restaurant;
        public ProfileAdapter(Context context){

        }

        @Override
        public int getCount() { return re.size(); }

        @Override
        public Object getItem(int position) {
            return re.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.restaurant_view, null);
            final Restaurant rest = re.get(position);

            TextView name= (TextView)convertView.findViewById(R.id.restaurant_name);
            TextView address= (TextView)convertView.findViewById(R.id.restaurant_address);
            RatingBar rbar=(RatingBar)convertView.findViewById(R.id.restaurant_rating);
            ImageView img = (ImageView) convertView.findViewById(R.id.restaurant_image);
            CardView card = (CardView) convertView.findViewById(R.id.restaurant_card);
            ImageButton fav_b=(ImageButton) convertView.findViewById(R.id.restaurant_favourite);
            TextView distance=(TextView) convertView.findViewById(R.id.restaurant_distance);
            distance.setText("");
            fav_b.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
            fav_b.setOnClickListener(new OnFavourite(fav_b,rest));
            name.setText(rest.getName());
            address.setText(rest.getAddress());
            rbar.setRating(rest.getRating());
            try {

                FirebaseStorage storage=FirebaseStorage.getInstance();
                StorageReference imageref=storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/restaurant/");
                getFromNetwork(imageref,rest.getRid(),img);
                //  img.setImageBitmap(restaurant.getImageBitmap());
            } catch (NullPointerException e){
                Log.e("immagine non caricata"," ");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), User_info_view.class);
                    intent.putExtra("rid",rest.getRid());
                    intent.putExtra("mid","-1");
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }
    public class DataList implements ChildEventListener {
        public User user;

        public DataList(User user){
            this.user=user;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            this.user=dataSnapshot.getValue(user.getClass());


            Log.v("name",user.getName());

            try {
                FirebaseStorage storage=FirebaseStorage.getInstance();
                StorageReference user_img=storage.getReference("Users");
                StorageReference img = user_img.child(user.getUid()+".jpg");

                final long ONE_MEGABYTE = 1024 * 1024;
                img.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        mlay.removeAllViews();
                        mlay.inflate(getBaseContext(), R.layout.profile, mlay);
                        showProfile();

                        name.setText(user.getName()+" "+user.getSurname());
                        username.setText(user.getUserName());
                        email.setText(user.getEmail());
                        image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.v("Click","Prova click edit owner");
                                Intent edit = new Intent(getBaseContext(), EditProfile.class);
                                startActivity(edit);

                            }
                        });


                        profileAdapter= new ProfileAdapter(getBaseContext());
                        lv.setAdapter(profileAdapter);
                        DatabaseReference fav=db.getReference("favourite");
                        fav.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> rfav=dataSnapshot.getChildren();
                                for(DataSnapshot d:rfav) {
                                    favourite.add(d.getKey());

                                    DatabaseReference rf = db.getReference("restaurant");
                                    rf.orderByChild("rid").equalTo(d.getKey()).addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                            try {
                                                Restaurant r = new Restaurant(
                                                        (String) dataSnapshot.child("uid").getValue(),
                                                        (String) dataSnapshot.child("rid").getValue()
                                                );

                                                r
                                                        .setName((String) dataSnapshot.child("name").getValue())
                                                        .setDescription((String) dataSnapshot.child("description").getValue())
                                                        .setAddress((String) dataSnapshot.child("address").getValue())
                                                        .setState((String) dataSnapshot.child("state").getValue())
                                                        .setCity((String) dataSnapshot.child("city").getValue())
                                                        .setWebsite((String) dataSnapshot.child("website").getValue())
                                                        .setTelephone((String) dataSnapshot.child("telephone").getValue())
                                                        .setZip((String) dataSnapshot.child("zip").getValue())
                                                        .setImageLocalPath((String) dataSnapshot.child("imageLocalPath").getValue());
                                                float rating = Float.parseFloat(dataSnapshot.child("rating").getValue().toString());
                                                r.setRating(rating);
                                                double xcoord = Double.parseDouble(dataSnapshot.child("xcoord").getValue().toString());
                                                r.setXCoord(xcoord);
                                                double ycoord = Double.parseDouble(dataSnapshot.child("ycoord").getValue().toString());
                                                r.setYCoord(ycoord);
                                                r.setRatingNumber(Float.parseFloat(dataSnapshot.child("ratingNumber").getValue().toString()));


                                                re.add(r);
                                                profileAdapter.notifyDataSetChanged();
                                            } catch (RestaurantException e) {
                                                e.printStackTrace();
                                            }

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
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });




                //image.setImageBitmap(user.getImageBitmap());



            } catch (NullPointerException e){
                Log.e("immagine non caricata"," ");
            }

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

    private void getFromNetwork(StorageReference storageRoot, final String id, final ImageView imView) throws FileNotFoundException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        final File dir = cw.getDir("images", Context.MODE_PRIVATE);
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
    public class OnFavourite implements View.OnClickListener{
        private ImageButton imageButton;
        private Restaurant restaurant;
        private ArrayList<String> menusList;


        public OnFavourite(ImageButton imageButton, Restaurant restaurant) {
            this.imageButton = imageButton;
            this.restaurant = restaurant;
            menusList=new ArrayList<>();
        }

        @Override
        public void onClick(View v) {
            if (favourite.contains(restaurant.getRid())){
                //is favourite
                favourite.remove(restaurant.getRid());
                imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border_black_24dp));
                FirebaseDatabase db=FirebaseDatabase.getInstance();
                DatabaseReference ref=db.getReference("favourite");
                ref.child(uid).child(restaurant.getRid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
                //remove on server
            }
            else{
                favourite.add(restaurant.getRid());
                //add on server
                imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                FirebaseDatabase db=FirebaseDatabase.getInstance();
                final DatabaseReference menus=db.getReference("menu");
                menus.orderByChild("rid").equalTo(restaurant.getRid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FirebaseDatabase db=FirebaseDatabase.getInstance();
                        DatabaseReference ref=db.getReference("favourite");
                        Iterable<DataSnapshot> favs=dataSnapshot.getChildren();
                        for(DataSnapshot d:favs){

                            ref.child(uid).child(restaurant.getRid()).child(d.getKey()).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {



                                }
                            });




                        }
                        Toast.makeText(getBaseContext(),restaurant.getName()+" now is your favourite!",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        }
    }
}