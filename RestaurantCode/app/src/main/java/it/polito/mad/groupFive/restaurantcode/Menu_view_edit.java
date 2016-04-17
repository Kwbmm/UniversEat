package it.polito.mad.groupFive.restaurantcode;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
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
    private ArrayList<Menu> menusshared;
    private ArrayList<Menu> motd;
    private MenuAdpter adp;
    private Restaurant rest;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readdata();
        showview();


    }
    public void readdata(){
        sharedPreferences=this.getSharedPreferences(getString(R.string.user_pref),this.MODE_PRIVATE);
        int rid,uid;
        uid=sharedPreferences.getInt("uid",-1);
        rid=sharedPreferences.getInt("rid",-1);
        try {

            rest = new User(this,rid,uid).getRestaurant();
            rest.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        menusshared = rest.getMenusByType(1);
        motd = rest.getMenusByType(2);


    }
    public void showview(){
        if(menusshared!=null||motd!=null){
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.menulist, mlay);
        if (menusshared!=null){
            adp=new MenuAdpter(this,menusshared);
            ListView lwcm = (ListView) findViewById(R.id.menu_lw);
            lwcm.setAdapter(adp);}
            if(motd!=null){
            TextView motd_name = (TextView) findViewById(R.id.name);
            motd_name.setText(motd.get(0).getName());
            TextView motd_desc = (TextView) findViewById(R.id.description);
            motd_desc.setText(motd.get(0).getDescription());
            TextView motd_price = (TextView) findViewById(R.id.price);
            motd_price.setText(motd.get(0).getPrice()+"€");
                TextView motd_title = (TextView) findViewById(R.id.title);
                motd_title.setText(R.string.motd_textViewTitle);}}
        else{
            FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
            mlay.inflate(this, R.layout.menulist, mlay);
            TextView motd_title = (TextView) findViewById(R.id.title);
            motd_title.setText("Your first menu");
        }






    }

    @Override
    protected void onResume() {
        super.onResume();
        readdata();
        showview();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
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
            title.setText(getString(R.string.menu_view_edit_fixed_price_menu));
            price.setText(menu.getPrice()+"€");
            description.setText(menu.getDescription());
            Button delete = (Button) convertView.findViewById(R.id.mlf_del);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Menu_view_edit.this);
                    final CharSequence[] items = {getString(R.string.menu_view_edit_yes), getString(R.string.menu_view_edit_no)};
                    dialog.setTitle(getString(R.string.menu_view_edit_delete));
                    dialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                remove(position);
                            }
                        }
                    });
                    dialog.show();
                }});





            return convertView;


        }

            public void remove(int position) {
                menusshared.remove(position);
                if(motd!=null){
                rest.getMenus().addAll(motd);}
                try {
                    rest.setMenus(menusshared);
                    rest.saveData();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                showview();

            }
    }

}


