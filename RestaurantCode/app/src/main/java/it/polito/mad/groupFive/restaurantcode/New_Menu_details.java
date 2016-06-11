package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.RestaurantView.User_info_view;
import it.polito.mad.groupFive.restaurantcode.datastructures.Course;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

/**
 * Created by Cristiano on 11/06/2016.
 */
public class New_Menu_details extends Fragment {

    private Restaurant restaurant;
    private it.polito.mad.groupFive.restaurantcode.datastructures.Menu menu;
    String mid;
    String rid;
    SharedPreferences sharedPreferences;
    private ArrayList<Course> courses;
    private CourseAdapter courseAdapter;
    public New_Menu_details() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v=inflater.inflate(R.layout.fragment_menu_details, container, false);
        sharedPreferences=getContext().getSharedPreferences(getString(R.string.user_pref),getContext().MODE_PRIVATE);
        mid=getArguments().getString("mid");
        rid=getArguments().getString("rid");
        try {
            restaurant=new Restaurant(rid);
        } catch (RestaurantException e) {
            e.printStackTrace();
        }
        courses = new ArrayList<>();
        FirebaseDatabase db;
        db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("course");
        DatabaseReference refmenu = db.getReference("menu");
        DatabaseReference refrest = db.getReference("restaurant");
        if (getArguments() != null) {

        }
        refmenu.child(mid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    menu = new it.polito.mad.groupFive.restaurantcode.datastructures.Menu(mid);
                    menu.setRid((String) dataSnapshot.child("rid").getValue());
                    menu.setName((String) dataSnapshot.child("name").getValue());
                    if (dataSnapshot.child("beverage").getValue() != null) {
                        menu.setBeverage((Boolean) dataSnapshot.child("beverage").getValue());
                    } else menu.setBeverage(false);
                    menu.setDescription((String) dataSnapshot.child("description").getValue());
                    menu.setPrice(Float.parseFloat(dataSnapshot.child("price").getValue().toString()));
                    if (dataSnapshot.child("serviceFee").getValue() != null) {
                        menu.setServiceFee((Boolean) dataSnapshot.child("serviceFee").getValue());
                    } else menu.setServiceFee(false);
                    menu.setType(Integer.parseInt(dataSnapshot.child("type").getValue().toString()));
                        TextView name = (TextView) v.findViewById(R.id.menu_name);
                        TextView description = (TextView) v.findViewById(R.id.menu_description);
                        TextView beverage = (TextView) v.findViewById(R.id.beverage);
                        TextView servicefee = (TextView) v.findViewById(R.id.servicefee);
                        TextView price = (TextView) v.findViewById(R.id.menu_price);
                        ImageView pic = (ImageView) v.findViewById(R.id.menu_image);
                        ListView listView = (ListView) v.findViewById(R.id.courseList);
                    FloatingActionButton fab=(FloatingActionButton)v.findViewById(R.id.fab);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uid = sharedPreferences.getString("uid", null);
                                Boolean logged = sharedPreferences.getBoolean("logged", false);
                                if (uid != null && logged == true) {
                                    Intent ordernow = new Intent(getActivity().getBaseContext(), MakeOrder.class);
                                    ordernow.putExtra("rid", rid);
                                    ordernow.putExtra("mid", mid);
                                    ordernow.putExtra("uid", uid);
                                    ordernow.putExtra("name", menu.getName());
                                    startActivity(ordernow);
                                } else {
                                    Toast.makeText(getActivity().getBaseContext(), "You need to login first!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        name.setText(menu.getName());
                        description.setText(menu.getDescription());
                        if (menu.isBeverage()) beverage.setText(getString(R.string.Menu_details_beverage_incl));
                        else beverage.setText(getString(R.string.Menu_details_beverage_not));
                        if (menu.isServiceFee())
                            servicefee.setText(getString(R.string.Menu_details_service_fee_incl));
                        else servicefee.setText(getString(R.string.Menu_details_service_fee_not));
                        price.setText(String.format("%.2f", menu.getPrice()) + "€");

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference imageref = storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/menus/");
                        try {
                            getFromNetwork(imageref, menu.getMid(), pic);
                        } catch (NullPointerException e) {
                            Log.e("immagine non caricata", " ");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        courseAdapter = new CourseAdapter();
                        listView.setAdapter(courseAdapter);


                } catch (MenuException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ref.orderByChild("mid").equalTo(mid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Course c = new Course();
                c.setMid(dataSnapshot.child("mid").getValue().toString());
                c.setCid(dataSnapshot.child("cid").getValue().toString());
                c.setName(dataSnapshot.child("name").getValue().toString());
                c.setGlutenFree((boolean) dataSnapshot.child("glutenFree").getValue());
                c.setSpicy((boolean) dataSnapshot.child("spicy").getValue());
                c.setVegan((boolean) dataSnapshot.child("vegan").getValue());
                c.setVegetarian((boolean) dataSnapshot.child("vegetarian").getValue());
                courses.add(c);
                try {
                    courseAdapter.notifyDataSetChanged();
                } catch (NullPointerException e){

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
        refrest.child(rid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                restaurant.setName((String) dataSnapshot.child("name").getValue())
                        .setImageLocalPath((String) dataSnapshot.child("imageLocalPath").getValue());
                TextView restname=(TextView)v.findViewById(R.id.restaurant_name);
                restname.setText(restaurant.getName());
                RelativeLayout restlayout=(RelativeLayout)v.findViewById(R.id.restaurantLayout);
                restlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent restaurant_view =new Intent(getContext(),User_info_view.class);
                        restaurant_view.putExtra("rid",restaurant.getRid());
                        startActivity(restaurant_view);
                    }
                });
                ImageView restImage=(ImageView)v.findViewById(R.id.restaurant_image);

                FirebaseStorage storage=FirebaseStorage.getInstance();
                StorageReference imageref=storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/restaurant/");
                try {
                    getFromNetwork(imageref,restaurant.getRid(),restImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }
    public class CourseAdapter extends BaseAdapter {

        public CourseAdapter() {

        }

        @Override
        public int getCount() {
            return courses.size();
        }

        @Override
        public Object getItem(int position) {
            return courses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.menu_details_course, null);
            Course course = courses.get(position);
            TextView course_name = (TextView) convertView.findViewById(R.id.course_name);
            ImageView img_hot = (ImageView) convertView.findViewById(R.id.img_hot);
            ImageView img_nogluten = (ImageView) convertView.findViewById(R.id.img_nogluten);
            ImageView img_vgt = (ImageView) convertView.findViewById(R.id.img_vgt);
            ImageView img_vgn = (ImageView) convertView.findViewById(R.id.img_vgn);
            course_name.setText(course.getName());
            if (!course.isSpicy()) img_hot.setColorFilter(Color.GRAY);
            if (!course.isGlutenFree()) img_nogluten.setColorFilter(Color.GRAY);
            if (!course.isVegetarian()) img_vgt.setColorFilter(Color.GRAY);
            if (!course.isVegan()) img_vgn.setColorFilter(Color.GRAY);
            return convertView;
        }
    }

    private void getFromNetwork(StorageReference storageRoot, final String id, final ImageView imView) throws FileNotFoundException {
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        final File dir = cw.getDir("images", Context.MODE_PRIVATE);
        File filePath = new File(dir, id);
        storageRoot.child(id).getFile(filePath).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                File img = new File(dir, id);
                Uri imgPath = Uri.fromFile(img);
                try {
                    Bitmap b = new Picture(imgPath, getActivity().getContentResolver()).getBitmap();
                    imView.setImageBitmap(b);
                } catch (IOException e) {
                    Log.e("getFromNet", e.getMessage());
                } catch (NullPointerException e){

                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
