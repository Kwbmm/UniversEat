package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
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

import it.polito.mad.groupFive.restaurantcode.datastructures.*;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CourseException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;


public class Menu_details_view extends NavigationDrawer {

    private Restaurant restaurant;
    private it.polito.mad.groupFive.restaurantcode.datastructures.Menu menu;
    String mid;
    String rid;
    SharedPreferences sharedPreferences;
    private ArrayList<Course> courses;
    private CourseAdapter courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.actionBar_menu);
        sharedPreferences=this.getSharedPreferences("RestaurantCode.Userdata",this.MODE_PRIVATE);
        mid=getIntent().getExtras().getString("mid");
        rid=getIntent().getExtras().getString("rid");
        Log.e("RID E MID",""+rid+" "+mid);
        courses=new ArrayList<>();
        FirebaseDatabase db;
        db=FirebaseDatabase.getInstance();
        DatabaseReference ref=db.getReference("course");
        DatabaseReference refmenu=db.getReference("menu");
        refmenu.child(mid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    menu= new it.polito.mad.groupFive.restaurantcode.datastructures.Menu(mid);
                    menu.setRid((String)dataSnapshot.child("rid").getValue());
                    menu.setName((String) dataSnapshot.child("name").getValue());
                    if(dataSnapshot.child("beverage").getValue()!=null){
                        menu.setBeverage((Boolean) dataSnapshot.child("beverage").getValue());}
                    else menu.setBeverage(false);
                    menu.setDescription((String) dataSnapshot.child("description").getValue());
                    menu.setPrice(Float.parseFloat(dataSnapshot.child("price").getValue().toString()));
                    if(dataSnapshot.child("serviceFee").getValue()!=null){
                        menu.setServiceFee((Boolean) dataSnapshot.child("serviceFee").getValue());}
                    else menu.setServiceFee(false);
                    menu.setType(Integer.parseInt( dataSnapshot.child("type").getValue().toString()));
                    //menu.setImageLocal((String) dataSnapshot.child("imageLocalPath").getValue());



                    FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
                    mlay.inflate(getBaseContext(), R.layout.menu_details_view, mlay);
                    showmenu();

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
                Course c= new Course();
                c.setMid(dataSnapshot.child("mid").getValue().toString());
                c.setCid(dataSnapshot.child("cid").getValue().toString());
                c.setName(dataSnapshot.child("name").getValue().toString());
                c.setGlutenFree((boolean)dataSnapshot.child("glutenFree").getValue());
                c.setSpicy((boolean)dataSnapshot.child("spicy").getValue());
                c.setVegan((boolean)dataSnapshot.child("vegan").getValue());
                c.setVegetarian((boolean)dataSnapshot.child("vegetarian").getValue());
                courses.add(c);
                courseAdapter.notifyDataSetChanged();


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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    private void showmenu(){
        TextView name = (TextView) findViewById(R.id.menu_name);
        TextView description = (TextView) findViewById(R.id.menu_description);
        TextView beverage = (TextView) findViewById(R.id.beverage);
        TextView servicefee = (TextView) findViewById(R.id.servicefee);
        TextView price = (TextView) findViewById(R.id.menu_price);
        ImageView pic = (ImageView) findViewById(R.id.menu_image);
        ListView listView = (ListView) findViewById(R.id.courseList);
        name.setText(menu.getName());
        description.setText(menu.getDescription());
        if(menu.isBeverage()) beverage.setText(getString(R.string.Menu_details_beverage_incl));
        else beverage.setText(getString(R.string.Menu_details_beverage_not));
        if(menu.isServiceFee()) servicefee.setText(getString(R.string.Menu_details_service_fee_incl));
        else servicefee.setText(getString(R.string.Menu_details_service_fee_not));
        price.setText(String.format("%.2f", menu.getPrice())+"€");
        try {
            FirebaseStorage storage=FirebaseStorage.getInstance();
            StorageReference imageref=storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/menus/");
            getFromNetwork(imageref,menu.getMid(),pic);
        } catch (NullPointerException e){
            Log.e("immagine non caricata"," ");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        courseAdapter = new CourseAdapter();
        listView.setAdapter(courseAdapter);
    }

    public class CourseAdapter extends BaseAdapter {

        public CourseAdapter(){

        }

        @Override
        public int getCount() { return courses.size(); }

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
            convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.menu_details_course, null);
            Course course = courses.get(position);
            TextView course_name = (TextView) convertView.findViewById(R.id.course_name);
            ImageView img_hot = (ImageView) convertView.findViewById(R.id.img_hot);
            ImageView img_nogluten = (ImageView) convertView.findViewById(R.id.img_nogluten);
            ImageView img_vgt = (ImageView) convertView.findViewById(R.id.img_vgt);
            ImageView img_vgn = (ImageView) convertView.findViewById(R.id.img_vgn);
            course_name.setText(course.getName());
            if(!course.isSpicy()) img_hot.setColorFilter(Color.GRAY);
            if(!course.isGlutenFree()) img_nogluten.setColorFilter(Color.GRAY);
            if(!course.isVegetarian()) img_vgt.setColorFilter(Color.GRAY);
            if(!course.isVegan()) img_vgn.setColorFilter(Color.GRAY);
            return convertView;
        }
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

}

