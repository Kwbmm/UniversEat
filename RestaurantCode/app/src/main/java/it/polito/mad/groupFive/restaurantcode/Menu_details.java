package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.datastructures.*;
import it.polito.mad.groupFive.restaurantcode.datastructures.Option;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

/**
 * Created by Cristiano on 24/04/2016.
 */
public class Menu_details extends NavigationDrawer {

    private Restaurant restaurant;
    private it.polito.mad.groupFive.restaurantcode.datastructures.Menu menu;
    private SharedPreferences sharedPreferences;
    private int rid;
    private int uid;
    private int mid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences=this.getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        int uid=sharedPreferences.getInt("uid",-1);
        //TODO: retrieve rid and mid from intent
        //TODO: get restaurant by rid
        try {
            restaurant.getData();
            menu = restaurant.getMenuByID(mid);
        } catch (RestaurantException e) {
            e.printStackTrace();
        }
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
        ListView listView = (ListView) findViewById(R.id.lView);
        ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Option> options = menu.getOptions();
        ArrayList<Course> courses = menu.getCourses();
        OptionAdapter optionAdapter = new OptionAdapter(this,options);
        listView.setAdapter(optionAdapter);
    }

    public class OptionAdapter extends BaseAdapter {
        ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Option> options;
        Context context;
        public OptionAdapter(Context context,ArrayList<it.polito.mad.groupFive.restaurantcode.datastructures.Option> options){
            this.options=options;
            this.context=context;
        }

        @Override
        public int getCount() { return options.size(); }

        @Override
        public Object getItem(int position) {
            return options.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.menu_details_option, null);
            TextView option_number = (TextView) findViewById(R.id.option_number);
            TextView course_name = (TextView) findViewById(R.id.course_name);
            TextView course_description = (TextView) findViewById(R.id.course_description);
            ImageView course_image = (ImageView) findViewById(R.id.course_image);
            option_number.setText(position);
            Spinner spinner=(Spinner)findViewById(R.id.spinner2);
            //TODO: spinner.setAdapter(new SpinnerAdapter());
            int selected=spinner.getSelectedItemPosition();
            Option option = options.get(position);
            Course course = option.getCourses().get(selected);
            course_name.setText(course.getName());
            course_description.setText(course.getDescription());
            //TODO: set image

            return convertView;
        }
    }
}

