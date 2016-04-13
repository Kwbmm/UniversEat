package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.zip.Inflater;

import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CourseException;

/**
 * Created by MacBookRetina on 12/04/16.
 */
public class Menu_view_edit extends NavigationDrawer {
    private ArrayList<Menu> menus;
    private  ArrayList<Menu> motd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.menulist, mlay);
        try {
            Restaurant rest = new User(this, 2, 2).getRestaurant();
            rest.getData();
            menus = rest.getMenus();
            motd = rest.getMenusByType(2);

            Log.v("dim",String.valueOf(menus.size()));
            MenuAdpter adp=new MenuAdpter(this,menus);
            ListView lwcm = (ListView) findViewById(R.id.menu_lw);
            lwcm.setAdapter(adp);

            TextView motd_name = (TextView) findViewById(R.id.name);
            motd_name.setText(motd.get(0).getName());
            TextView motd_desc = (TextView) findViewById(R.id.description);
            motd_desc.setText(motd.get(0).getDescription());
            TextView motd_price = (TextView) findViewById(R.id.price);
            motd_price.setText(motd.get(0).getPrice()+"€");

        }catch (Exception e){

        }


    }
    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nfpm: {
                        Intent intent = new Intent(getBaseContext(), Create_menu.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.ndm: {
                        Intent intent = new Intent(getBaseContext(), Create_menu.class);
                        startActivity(intent);

                        break;

                    }
                }
                return true;
            }
        });
        popup.inflate(R.menu.popup);
        popup.show();
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_add, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.add_ab) {
            showMenu(findViewById(R.id.add_ab));
        }
        return true;
    }

        public class MenuAdpter extends BaseAdapter{
        ArrayList<Menu> menus;
        Context context;
        public MenuAdpter(Context context,ArrayList<Menu> menus){
            this.menus=menus;
            this.context=context;
        }

        @Override
        public int getCount() {
            return menus.size();
        }

        @Override
        public Object getItem(int position) {
            return menus.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView=LayoutInflater.from(context).inflate(R.layout.menu_list_item_edit_fragment,null);
            TextView title= (TextView) convertView.findViewById(R.id.m_title);
            Menu menu= (Menu) getItem(position);
            TextView name =(TextView) convertView.findViewById(R.id.m_name);
            TextView price = (TextView)convertView.findViewById(R.id.m_price);
            TextView description=(TextView) convertView.findViewById(R.id.m_descrip);
            try {
                menu.getData();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (CourseException e) {
                e.printStackTrace();
            }
            name.setText(menu.getName());
            title.setText("Fixed Price Menu");
            price.setText(menu.getPrice()+"€");
            description.setText(menu.getDescription());





            return convertView;


        }
    }

}


