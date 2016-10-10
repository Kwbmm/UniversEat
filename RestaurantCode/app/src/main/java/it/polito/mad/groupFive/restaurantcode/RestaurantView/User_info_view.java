package it.polito.mad.groupFive.restaurantcode.RestaurantView;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

public class User_info_view extends NavigationDrawer implements Restaurant_info_user.OnFragmentInteractionListener, Restaurant_info_user.restaurantData, Restaurant_menu_user.OnFragmentInteractionListener, Restaurant_menu_user.restaurantData, Review_user_view.OnFragmentInteractionListener, Review_user_view.restaurantData {
    Restaurant restaurant;
    RelativeLayout rest_i;
    RelativeLayout rest_m;
    RelativeLayout rest_r;
    ImageView line_i;
    ImageView line_m;
    ImageView line_r;
    TextView text_i;
    TextView text_m;
    TextView text_r;
    String mid;
    String rid;
    FirebaseDatabase db;
    ArrayList<Menu> menus;
    FileDownloadTask imageDownloadTask;
    ImageDownloadListener idl;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {


        try {
            super.onCreate(savedInstanceState);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle("");
            FrameLayout mlay = (FrameLayout) findViewById(R.id.frame);
            mlay.inflate(this, R.layout.activity_user_info_view, mlay);
            rid = getIntent().getExtras().getString("rid");
            mid = getIntent().getExtras().getString("mid");
            restaurant = new Restaurant(rid);
            menus = new ArrayList<>();
            db = FirebaseDatabase.getInstance();
            DatabaseReference reference = db.getReference("restaurant");
            reference.child(rid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    restaurant
                            .setName((String) dataSnapshot.child("name").getValue())
                            .setDescription((String) dataSnapshot.child("description").getValue())
                            .setAddress((String) dataSnapshot.child("address").getValue())
                            .setState((String) dataSnapshot.child("state").getValue())
                            .setCity((String) dataSnapshot.child("city").getValue())
                            .setWebsite((String) dataSnapshot.child("website").getValue())
                            .setTelephone((String) dataSnapshot.child("telephone").getValue())
                            .setZip((String) dataSnapshot.child("zip").getValue())
                            .setImageLocalPath((String) dataSnapshot.child("imageLocalPath").getValue());
                    restaurant.setTickets((Map<String,Boolean>) dataSnapshot.child("tickets").getValue());
                    float rating = Float.parseFloat(dataSnapshot.child("rating").getValue().toString());
                    restaurant.setRating(rating);
                    double xcoord = Double.parseDouble(dataSnapshot.child("xcoord").getValue().toString());
                    restaurant.setXCoord(xcoord);
                    double ycoord = Double.parseDouble(dataSnapshot.child("ycoord").getValue().toString());
                    restaurant.setYCoord(ycoord);
                    restaurant.setTimetableDinner((HashMap) dataSnapshot.child("timetableDinner").getValue());
                    restaurant.setTimetableLunch((HashMap) dataSnapshot.child("timetableLunch").getValue());
                    restaurant.setRatingNumber(Float.parseFloat(dataSnapshot.child("ratingNumber").getValue().toString()));
                    TextView r_name = (TextView) findViewById(R.id.r_name);
                    TextView r_rating = (TextView) findViewById(R.id.r_rating);
                    RatingBar r_ratingBar = (RatingBar) findViewById(R.id.r_ratingBar);
                    TextView r_reviews = (TextView) findViewById(R.id.r_reviews);
                    r_name.setText(restaurant.getName());
                    if(restaurant.getName().length()>18)
                        r_name.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
                    r_rating.setText(String.format("%.1f", restaurant.getRating()));
                    r_ratingBar.setRating(restaurant.getRating());
                    r_reviews.setText(String.format("%.0f", restaurant.getRatingNumber()) + " " + getResources().getString(R.string.review_based_on));
                    ImageView restImage = (ImageView) findViewById(R.id.restaurant_image);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference imageref = storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/restaurant/");
                    try {
                        getFromNetwork(imageref, restaurant.getRid(), restImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    rest_i = (RelativeLayout) findViewById(R.id.info_b);
                    line_i = (ImageView) findViewById(R.id.info_b_line);
                    text_i = (TextView) findViewById(R.id.info_b_text);
                    rest_i.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (line_i.getVisibility() != View.VISIBLE) {
                                text_i.setTextColor(Color.parseColor("#ffffff"));
                                text_r.setTextColor(Color.parseColor("#8A000000"));
                                text_m.setTextColor(Color.parseColor("#8A000000"));
                                line_i.setVisibility(View.VISIBLE);
                                line_r.setVisibility(View.INVISIBLE);
                                line_m.setVisibility(View.INVISIBLE);
                                Restaurant_info_user rest_view = new Restaurant_info_user();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out)
                                        .replace(R.id.uif_fragment, rest_view)
                                        .commit();
                            }

                        }
                    });

                    rest_m = (RelativeLayout) findViewById(R.id.menu_b);
                    line_m = (ImageView) findViewById(R.id.menu_b_line);
                    text_m = (TextView) findViewById(R.id.menu_b_text);
                    rest_m.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (line_m.getVisibility() != View.VISIBLE) {
                                Restaurant_menu_user rest_view = new Restaurant_menu_user();
                                if (line_i.getVisibility() == View.VISIBLE) {
                                    text_m.setTextColor(Color.parseColor("#ffffff"));
                                    text_i.setTextColor(Color.parseColor("#8A000000"));
                                    text_r.setTextColor(Color.parseColor("#8A000000"));
                                    line_i.setVisibility(View.INVISIBLE);
                                    line_r.setVisibility(View.INVISIBLE);
                                    line_m.setVisibility(View.VISIBLE);
                                    getSupportFragmentManager()
                                            .beginTransaction()
                                            .setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out)
                                            .replace(R.id.uif_fragment, rest_view)
                                            .commit();
                                }
                                if (line_r.getVisibility() == View.VISIBLE) {
                                    text_m.setTextColor(Color.parseColor("#ffffff"));
                                    text_i.setTextColor(Color.parseColor("#8A000000"));
                                    text_r.setTextColor(Color.parseColor("#8A000000"));
                                    line_i.setVisibility(View.INVISIBLE);
                                    line_r.setVisibility(View.INVISIBLE);
                                    line_m.setVisibility(View.VISIBLE);
                                    getSupportFragmentManager()
                                            .beginTransaction()
                                            .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out)
                                            .replace(R.id.uif_fragment, rest_view)
                                            .commit();
                                }
                            }
                        }
                    });

                    rest_r = (RelativeLayout) findViewById(R.id.review_b);
                    line_r = (ImageView) findViewById(R.id.review_b_line);
                    text_r = (TextView) findViewById(R.id.review_b_text);
                    rest_r.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (line_r.getVisibility() != View.VISIBLE) {
                                text_m.setTextColor(Color.parseColor("#8A000000"));
                                text_i.setTextColor(Color.parseColor("#8A000000"));
                                text_r.setTextColor(Color.parseColor("#ffffff"));
                                line_i.setVisibility(View.INVISIBLE);
                                line_r.setVisibility(View.VISIBLE);
                                line_m.setVisibility(View.INVISIBLE);
                                Review_user_view review_user_view = new Review_user_view();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out)
                                        .replace(R.id.uif_fragment, review_user_view)
                                        .commit();
                            }
                        }
                    });


                    if (mid == null) {
                        text_i.setTextColor(Color.parseColor("#ffffff"));
                        text_r.setTextColor(Color.parseColor("#8A000000"));
                        text_m.setTextColor(Color.parseColor("#8A000000"));
                        line_i.setVisibility(View.VISIBLE);
                        line_r.setVisibility(View.INVISIBLE);
                        line_m.setVisibility(View.INVISIBLE);
                        Restaurant_info_user rest_view = new Restaurant_info_user();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.uif_fragment, rest_view)
                                .commit();

                    } else {
                        text_m.setTextColor(Color.parseColor("#ffffff"));
                        text_i.setTextColor(Color.parseColor("#8A000000"));
                        text_r.setTextColor(Color.parseColor("#8A000000"));
                        line_i.setVisibility(View.INVISIBLE);
                        line_r.setVisibility(View.INVISIBLE);
                        line_m.setVisibility(View.VISIBLE);
                        Restaurant_menu_user menu_view = new Restaurant_menu_user();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.uif_fragment, menu_view)
                                .commit();
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (RestaurantException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause(){
        getSupportFragmentManager().popBackStack(null, android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        super.onPause();
    }

    private void getFromNetwork(StorageReference storageRoot, final String id, final ImageView imView) throws FileNotFoundException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        final File dir = cw.getDir("images", Context.MODE_PRIVATE);
        File filePath = new File(dir, id);
        this.imageDownloadTask = storageRoot.child(id).getFile(filePath);
        this.idl = new ImageDownloadListener(imView, dir, id);
        this.imageDownloadTask.addOnSuccessListener(this.idl);
    }

    private class ImageDownloadListener implements OnSuccessListener<FileDownloadTask.TaskSnapshot> {
        private ImageView imView;
        private File directory;
        private String id;

        public ImageDownloadListener(ImageView imageView, File imageDirectory, String imageID) {
            this.imView = imageView;
            this.directory = imageDirectory;
            this.id = imageID;
        }

        @Override
        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
            File img = new File(this.directory, this.id);
            Uri imgPath = Uri.fromFile(img);
            try {
                Bitmap b = new Picture(imgPath, getContentResolver()).getBitmap();
                this.imView.setImageBitmap(b);
            } catch (IOException e) {
                //Log.e("getFromNet", e.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public Restaurant getRestaurant() {
        return this.restaurant;
    }

    @Override
    public String getMid() {
        return mid;
    }

    @Override
    public ArrayList<Menu> getMenus() {
        return menus;
    }
}
