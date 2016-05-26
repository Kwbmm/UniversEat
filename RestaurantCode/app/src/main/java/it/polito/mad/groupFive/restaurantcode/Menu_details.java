package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.datastructures.*;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CourseException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

/**
 * Created by Cristiano on 24/04/2016.
 */
public class Menu_details extends NavigationDrawer {

    private Restaurant restaurant;
    private it.polito.mad.groupFive.restaurantcode.datastructures.Menu menu;
    int mid;
    int rid;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mid=getIntent().getExtras().getInt("mid");
        rid=getIntent().getExtras().getInt("rid");
        Log.e("RID E MID",""+rid+" "+mid);
        super.onCreate(savedInstanceState);
        sharedPreferences=this.getSharedPreferences("RestaurantCode.Userdata",this.MODE_PRIVATE);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.menu_details, mlay);
        showmenu();
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
        final Button ordernow = (Button) findViewById(R.id.orderButton);
        ordernow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int uid = sharedPreferences.getInt("uid",-1);
                Log.e("UID",sharedPreferences.getInt("uid",-1)+" ");
                if(uid!=-1) {
                    Intent ordernow = new Intent(getBaseContext(), MakeOrder.class);
                    ordernow.putExtra("rid", rid);
                    ordernow.putExtra("mid", mid);
                    ordernow.putExtra("uid", uid);
                    startActivity(ordernow);
                }
                else {
                    Toast.makeText(getBaseContext(), "You need to login first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ArrayList<Course> courses = menu.getCourses();
        name.setText(menu.getName());
        description.setText(menu.getDescription());
        if(menu.isBeverage()) beverage.setText(getString(R.string.Menu_details_beverage_incl));
        else beverage.setText(getString(R.string.Menu_details_beverage_not));
        if(menu.isServiceFee()) servicefee.setText(getString(R.string.Menu_details_service_fee_incl));
        else servicefee.setText(getString(R.string.Menu_details_service_fee_not));
        price.setText(String.format("%.2f", menu.getPrice())+"€");
        Log.e("numero di courses",courses.size()+" ");
        CourseAdapter courseAdapter = new CourseAdapter(this,courses);
        listView.setAdapter(courseAdapter);
    }

    public class CourseAdapter extends BaseAdapter {
        ArrayList<Course> courses;
        Context context;
        public CourseAdapter(Context context,ArrayList<Course> courses){
            this.courses=courses;
            this.context=context;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.menu_details_course, null);
            Course course = courses.get(position);
            TextView course_name = (TextView) convertView.findViewById(R.id.course_name);
            ImageView img_hot = (ImageView) convertView.findViewById(R.id.img_hot);
            ImageView img_nogluten = (ImageView) convertView.findViewById(R.id.img_nogluten);
            ImageView img_vgt = (ImageView) convertView.findViewById(R.id.img_vgt);
            ImageView img_vgn = (ImageView) convertView.findViewById(R.id.img_vgn);
            course_name.setText(course.getName());
            if(course.isSpicy()) img_hot.setVisibility(View.VISIBLE);
            if(course.isGlutenFree()) img_nogluten.setVisibility(View.VISIBLE);
            if(course.isVegetarian()) img_vgt.setVisibility(View.VISIBLE);
            if(course.isVegan()) img_vgn.setVisibility(View.VISIBLE);
            return convertView;
        }
    }

}

